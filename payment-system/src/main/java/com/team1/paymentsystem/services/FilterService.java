package com.team1.paymentsystem.services;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.filter.*;
import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.dto.user.UserDTO;

import java.util.List;

public interface FilterService {
    List<UserDTO> findFilteredUsers(UserFilterDTO userFilterDTO);
    List<ProfileDTO> findFilteredProfiles(ProfileFilterDTO userFilterDTO);
    List<AccountDTO> findFilteredAccounts(AccountFilterDTO accountFilterDTO);
    List<CustomerDTO> findFilteredCustomer(CustomerFilterDTO customerFilterDTO);
    List<PaymentDTO> findFilteredPayments(PaymentFilterDTO paymentFilterDTO);
}
