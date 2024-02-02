package com.team1.paymentsystem.mappers.entity;

import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.OrderNumber;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.managers.response.ErrorType;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.repositories.AccountRepository;
import com.team1.paymentsystem.repositories.OrderNumberRepository;
import com.team1.paymentsystem.repositories.PaymentRepository;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.PaymentStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class PaymentMapper implements Mapper<PaymentDTO, Payment> {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    OrderNumberRepository orderNumberRepository;

    @Override
    public PaymentDTO toDTO(Payment entity) {
        PaymentDTO paymentDTO = new PaymentDTO();
        BeanUtils.copyProperties(entity, paymentDTO);
        paymentDTO.setCreditAccountNumber(entity.getCreditAccount().getAccountNumber());
        paymentDTO.setDebitAccountNumber(entity.getDebitAccount().getAccountNumber());
        List<PaymentStatus> approveStatuses = List.of(PaymentStatus.APPROVE, PaymentStatus.AUTHORIZE,
                PaymentStatus.VERIFY, PaymentStatus.REPAIR);
        paymentDTO.setNeedsApproval(approveStatuses.contains(paymentDTO.getStatus()));
        paymentDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return paymentDTO;
    }

    @Override
    public Payment toEntity(PaymentDTO dto, Operation operation) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(dto, payment);
        Payment db = paymentRepository.findBySystemReference(dto.getSystemReference()).orElse(null);

        if(operation == Operation.CREATE || operation == Operation.CREATE_MOBILE){
            if(db != null) return null;
            if(dto.getCreditAccountNumber() == null || dto.getDebitAccountNumber() == null){
                return null;
            }
            Account creditAccount = accountRepository.findByAccountNumber(dto.getCreditAccountNumber()).orElse(null);
            Account debitAccount = accountRepository.findByAccountNumber(dto.getDebitAccountNumber()).orElse(null);

            payment.setCreditAccount(creditAccount);
            payment.setDebitAccount(debitAccount);
            if(dto.getTimeStamp() == null) payment.setTimeStamp(LocalDateTime.now());
            payment.setSystemReference(generateSystemReference(payment));
            payment.setNeededApproval(payment.getAmount() >= payment.getCurrency().getApproveThreshold());
        }
        else { // use database
            if(db == null) return null;

            payment.setId(db.getId());
            payment.setVersion(db.getVersion());
            if(payment.getCurrency() == null) payment.setCurrency(db.getCurrency());

            payment.setDebitAccount(db.getDebitAccount());
            payment.setCreditAccount(db.getCreditAccount());
            if(payment.getAmount() == null){
                payment.setAmount(db.getAmount());
            }

            if(payment.getUserReference() == null || payment.getUserReference().isEmpty())  payment.setUserReference(db.getUserReference());
            if(payment.getTimeStamp() == null) payment.setTimeStamp(db.getTimeStamp());
            if(payment.getStatus() == null) payment.setStatus(db.getStatus());

            if(payment.getLongitude() == null || payment.getLatitude() == null) {
                payment.setLongitude(db.getLongitude());
                payment.setLatitude(db.getLatitude());
            }
            if(payment.getNeededApproval() == null) {
                payment.setNeededApproval(db.getNeededApproval());
            }
        }

/*       if(payment.getTimeStamp() == null && db.getTimeStamp() == null){
            payment.setTimeStamp(LocalDateTime.now());
        }
        else */
       /* if(payment.getSystemReference() == null || payment.getSystemReference().equals("")){
            payment.setSystemReference(generateSystemReference(payment));
        }*/
        /*
        if(payment.getNeededApproval() == null){
            payment.setNeededApproval(db.getNeededApproval());
            if(payment.getNeededApproval() == null){
                payment.setNeededApproval(payment.getAmount() >= payment.getCurrency().getApproveThreshold());
            }
        }*/
        return payment;
    }

    public List<ErrorInfo> preValidate(PaymentDTO dto){
        List<ErrorInfo> errorInfos = new LinkedList<>();
        if(dto.getCreditAccountNumber() != null){
            Account creditAccount = accountRepository.findByAccountNumber(dto.getCreditAccountNumber()).orElse(null);
            Account debitAccount = accountRepository.findByAccountNumber(dto.getDebitAccountNumber()).orElse(null);
            if(creditAccount == null || debitAccount == null){
                errorInfos.add(new ErrorInfo(ErrorType.VALIDATION_ERROR, "Account does not exist"));
                return errorInfos;
            }
        }
        return errorInfos;
    }

    public synchronized String generateSystemReference(Payment payment){
        String systemReference = "";
        systemReference += LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String debitAccountNumber = payment.getDebitAccount().getAccountNumber();
        String creditAccountNumber = payment.getCreditAccount().getAccountNumber();

        if(debitAccountNumber != null && creditAccountNumber != null){
            systemReference += debitAccountNumber.substring(3,8);
            systemReference += creditAccountNumber.substring(3,8);
        }
        else {
            systemReference += "00000";
            systemReference += "00000";
        }

        Long nextOrderNumber = getNextOrderNumber();
        String formattedOrderNumber = String.format("%04d", nextOrderNumber);
        systemReference += formattedOrderNumber;
        return systemReference;
    }

    public synchronized Long getNextOrderNumber(){
        OrderNumber maybeOrderNumber = orderNumberRepository.findByDate(LocalDate.now()).orElse(null);
        if(maybeOrderNumber == null){
            OrderNumber orderNumber = new OrderNumber();
            orderNumber.setDate(LocalDate.now());
            orderNumber.setNumber(1L);
            orderNumberRepository.save(orderNumber);
            return orderNumber.getNumber();
        }
        else {
            Long number = maybeOrderNumber.getNumber();
            maybeOrderNumber.setNumber(number + 1L);
            orderNumberRepository.save(maybeOrderNumber);
            return number + 1L;
        }
    }
}
