package com.team1.paymentsystem.managers;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.dto.common.SystemDTO;
import com.team1.paymentsystem.dto.filter.AccountFilterDTO;
import com.team1.paymentsystem.entities.*;
import com.team1.paymentsystem.entities.history.AccountHistory;
import com.team1.paymentsystem.entities.history.CustomerHistory;
import com.team1.paymentsystem.managers.common.ManagerUtils;
import com.team1.paymentsystem.managers.common.OperationManager;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.managers.response.OperationResponse;
import com.team1.paymentsystem.mappers.entity.AccountMapper;
import com.team1.paymentsystem.mappers.entity.BalanceMapper;
import com.team1.paymentsystem.repositories.ExchangeHistoryRepository;
import com.team1.paymentsystem.services.FilterService;
import com.team1.paymentsystem.services.entities.AccountService;
import com.team1.paymentsystem.services.entities.BalanceService;
import com.team1.paymentsystem.services.entities.CustomerService;
import com.team1.paymentsystem.services.entities.ExchangeRateService;
import com.team1.paymentsystem.services.entities.history.CustomerHistoryService;
import com.team1.paymentsystem.states.AccountStatus;
import com.team1.paymentsystem.states.Currency;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.Status;
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
public class AccountManager extends OperationManager<Account, AccountDTO> {
    @Autowired
    ApplicationContext context;

    @Override
    public OperationResponse approve(Account statusObject, String username) {
        AccountService service = context.getBean(AccountService.class);
        Currency beforeCurrency = service.findByAccountNumber((statusObject).getAccountNumber()).getCurrency();

        OperationResponse response =  super.approve(statusObject, username);
        Account after = (Account) response.getObject();

        if(response.isValid() && !beforeCurrency.getName().equals(after.getCurrency().getName())){
            // convert balance from old currency to new currency
            BalanceService balanceService = context.getBean(BalanceService.class);
            ExchangeRateService exchangeRateService = context.getBean(ExchangeRateService.class);
            ExchangeRate exchangeRate = exchangeRateService
                    .findActiveExchangeSourceDestination(beforeCurrency, after.getCurrency());
            Balance lastBalance = balanceService.findLastBalanceByAccount(after);
            Balance insertBalance = new Balance(lastBalance);
            insertBalance.setAvailableDebitAmount((long) (lastBalance.getAvailableDebitAmount() * exchangeRate.getRatio()));
            insertBalance.setAvailableCreditAmount((long) (lastBalance.getAvailableCreditAmount() * exchangeRate.getRatio()));
            insertBalance.setPendingCreditAmount((long) (lastBalance.getPendingCreditAmount() * exchangeRate.getRatio()));
            insertBalance.setPendingDebitAmount((long) (lastBalance.getPendingDebitAmount() * exchangeRate.getRatio()));

            insertBalance.setTimeStamp(LocalDateTime.now());
            insertBalance.setAccount(after);
            balanceService.save(insertBalance);

            // insert exchange history
            ExchangeHistory exchangeHistory = new ExchangeHistory();
            exchangeHistory.setExchangeRate(exchangeRate);
            exchangeHistory.setDestinationAccount(after);
            exchangeHistory.setTimeStamp(LocalDateTime.now());
            ExchangeHistoryRepository exchangeHistoryRepository = context.getBean(ExchangeHistoryRepository.class);
            exchangeHistoryRepository.save(exchangeHistory);
        }
        return response;
    }

    @Override
    public OperationResponse manageOperation(AccountDTO systemDTO, Operation operation, String username) {
        List<Operation> accountStatusOperations = List.of(Operation.BLOCK, Operation.BLOCK_CREDIT, Operation.BLOCK_DEBIT,
                Operation.UNBLOCK, Operation.UNBLOCK_CREDIT, Operation.UNBLOCK_DEBIT, Operation.CLOSE);
        if(accountStatusOperations.contains(operation)){
            return manageAccountStatusOperation(systemDTO, operation, username);
        } else {
            return super.manageOperation(systemDTO, operation, username);
        }
    }



