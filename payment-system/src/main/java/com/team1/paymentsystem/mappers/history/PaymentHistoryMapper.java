package com.team1.paymentsystem.mappers.history;

import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.dto.payment.PaymentHistoryDTO;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.entities.history.PaymentHistory;
import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.mappers.entity.PaymentMapper;
import com.team1.paymentsystem.states.Operation;
import com.team1.paymentsystem.states.PaymentStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentHistoryMapper  implements Mapper<PaymentHistoryDTO, PaymentHistory> {
    @Autowired
    PaymentMapper paymentMapper;

    @Override
    public PaymentHistoryDTO toDTO(PaymentHistory entity) {
        PaymentHistoryDTO paymentHistoryDTO = new PaymentHistoryDTO();
        BeanUtils.copyProperties(entity,paymentHistoryDTO);
        paymentHistoryDTO.setCreditAccountNumber(entity.getCreditAccount().getAccountNumber());
        paymentHistoryDTO.setDebitAccountNumber(entity.getDebitAccount().getAccountNumber());
        List<PaymentStatus> approveStatuses = List.of(PaymentStatus.APPROVE, PaymentStatus.AUTHORIZE,
                PaymentStatus.VERIFY, PaymentStatus.REPAIR);
        paymentHistoryDTO.setNeedsApproval(approveStatuses.contains(paymentHistoryDTO.getStatus()));
        paymentHistoryDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        paymentHistoryDTO.setStringHistoryTimeStamp(entity.getHistoryTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return paymentHistoryDTO;
    }

    @Override
    public PaymentHistory toEntity(PaymentHistoryDTO dto, Operation operation) {
        PaymentDTO paymentDTO = new PaymentDTO();
        BeanUtils.copyProperties(dto,paymentDTO);
        Payment payment = paymentMapper.toEntity(paymentDTO, operation);
        PaymentHistory paymentHistory = new PaymentHistory();
        BeanUtils.copyProperties(dto,paymentHistory);
        BeanUtils.copyProperties(payment,paymentHistory);
        paymentHistory.setId(0);
        paymentHistory.setOriginalId(payment.getId());
        if(paymentHistory.getTimeStamp() == null){
            paymentHistory.setTimeStamp(payment.getTimeStamp());
        }
        if (paymentHistory.getHistoryTimeStamp() == null){
            paymentHistory.setHistoryTimeStamp(payment.getTimeStamp());
        }
        return paymentHistory;
    }
}
