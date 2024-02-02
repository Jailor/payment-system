package com.team1.paymentsystem.managers;

import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.dto.filter.PaymentFilterDTO;
import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.managers.common.AuthorizationManager;
import com.team1.paymentsystem.managers.common.ManagerUtils;
import com.team1.paymentsystem.managers.common.OperationManager;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.mappers.entity.PaymentMapper;
import com.team1.paymentsystem.services.FilterService;
import com.team1.paymentsystem.services.FraudPreventionService;
import com.team1.paymentsystem.services.entities.*;
import com.team1.paymentsystem.services.validation.PaymentValidator;
import com.team1.paymentsystem.states.*;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.java.Log;
import org.hibernate.StaleStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


@Component
@Transactional(rollbackFor = Exception.class)
@Log
public class PaymentManager {
    @Autowired
    protected ApplicationContext context;
    @Autowired
    protected ManagerUtils managerUtils;
    @Autowired
    protected AuthorizationManager authorizationManager;
    @Autowired
    protected OperationManager operationManager;
    @Autowired
    protected FraudPreventionService fraudPreventionService;
    @Autowired
    protected ExchangeRateService exchangeRateService;


    /**
     * Manages a payment operation from start to finish, handling the conversion to entity and back to DTO.
     * The operation is performed in a transactional context.
     * @param paymentDTO the DTO of the payment to be created
     * @param username the username of the user performing the operation, used for authentication
     * @param operation the operation to be performed
     * @return the response of the operation, with the data object set to the DTO of the payment
     */
    public OperationResponse managePaymentOperation(PaymentDTO paymentDTO,
                                                    Operation operation, String username){
        PaymentMapper mapper = context.getBean(PaymentMapper.class);
        OperationResponse response = new OperationResponse();
        response.addErrors(mapper.preValidate(paymentDTO));
        if(!response.isValid()) return response;

        Payment mock = mapper.toEntity(paymentDTO, operation);
        if(mock == null){
            response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                    "sent could not be mapped to an entity. Please check the data sent."));
        }
        if(mock != null){
            List<Status> invalidStatuses = List.of(Status.BLOCKED,Status.APPROVE,Status.REMOVED);
            Status creditAccountStatus = mock.getCreditAccount().getStatus();
            Status debitAccountStatus = mock.getDebitAccount().getStatus();
            if(invalidStatuses.contains(creditAccountStatus) || invalidStatuses.contains(debitAccountStatus)){
                response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR, "The status of the account is invalid for transfer"));
            }
        }

        if(response.isValid()){
            try {
                response = paymentOperation(mock, username, operation);
            }
            catch (StaleStateException | OptimisticLockException e){
                response = new OperationResponse();
                response.addError(new ErrorInfo(ErrorType.VERSIONING_ERROR,
                        "The object has been modified by another user. Please refresh the page and try again."));
            }

            if(response != null && response.isValid()){
                SystemDTO backDTO = mapper.toDTO((Payment) response.getObject());
                if(backDTO==null){
                    response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                            "sent could not be mapped to a DTO. Please check the data sent."));
                }
                response.setDataObject(backDTO);
            }
        }

        return response;

    }

    /**
     * Manages the operation of a payment, authorizing the user and validating the payment data in the process.
     * @param payment the payment to be operated on
     * @param username the username of the user performing the operation, used for authentication
     * @param operation the operation to be performed
     * @return an OperationResponse object containing the result of the operation and the list of associated errors
     */
    private OperationResponse paymentOperation(Payment payment, String username, Operation operation){
        OperationResponse operationResponse = authorizationManager.authorizeProfile(payment, username, operation);
        if (!operationResponse.isValid()) return operationResponse;
        PaymentService paymentService = context.getBean(PaymentService.class);
        operationResponse.addErrors(paymentService.validate(payment, operation));

        if (operationResponse.isValid()) {
            Payment payload = processPayment(payment, operation, username, operationResponse);
            if (payload != null && operationResponse.isValid()) {
                managerUtils.insertHistoryObj(payload);
                operationManager.createAudit(payload, username, operation);
                operationResponse.setDataObject(payload);
            }
        }
        return operationResponse;
    }


    /**
     * Processes a given payment, according to operation
     * @return the payment payload if successful, null otherwise
     */
    private Payment processPayment(Payment payment, Operation operation, String username, OperationResponse response) {;
        Payment payload = switch (operation) {
            case CREATE -> processCreate(payment, response);
            case CREATE_MOBILE->{
                Payment payment1 = processCreate(payment, response);
                if(response.isValid()){
                    payment1 = processVerify(payment, response, username);
                }
                yield payment1;
            }
            case VERIFY -> processVerify(payment, response, username);
            case APPROVE -> processApprove(payment, response, username);
            case AUTHORIZE -> processAuthorize(payment, response, username);
            case CANCEL -> processCancel(payment, response, username);
            case REPAIR -> processRepair(payment, response, username);
            case UNBLOCK_FRAUD -> processUnblockFraud(payment, response, username);
            default -> null;
        };

        return payload;
    }

    /**
     * Creates a payment. If the payment is valid, it is saved and the
     * balances are updated accordingly.
     * @param payment the payment to be created
     * @return the created payment if successful, null otherwise
     */
    private Payment processCreate(Payment payment, OperationResponse response) {
        payment.setStatus(PaymentStatus.VERIFY);
        PaymentService paymentService = context.getBean(PaymentService.class);
        Payment payload = paymentService.save(payment);
        if(payload == null){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Error saving the object: the object already exists"));
        }
        Account debitAccount = payment.getDebitAccount();
        Account creditAccount = payment.getCreditAccount();

        // check account status validity
        List<AccountStatus> validStatusDebit = List.of(AccountStatus.OPEN, AccountStatus.BLOCKED_CREDIT);
        List<AccountStatus> validStatusCredit = List.of(AccountStatus.OPEN, AccountStatus.BLOCKED_DEBIT);
        if(!validStatusDebit.contains(debitAccount.getAccountStatus())){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Debit account is not in a valid status: " + debitAccount.getAccountStatus()));
        }
        if(!validStatusCredit.contains(creditAccount.getAccountStatus())){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Credit account is not in a valid status: " + creditAccount.getAccountStatus()));
        }
        if(response.isValid()){
            createBalanceUpdate(payment);
            if(payload != null){
                exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
                exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
            }
        }

        return payload;
    }

    /**
     * Verifies the given payment, if the payment is valid, it is set conditionally to
     * APPROVE, AUTHORIZE or COMPLETED status.
     * @param payment the payment to be verified
     * @return the payment object if the operation was successful, null otherwise
     */
    private Payment processVerify(Payment payment, OperationResponse response, String username) {
        List<PaymentStatus> validStatuses = List.of(PaymentStatus.VERIFY, PaymentStatus.REPAIR);
        PaymentService paymentService = context.getBean(PaymentService.class);
        PaymentValidator paymentValidator = context.getBean(PaymentValidator.class);
        Payment payload = null;

        if(!validStatuses.contains(payment.getStatus())){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be verified from this status" + payment.getStatus()));
            return null;
        }
        managerUtils.nEyesCheckPayment(payment, username, response);
        if(response.isValid()){
            if(payment.getAmount() >= payment.getCurrency().getApproveThreshold()){
                payment.setStatus(PaymentStatus.APPROVE);
            }
            else if(!paymentValidator.hasEnoughDebitBalance(payment)){
                payment.setStatus(PaymentStatus.AUTHORIZE);
            }
            else
            {
              OperationResponse fraudResponse = fraudPreventionService.checkFraud(payment);
              if(fraudResponse.isValid()){
                    payment.setStatus(PaymentStatus.COMPLETED);
                    balanceUpdate(payment, true);
              }
              else {
                  payment.setStatus(PaymentStatus.BLOCKED_BY_FRAUD);
              }
            }
            payload = paymentService.update(payment);
            if(payload.getStatus().equals(PaymentStatus.COMPLETED)){
                exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
                exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
            }
        }
        return payload;
    }


    /**
     * Approves a payment. The payment must be in status APPROVE.
     * @param payment payment to be approved
     * @return the payment object, with the status updated in case of success.
     */
    private Payment processApprove(Payment payment, OperationResponse response, String username){
        PaymentService paymentService = context.getBean(PaymentService.class);
        PaymentValidator paymentValidator = context.getBean(PaymentValidator.class);
        Payment payload = null;
        if(!payment.getStatus().equals(PaymentStatus.APPROVE)){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be verified from this status " + payment.getStatus()));
        }
        managerUtils.nEyesCheckPayment(payment, username, response);
        if(response.isValid()){
            if(!paymentValidator.hasEnoughDebitBalance(payment)){
                payment.setStatus(PaymentStatus.AUTHORIZE);
            }
            else
            {
                OperationResponse fraudResponse = fraudPreventionService.checkFraud(payment);
                if(fraudResponse.isValid()){
                    payment.setStatus(PaymentStatus.COMPLETED);
                    balanceUpdate(payment, true);
                }
                else {
                    payment.setStatus(PaymentStatus.BLOCKED_BY_FRAUD);
                }
            }
            payload = paymentService.update(payment);
            if(payload.getStatus().equals(PaymentStatus.COMPLETED)){
                exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
                exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
            }
        }
        return payload;
    }

    /**
     * Authorizes a payment. The payment must be in status AUTHORIZE.
     * @param payment payment to be authorized
     * @return the payment object, with the status updated in case of success.
     */
    private Payment processAuthorize(Payment payment, OperationResponse response, String username){
        PaymentService paymentService = context.getBean(PaymentService.class);
        Payment payload = null;
        if(!payment.getStatus().equals(PaymentStatus.AUTHORIZE)){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be verified from this status " + payment.getStatus()));
        }
        managerUtils.nEyesCheckPayment(payment, username, response);
        if(response.isValid()){
            OperationResponse fraudResponse = fraudPreventionService.checkFraud(payment);
            if(fraudResponse.isValid()){
                payment.setStatus(PaymentStatus.COMPLETED);
                balanceUpdate(payment, true);
            }
            else {
                payment.setStatus(PaymentStatus.BLOCKED_BY_FRAUD);
            }
            payload = paymentService.update(payment);
            if(payload.getStatus().equals(PaymentStatus.COMPLETED)){
                exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
                exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
            }
        }

        return payload;
    }

    /**
     * Repairs a payment with a new one given as parameter,
     * validating the status, its existence and the n-eyes check. The payment must be in VERIFY status.
     * @param payment payment information to update
     * @return the repaired payment with status updated in case of success.
     */
    private Payment processRepair(Payment payment, OperationResponse response, String username){
        PaymentService paymentService = context.getBean(PaymentService.class);
        Payment existingPayment = paymentService.findBySystemReference(payment.getSystemReference());
        Payment payload = null;
        if(existingPayment == null){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be repaired: no payment with this system reference"));
        }
        if(existingPayment != null && !existingPayment.getStatus().equals(PaymentStatus.VERIFY)){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be repaired from this status. Status: " + existingPayment.getStatus()));
        }

        managerUtils.nEyesCheckPayment(payment, username, response);
        if(response.isValid()){
            balanceUpdate(existingPayment, false);
            createBalanceUpdate(payment);
            payment.setStatus(PaymentStatus.REPAIR);
            payload =  paymentService.update(payment);
            exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
            exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
        }
        return payload;
    }

    /**
     * Unblocks a payment that was blocked by the fraud prevention service.
     * @param payment payment to be unblocked. If not in BLOCKED_BY_FRAUD status, the operation is not performed and
     *                a validation error is added to the response.
     * @return the payment object, with the status updated in case of success.
     */
    private Payment processUnblockFraud(Payment payment, OperationResponse response, String username){
        PaymentService paymentService = context.getBean(PaymentService.class);
        Payment payload = null;
        if(!payment.getStatus().equals(PaymentStatus.BLOCKED_BY_FRAUD)){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be unblocked for fraud from this status " + payment.getStatus()));
        }
        managerUtils.nEyesCheckPayment(payment, username, response);
        if(response.isValid()){
            payment.setStatus(PaymentStatus.COMPLETED);
            balanceUpdate(payment, true);
            payload = paymentService.update(payment);
            exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
            exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
        }

        return payload;
    }

    /**
     * Cancels a payment. The payment is validated and the pending balance is updated.
     * Customers can only cancel their own payments.
     * @param payment payment to be canceled
     * @return the updated payment object with the status updated in case of success.
     */
    private Payment processCancel(Payment payment, OperationResponse response, String username) {
        List<PaymentStatus> validStatuses = List.of(PaymentStatus.VERIFY, PaymentStatus.APPROVE,
                PaymentStatus.REPAIR, PaymentStatus.AUTHORIZE, PaymentStatus.BLOCKED_BY_FRAUD);
        Payment payload = null;
        if (!validStatuses.contains(payment.getStatus())) {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Payment cannot be canceled from this status" + payment.getStatus()));
        }
        if(response.isValid()){
            PaymentService paymentService = context.getBean(PaymentService.class);
            UserService userService = context.getBean(UserService.class);
            User user = userService.findByUsername(username);
            // validate customer
            if(user.getProfile().getProfileType().equals(ProfileType.CUSTOMER)){
                AccountService accountService = context.getBean(AccountService.class);
                List<Account> userAccounts = accountService.findByEmail(user.getEmail());
                if(!userAccounts.contains(payment.getDebitAccount()) && !userAccounts.contains(payment.getCreditAccount())){
                    response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                            "User is not owner of any of the accounts in this payment"));
                }
            }
            if(response.isValid()){
                payment.setStatus(PaymentStatus.CANCELLED);
                balanceUpdate(payment, false);
                payload = paymentService.update(payment);
                exchangeRateService.saveExchangeRateHistory(payload,payload.getCreditAccount());
                exchangeRateService.saveExchangeRateHistory(payload,payload.getDebitAccount());
            }
        }
        return payload;
    }



    /**
     * Creates the required balance entities for the given payment and inserts them into the database.
     * The balances are inserted at the current timestamp when the method is called. Pending amounts
     * are updated either way, actual amounts are only updated if the payment was successful.
     * @param payment payment to be updated
     * @param successful true if payment was successful, false otherwise
     */
    private void balanceUpdate(Payment payment, boolean successful){
        BalanceService balanceService = context.getBean(BalanceService.class);

        Account debitAccount = payment.getDebitAccount();
        Account creditAccount = payment.getCreditAccount();

        Balance debitBalance = balanceService.findLastBalanceByAccount(debitAccount);
        Balance creditBalance = balanceService.findLastBalanceByAccount(creditAccount);
        Balance insertDebitBalance = new Balance(debitBalance);
        Balance insertCreditBalance = new Balance(creditBalance);

        Long debitAmount = exchangeRateService
                .getPaymentAmountInDestinationCurrency(payment, debitAccount);
        insertDebitBalance.setPendingDebitAmount(debitBalance.getPendingDebitAmount() - debitAmount);
        insertDebitBalance.setPendingDebitCount(debitBalance.getPendingDebitCount() - 1);

        Long creditAmount = exchangeRateService
                .getPaymentAmountInDestinationCurrency(payment, creditAccount);
        insertCreditBalance.setPendingCreditAmount(creditBalance.getPendingCreditAmount() - creditAmount);
        insertCreditBalance.setPendingCreditCount(creditBalance.getPendingCreditCount() - 1);

        if(successful){
            insertDebitBalance.setAvailableDebitAmount(debitBalance.getAvailableDebitAmount() + debitAmount);
            insertDebitBalance.setAvailableDebitCount(debitBalance.getAvailableDebitCount() + 1);
            insertCreditBalance.setAvailableCreditAmount(creditBalance.getAvailableCreditAmount() + creditAmount);
            insertCreditBalance.setAvailableCreditCount(creditBalance.getAvailableCreditCount() + 1);
        }

        saveBalances(insertDebitBalance, insertCreditBalance, balanceService);
    }

    /**
     * Creates the required balance entities for the given payment and inserts them into the database.
     * The balances are inserted at the current timestamp when the method is called. Only the pending
     * amounts and counts are updated.
     * @param payment payment to be executed
     */
    private void createBalanceUpdate(Payment payment){
        BalanceService balanceService = context.getBean(BalanceService.class);

        Account debitAccount = payment.getDebitAccount();
        Account creditAccount = payment.getCreditAccount();

        Balance debitBalance = balanceService.findLastBalanceByAccount(debitAccount);
        Balance creditBalance = balanceService.findLastBalanceByAccount(creditAccount);
        Balance insertDebitBalance = new Balance(debitBalance);
        Balance insertCreditBalance = new Balance(creditBalance);

        Long debitAmount = exchangeRateService
                .getPaymentAmountInDestinationCurrency(payment, debitAccount);
        insertDebitBalance.setPendingDebitAmount(debitBalance.getPendingDebitAmount() + debitAmount);
        insertDebitBalance.setPendingDebitCount(debitBalance.getPendingDebitCount() + 1);

        Long creditAmount = exchangeRateService
                .getPaymentAmountInDestinationCurrency(payment, creditAccount);
        insertCreditBalance.setPendingCreditAmount(creditBalance.getPendingCreditAmount() + creditAmount);
        insertCreditBalance.setPendingCreditCount(creditBalance.getPendingCreditCount() + 1);

        saveBalances(insertDebitBalance, insertCreditBalance, balanceService);

    }

    private void saveBalances(Balance insertDebitBalance, Balance insertCreditBalance, BalanceService balanceService){
        insertDebitBalance.setTimeStamp(LocalDateTime.now());
        insertCreditBalance.setTimeStamp(LocalDateTime.now());
        // save the new balances
        balanceService.save(insertDebitBalance);
        balanceService.save(insertCreditBalance);
    }

    public OperationResponse findAll(String username){
        OperationManager operationManager = context.getBean(OperationManager.class);
        OperationResponse response = operationManager.findAll(new Payment(), username);
        if (response.isValid()) {
            List<Payment> paymentList = (List<Payment>) response.getObject();
            List<PaymentDTO> dtos = new LinkedList<>();
            for (Payment p : paymentList) {
                dtos.add((PaymentDTO) managerUtils.toDTO(p));
            }
            response.setDataObject(dtos);
        }
        return response;
    }
    public OperationResponse findUserPayments(String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());

        if (response.isValid()) {
            List<Payment> paymentList = paymentService.findUserPayment(username);
            List<PaymentDTO> dtos = new LinkedList<>();
            for (Payment p : paymentList) {
                dtos.add((PaymentDTO) managerUtils.toDTO(p));
            }
            response.setDataObject(dtos);
        }
        return response;
    }

    public OperationResponse findAllCompleted(String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if (response.isValid()) {
            PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
            List<Payment> paymentList = paymentService.findAllCompleted();
            List<PaymentDTO> dtos = new LinkedList<>();
            for (Payment p : paymentList) {
                dtos.add((PaymentDTO) managerUtils.toDTO(p));
            }
            response.setDataObject(dtos);
        }
        return response;
    }

    public OperationResponse findBySystemReference(String systemReference, String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
        try {
            Payment payment = paymentService.findBySystemReference(systemReference);
            if (payment != null) {
                PaymentDTO paymentDTO = (PaymentDTO) managerUtils.toDTO(payment);
                response.setDataObject(paymentDTO);
            } else {
                response.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR, "Payment not found"));
            }
        } catch (Exception e) {
            response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, e.getMessage()));
        }
        return response;
    }

    public OperationResponse findAllApprovalPayments(String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if(response.isValid()){
            PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
            List<PaymentDTO> paymentDTOS = new LinkedList<>();
            paymentService.findNeedsApproval().forEach(payment -> paymentDTOS.add((PaymentDTO) managerUtils.toDTO(payment)));
            response.setDataObject(paymentDTOS);
        }
        return response;
    }

    public OperationResponse findAllFraudPayments(String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if(response.isValid()){
            PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
            List<PaymentDTO> paymentDTOS = new LinkedList<>();
            paymentService.findFraud().forEach(payment -> paymentDTOS.add((PaymentDTO) managerUtils.toDTO(payment)));
            response.setDataObject(paymentDTOS);
        }
        return response;
    }

    public OperationResponse findAllApprovalPaymentsByAccount(String accountNumber, String username) {
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if(response.isValid()){
            PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
            List<PaymentDTO> paymentDTOS = new LinkedList<>();
            paymentService.findNeedsApprovalByAccount(accountNumber)
                    .forEach(payment -> paymentDTOS.add((PaymentDTO) managerUtils.toDTO(payment)));
            response.setDataObject(paymentDTOS);
        }
        return response;
    }

    public OperationResponse findAllFraudPaymentsByAccount(String accountNumber, String username) {
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if(response.isValid()){
            PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
            List<PaymentDTO> paymentDTOS = new LinkedList<>();
            paymentService.findFraudByAccount(accountNumber)
                    .forEach(payment -> paymentDTOS.add((PaymentDTO) managerUtils.toDTO(payment)));
            response.setDataObject(paymentDTOS);
        }
        return response;
    }

    public OperationResponse findByAccountNumber(String accountNumber, String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if(response.isValid()){
            PaymentService paymentService = (PaymentService) managerUtils.getService(new Payment());
            List<PaymentDTO> paymentDTOS = new LinkedList<>();
            paymentService.findByAccountNumber(accountNumber)
                    .forEach(payment -> paymentDTOS.add((PaymentDTO) managerUtils.toDTO(payment)));
            response.setDataObject(paymentDTOS);
        }
        return response;
    }

    public OperationResponse findFilteredPayments(PaymentFilterDTO paymentFilterDTO, String username){
        AuthorizationManager authorizationManager = context.getBean(AuthorizationManager.class);
        OperationResponse response = authorizationManager.authorizeProfile(new Payment(), username, Operation.LIST);
        if(response.isValid()){
            FilterService filterService = context.getBean(FilterService.class);
            List<PaymentDTO> filteredPaymentsDto= filterService.findFilteredPayments(paymentFilterDTO);
            response.setDataObject(filteredPaymentsDto);
        }
        return response;
    }


}