    /**
     * Manages the operation of updating the status of an account,handling the conversion to entity and back to DTO,
     * authorizing with username and validating the payment data in the process.
     * @param accountDTO the account to be operated on
     * @param username the username of the user performing the operation, used for authentication
     * @param operation the operation to be performed
     * @return an OperationResponse object containing the result of the operation and the list of associated errors
     */
    public OperationResponse manageAccountStatusOperation(AccountDTO accountDTO,
                                                    Operation operation, String username){
        AccountMapper mapper = context.getBean(AccountMapper.class);
        Account account = mapper.toEntity(accountDTO, operation);
        if(account == null){
            OperationResponse response = new OperationResponse();
            response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                    "sent could not be mapped to an entity. Please check the data sent."));
            return response;
        }

        OperationResponse response;
        try {
            response = accountStatusUpdate(account, username, operation);
        }
        catch (StaleStateException | OptimisticLockException e){
            response = new OperationResponse();
            response.addError(new ErrorInfo(ErrorType.VERSIONING_ERROR,
                    "The object has been modified by another user. Please refresh the page and try again."));
            return response;
        }

        if(response.isValid()){
            SystemDTO backDTO = mapper.toDTO((Account) response.getObject());
            if(backDTO==null){
                response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, "The data" +
                        "sent could not be mapped to a DTO. Please check the data sent."));
            }
            response.setDataObject(backDTO);
        }

        return response;

    }


    /**
     * Manages the operation of an account status, authorizing the user and validating the data in the process.
     * @param username the username of the user performing the operation, used for authentication
     * @param operation the operation to be performed
     * @return an OperationResponse object containing the result of the operation and the list of associated errors
     */
    private OperationResponse accountStatusUpdate(Account account, String username, Operation operation) {
        OperationResponse operationResponse = authorize(account, username, operation);
        AccountService accountService = context.getBean(AccountService.class);
        Account returnObject = accountService.findByDiscriminator(account);

        if(returnObject == null) {
            operationResponse.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR,
                    "Object has not been found  (by discriminator)"));
        }
        if(returnObject != null && returnObject.getNextStateId() != null){
            operationResponse.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Entity is already undergoing another modification"));
        }
        if(operationResponse.isValid()) {
            Account copyObject = accountService.makeCopy(returnObject);
            // copy properties to insert into history
            returnObject.setStatus(Status.APPROVE);
            returnObject.setNextStatus(Status.ACTIVE);
            processAccountStatusOperation(returnObject, operation, operationResponse);
            if(operationResponse.isValid()){
                ManagerUtils managerUtils = context.getBean(ManagerUtils.class);
                AccountHistory historyObj = (AccountHistory) managerUtils.insertHistoryObj(returnObject);
                // add nextStateId
                copyObject.setNextStateId(historyObj.getId());
                copyObject.setNextStatus(Status.ACTIVE);

                // update and audit
                copyObject = accountService.update(copyObject);
                createAudit(copyObject, username, operation);

                operationResponse.setDataObject(copyObject);
            }
        }
        return operationResponse;
    }

    /**
     * Processes the given account status operations, by delegating to the appropriate methods.
     * In case the starting status is not correct for the given operation, an error is added to the response.
     * @param account - the account whose status is to be changed (the object given as parameter is modified)
     * @param operation - the operation to be performed
     * @param response - the operation response, with errors if any
     */
    private void processAccountStatusOperation(Account account, Operation operation, OperationResponse response) {
        switch (operation) {
            case CLOSE -> processCloseOperation(account, response);
            case BLOCK -> processBlockOperation(account, response);
            case BLOCK_CREDIT-> processBlockCreditOperation(account, response);
            case BLOCK_DEBIT -> processBlockDebitOperation(account, response);
            case UNBLOCK -> processUnblockOperation(account, response);
            case UNBLOCK_CREDIT -> processUnblockCreditOperation(account, response);
            case UNBLOCK_DEBIT -> processUnblockDebitOperation(account, response);
            default -> {
                response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                        "Operation not supported: " + operation));
            }
        }
    }

    private void processCloseOperation(Account returnObject, OperationResponse response) {
        if (!returnObject.getAccountStatus().equals(AccountStatus.CLOSED)) {
            returnObject.setAccountStatus(AccountStatus.CLOSED);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account is already closed"));
        }
    }

    private void processBlockOperation(Account returnObject, OperationResponse response) {
        if (returnObject.getAccountStatus().equals(AccountStatus.OPEN)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account cannot be blocked from this status: " + returnObject.getAccountStatus()));
        }
    }

    private void processBlockCreditOperation(Account returnObject, OperationResponse response) {
        if (returnObject.getAccountStatus().equals(AccountStatus.OPEN)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED_CREDIT);
        } else if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED_DEBIT)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account cannot be blocked credit from this status: " + returnObject.getAccountStatus()));
        }
    }

    private void processBlockDebitOperation(Account returnObject, OperationResponse response) {
        if (returnObject.getAccountStatus().equals(AccountStatus.OPEN)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED_DEBIT);
        } else if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED_CREDIT)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account cannot be blocked debit from this status: " + returnObject.getAccountStatus()));
        }
    }

    private void processUnblockOperation(Account returnObject, OperationResponse response) {
        if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED)) {
            returnObject.setAccountStatus(AccountStatus.OPEN);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account cannot be unblocked from this status: " + returnObject.getAccountStatus()));
        }
    }

    private void processUnblockCreditOperation(Account returnObject, OperationResponse response) {
        if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED_CREDIT)) {
            returnObject.setAccountStatus(AccountStatus.OPEN);
        } else if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED_DEBIT);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account cannot be unblocked credit from this status: " + returnObject.getAccountStatus()));
        }
    }

    private void processUnblockDebitOperation(Account returnObject, OperationResponse response) {
        if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED_DEBIT)) {
            returnObject.setAccountStatus(AccountStatus.OPEN);
        } else if (returnObject.getAccountStatus().equals(AccountStatus.BLOCKED)) {
            returnObject.setAccountStatus(AccountStatus.BLOCKED_CREDIT);
        } else {
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Account cannot be unblocked debit from this status: " + returnObject.getAccountStatus()));
        }
    }

    @Override
    public OperationResponse save(Account obj, String username) {
        OperationResponse response = super.save(obj, username);
        if(response.isValid()){
            BalanceMapper balanceMapper = context.getBean(BalanceMapper.class);
            Account account = (Account) response.getObject();
            Balance balance = balanceMapper.toEntity(account);
            BalanceService balanceService = context.getBean(BalanceService.class);
            balanceService.save(balance);
            AccountService accountService = context.getBean(AccountService.class);
            Customer owner = account.getOwner();
            List<Account> accounts = accountService.findByEmail(owner.getEmail());
            // if its the first account created, set it as default
            if(accounts.size() == 1){
                owner.setDefaultAccountNumber(account.getAccountNumber());
                CustomerHistoryService customerHistoryService = context.getBean(CustomerHistoryService.class);
                CustomerService customerService = context.getBean(CustomerService.class);
                Customer customerRet = customerService.update(owner);
                CustomerHistory customerHistory = customerHistoryService.createHistory(customerRet);
                customerHistoryService.save(customerHistory);

            }
        }
        return response;
    }

    @Override
    public OperationResponse remove(Account obj, String username) {
        OperationResponse response = new OperationResponse();
        Account account = obj;
        if(!account.getAccountStatus().equals(AccountStatus.CLOSED)){
            response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                    "Cannot remove account that is not closed"));
        }
        if(response.isValid()){

                if(account.getOwner().getDefaultAccountNumber()!=null && account.getAccountNumber().equals(account.getOwner().getDefaultAccountNumber())){
                    response.addError(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Can't remove a default account."));
                }else{
                    response = super.remove(account, username);
                }
        }
        return response;
    }

    @Override
    public OperationResponse reject(Account statusObject, String username) {
        OperationResponse response = super.reject(statusObject, username);
        if(response.isValid()){
            Account account = (Account) response.getObject();
            AccountService accountService = context.getBean(AccountService.class);
            account.setAccountStatus(AccountStatus.CLOSED);
            accountService.update(account);
        }
        return response;
    }

    public OperationResponse findByEmail(String email, String username) {
        OperationResponse response = authorize(new Account(), username, Operation.LIST);
        if(response.isValid()){
            AccountService accountService = context.getBean(AccountService.class);
            List<Account> accounts = accountService.findByEmail(email);
            List<AccountDTO> dtos = new LinkedList<>();
            for (Account a : accounts) {
                dtos.add(toDTO(a));
            }
            response.setDataObject(dtos);
        }
        return response;
    }

    public OperationResponse findByAccountNumber(String accountNumber, String username) {
        OperationResponse response = authorize(new Account(), username, Operation.LIST);
        AccountService accountService = context.getBean(AccountService.class);

        if(response.isValid()){
            try {
                Account account = accountService.findByAccountNumber(accountNumber);
                if (account != null) {
                    AccountDTO accountDTO = toDTO(account);
                    response.setDataObject(accountDTO);
                } else {
                    response.addError(new ErrorInfo(ErrorType.NOT_FOUND_ERROR, "Account not found"));
                }
            } catch (Exception e) {
                response.addError(new ErrorInfo(ErrorType.INTERNAL_ERROR, e.getMessage()));
            }
        }
        return response;
    }

    public OperationResponse findFilteredAccounts(AccountFilterDTO accountFilterDTO, String username) {
        OperationResponse response = authorize(new Account(), username, Operation.LIST);
        if(response.isValid()){
            FilterService filterService = context.getBean(FilterService.class);
            List<AccountDTO> filteredAccountsDto = filterService.findFilteredAccounts(accountFilterDTO);
            response.setDataObject(filteredAccountsDto);
        }
        return response;
    }
}
