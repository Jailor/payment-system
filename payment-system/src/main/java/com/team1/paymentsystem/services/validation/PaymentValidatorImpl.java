package com.team1.paymentsystem.services.validation;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.PaymentRepository;
import com.team1.paymentsystem.services.entities.BalanceService;
import com.team1.paymentsystem.services.entities.ExchangeRateService;
import com.team1.paymentsystem.states.Operation;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Log
public class PaymentValidatorImpl implements PaymentValidator{
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    ApplicationContext context;
    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<ErrorInfo> validate(Payment obj, Operation operation) {

        if(operation.equals(Operation.CREATE) || operation.equals(Operation.REPAIR)){
            List<ErrorInfo> errors = new LinkedList<>();

            if(!hasEnoughDebitBalance(obj)){
                //errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Not enough money on debit account"));
                log.severe("Not enough money on debit account, but transaction can potentially proceed");
            }
            String debitAccountNumber = obj.getDebitAccount().getAccountNumber();
            String creditAccountNumber = obj.getCreditAccount().getAccountNumber();
            Account debitAccount = accountRepository.findByAccountNumber(debitAccountNumber).orElse(null);
            Account creditAccount = accountRepository.findByAccountNumber(creditAccountNumber).orElse(null);
            if(debitAccount == null){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Debit account does not exist"));
                return errors;
            }
            if(creditAccount == null){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Credit account does not exist"));
                return errors;
            }
            if(operation.equals(Operation.CREATE)){
                // check uniqueness of system reference
                Payment payment = paymentRepository.findBySystemReference(obj.getSystemReference()).orElse(null);
                if(payment != null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                            "Payment with this system reference already exists"));
                    return errors;
                }
            }
            else if(operation.equals(Operation.REPAIR)){
                // check existence of payment
                Payment payment = paymentRepository.findBySystemReference(obj.getSystemReference()).orElse(null);
                if(payment == null){
                    errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR,
                            "Payment with this reference does not exist"));
                    return errors;
                }
            }

            // check amount
            if(obj.getAmount() == null){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Amount is not specified"));
                return errors;
            }
            if(obj.getAmount() <= 0){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Amount must be positive"));
                return errors;
            }
            if(obj.getCreditAccount().getAccountNumber().equals(obj.getDebitAccount().getAccountNumber())){
                errors.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Debit and credit accounts must be different"));
                return errors;
            }
            return errors;
        }
        else {
            return new LinkedList<>();
        }
    }

    @Override
    public boolean hasEnoughDebitBalance(Payment payment){
        Account debitAccount = payment.getDebitAccount();

        BalanceService balanceService = context.getBean(BalanceService.class);
        Balance debitBalance = balanceService.findLastBalanceByAccount(debitAccount);
        // Account creditAccount = obj.getCreditAccount();
        // Balance creditBalance = balanceService.findLastBalanceByAccount(creditAccount);
        // debit account - account FROM which the cash is moved
        // credit account - account INTO which the cash is moved
        long debitAvailableBalance = balanceService.getAvailableBalance(debitBalance);
        ExchangeRateService exchangeRateService = context.getBean(ExchangeRateService.class);
        long paymentAmount = exchangeRateService
                .getPaymentAmountInDestinationCurrency(payment, debitAccount);
        return debitAvailableBalance >= paymentAmount;
    }
}
