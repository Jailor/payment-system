package com.team1.paymentsystem.mappers.approve;

import com.team1.paymentsystem.dto.approval.AccountApproveDTO;
import com.team1.paymentsystem.entities.common.AbstractAccount;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class AccountApproveMapper implements ApproveMapper<AbstractAccount, AccountApproveDTO> {
    @Override
    public AccountApproveDTO toDTO(AbstractAccount oldEntity, AbstractAccount newEntity, Operation operation) {
        AccountApproveDTO accountApproveDTO = new AccountApproveDTO();
        BeanUtils.copyProperties(oldEntity,accountApproveDTO);
        accountApproveDTO.setOwnerEmail(oldEntity.getOwner().getEmail());

        accountApproveDTO.setNewCurrency(newEntity.getCurrency());
        accountApproveDTO.setNewAccountStatus(newEntity.getAccountStatus());
        accountApproveDTO.setNewStatus(newEntity.getStatus());
        accountApproveDTO.setOperation(operation);
        accountApproveDTO.setNeedsApproval(true);
        return accountApproveDTO;
    }
}
