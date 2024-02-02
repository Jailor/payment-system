package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.mappers.entity.PaymentMapper;
import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.AuditRepository;
import com.team1.paymentsystem.repositories.PaymentRepository;
import com.team1.paymentsystem.services.validation.PaymentValidator;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.PaymentStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService{
    @Autowired
    ApplicationContext context;
    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected AuditRepository auditRepository;
    @Autowired
    protected PaymentMapper paymentMapper;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected PaymentValidator paymentValidator;

    @Override
    public Payment save(Payment obj) {
        Payment fromDB = paymentRepository.findBySystemReference(obj.getSystemReference()).orElse(null);
        if (fromDB != null) {
            return null;
        }
        return paymentRepository.save(obj);
    }

    @Override
    public Payment update(Payment obj) {
        Payment fromDB = paymentRepository.findBySystemReference(obj.getSystemReference()).orElse(null);
        if (fromDB == null) {
            return null;
        }
        long id = fromDB.getId();
        long version = fromDB.getVersion();
        BeanUtils.copyProperties(obj, fromDB);
        fromDB.setId(id);
        fromDB.setVersion(version);
        return paymentRepository.save(fromDB);
    }

    @Override
    public Payment remove(Payment obj) {
        if(obj == null) return null;
        paymentRepository.delete(obj);
        return obj;
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAllOrdered();
    }
    @Override
    public List<Payment> findAllCompleted() {
        return paymentRepository.findAllOrderedCompleted();
    }

    @Override
    public List<Payment> findUserPayment(String username) {
        UserService userService = context.getBean(UserService.class);
        User user = userService.findByUsername(username);
        String email = user.getEmail();
        return paymentRepository.findByEmail(email);
    }

    @Override
    public Optional<Payment> findLastPaymentByDebit(String debitAccountNumber) {
        return paymentRepository.findLastByDebitAccount(debitAccountNumber);
    }

    @Override
    public Long medianPaymentValueCredit(String creditAccountNumber) {
        List<Payment> payments = paymentRepository.findByCreditAccountNumberOrderByAmountCompleted(creditAccountNumber);
        return findMedianValue(payments);
    }

    @Override
    public Long medianPaymentValueDebit(String debitAccountNumber) {
        List<Payment> payments = paymentRepository.findByDebitAccountNumberOrderByAmountCompleted(debitAccountNumber);
        return findMedianValue(payments);
    }



    private Long findMedianValue(List<Payment> payments){
        Long median = null;
        if(payments.size() != 0){
            if(payments.size() % 2 == 0){
                median = (payments.get(payments.size()/2).getAmount() + payments.get(payments.size()/2-1).getAmount())/2;
            }else{
                median = payments.get(payments.size()/2).getAmount();
            }
        }
        return median;
    }

    @Override
    public Payment findById(long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public Payment findByDiscriminator(Payment obj) {
        return paymentRepository.findBySystemReference(obj.getSystemReference()).orElse(null);
    }
    @Override
    public Payment toEntity(PaymentDTO paymentDTO, Operation operation) {
        return paymentMapper.toEntity(paymentDTO, operation);
    }

    @Override
    public PaymentDTO toDTO(Payment entity) {
        return paymentMapper.toDTO(entity);
    }

    @Override
    public List<Payment> findNeedsApproval() {
        List<PaymentStatus> statuses = List.of(PaymentStatus.REPAIR, PaymentStatus.VERIFY,
                PaymentStatus.APPROVE, PaymentStatus.AUTHORIZE, PaymentStatus.BLOCKED_BY_FRAUD);
        return paymentRepository.findByStatuses(statuses);
    }

    @Override
    public List<Payment> findFraud() {
        List<PaymentStatus> statuses = List.of(PaymentStatus.BLOCKED_BY_FRAUD);
        return paymentRepository.findByStatuses(statuses);
    }


    @Override
    public List<Payment> findNeedsApprovalByAccount(String accountNumber) {
        List<PaymentStatus> statuses = List.of(PaymentStatus.REPAIR, PaymentStatus.VERIFY,
                PaymentStatus.APPROVE, PaymentStatus.AUTHORIZE, PaymentStatus.BLOCKED_BY_FRAUD);
        return paymentRepository.findByStatusesAndAccount(accountNumber, statuses);
    }
    @Override
    public List<Payment> findFraudByAccount(String accountNumber) {
        List<PaymentStatus> statuses = List.of(PaymentStatus.BLOCKED_BY_FRAUD);
        return paymentRepository.findByStatusesAndDebitAccount(accountNumber, statuses);
    }


    @Override
    public Payment makeCopy(Payment obj) {
        Payment copy = new Payment();
        BeanUtils.copyProperties(obj, copy);
        return copy;
    }

    @Override
    public List<ErrorInfo> validate(Payment obj, Operation operation) {
        return paymentValidator.validate(obj, operation);
    }

    @Override
    public int getEyes(Payment obj){
        List<Audit> audits = auditRepository.findUsersWhichApprovedCheck(obj.getId(),"PAYMENT");
        return (audits.size() + 1) * 2;
    }

    @Override
    public boolean fourEyesCheck(Payment obj, String username, int nEyesCheck) {
        int limit = nEyesCheck/2-1;
        List<Audit> audits = auditRepository.findUsersWhichApprovedCheck(obj.getId(),"PAYMENT");
        if(audits.size() > limit){
            audits = audits.subList(0,limit);
        }
        for(Audit audit : audits){
            if(audit.getUser().getUsername().equals(username)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Payment findBySystemReference(String systemReference) {
        return paymentRepository.findBySystemReference(systemReference).orElse(null);
    }

    @Override
    public List<Payment> findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        return paymentRepository.findByCreditAccountOrDebitAccount(account);
    }

    @Override
    public List<Payment> findByDebitAccountNumberOrderedTimestampCompleted(String debitAccountNumber) {
        return paymentRepository.findByDebitAccountNumberOrderByTimeStampCompleted(debitAccountNumber);
    }
}
