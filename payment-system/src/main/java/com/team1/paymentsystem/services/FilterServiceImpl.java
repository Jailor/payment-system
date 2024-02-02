package com.team1.paymentsystem.services;

import com.team1.paymentsystem.dto.account.AccountDTO;
import com.team1.paymentsystem.dto.customer.CustomerDTO;
import com.team1.paymentsystem.dto.filter.*;
import com.team1.paymentsystem.dto.payment.PaymentDTO;
import com.team1.paymentsystem.entities.*;
import com.team1.paymentsystem.mappers.entity.ProfileMapper;
import com.team1.paymentsystem.dto.profile.ProfileDTO;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.services.entities.*;
import com.team1.paymentsystem.states.ProfileRight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class FilterServiceImpl implements FilterService {
    @Autowired
    UserService userService;
    @Autowired
    ProfileService profileService;
    @Autowired
    AccountService accountService;
    @Autowired
    CustomerService customerService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    ProfileMapper profileMapper;

    @Override
    public List<UserDTO> findFilteredUsers(UserFilterDTO userFilterDTO) {
        List<User> users = userService.findAll();
        List<User> filteredUsers;
        Predicate<User> statusFilter = user -> userFilterDTO.getStatuses() == null || userFilterDTO.getStatuses().isEmpty() || userFilterDTO.getStatuses().contains(user.getStatus());
        Predicate<User> usernameFilter = user -> userFilterDTO.getUsernameFilter() == null || user.getUsername().contains(userFilterDTO.getUsernameFilter());
        Predicate<User> fullNameFilter = user -> userFilterDTO.getFullNameFilter() == null || user.getFullName().contains(userFilterDTO.getFullNameFilter());
        Predicate<User> emailFilter = user -> userFilterDTO.getEmailFilter() == null || user.getFullName().contains(userFilterDTO.getEmailFilter());
        Predicate<User> addressFilter = user -> userFilterDTO.getAddressFilter() == null || user.getAddress().contains(userFilterDTO.getAddressFilter());
        Predicate<User> combinedFilter = statusFilter.and(usernameFilter).and(fullNameFilter).and(emailFilter).and(addressFilter);
        filteredUsers = users.stream()
                .filter(combinedFilter)
                .toList();

        List<UserDTO> userDTOS = new LinkedList<>();
        for (User user : filteredUsers) {
            userDTOS.add(userService.toDTO(user));
        }
        return userDTOS;
    }

    @Override
    public List<ProfileDTO> findFilteredProfiles(ProfileFilterDTO profileFilterDTO) {
        List<Profile> profiles = profileService.findAll();
        List<Profile> filteredProfiles;
        Predicate<Profile> statusFiler = profile -> profileFilterDTO.getStatuses() == null || profileFilterDTO.getStatuses().isEmpty() || profileFilterDTO.getStatuses().contains(profile.getStatus());
        Predicate<Profile> typesFilter = profile -> profileFilterDTO.getTypes() == null || profileFilterDTO.getTypes().isEmpty() || profileFilterDTO.getTypes().contains(profile.getProfileType());
        Predicate<Profile> nameFilter = profile -> profileFilterDTO.getNameFilter() == null || profile.getName().contains(profileFilterDTO.getNameFilter());
        Predicate<Profile> rightsFilter = profile -> {
            if (profileFilterDTO.getRights() == null) {
                return true;
            }
            List<ProfileRight> rights = profileMapper.generateRightsList(profile.getRights());
            for(ProfileRight profileRight : profileFilterDTO.getRights()){
                if(rights.contains(profileRight)){
                    return true;
                }
            }
            return false;
        };
        Predicate<Profile> combinedFilter = statusFiler.and(typesFilter).and(nameFilter).and(rightsFilter);
        filteredProfiles = profiles.stream()
                .filter(combinedFilter)
                .toList();
        List<ProfileDTO> profileDTOS = new LinkedList<>();
        for (Profile profile : filteredProfiles) {
            profileDTOS.add(profileService.toDTO(profile));
        }
        return profileDTOS;
    }

    @Override
    public List<AccountDTO> findFilteredAccounts(AccountFilterDTO accountFilterDTO) {
        List<Account> accounts = accountService.findAll();
        List<Account> filteredAccount;
        Predicate<Account> statusFilter  = account -> accountFilterDTO.getStatuses() == null || accountFilterDTO.getStatuses().isEmpty() || accountFilterDTO.getStatuses().contains(account.getStatus());
        Predicate<Account> currencyFilter = account -> accountFilterDTO.getCurrencyFilter() == null || accountFilterDTO.getCurrencyFilter().isEmpty() || accountFilterDTO.getCurrencyFilter().contains(account.getCurrency());
        Predicate<Account> accountStatusFilter = account -> accountFilterDTO.getAccountStatusFilter() == null || accountFilterDTO.getAccountStatusFilter().isEmpty() || accountFilterDTO.getAccountStatusFilter().contains(account.getAccountStatus());
        Predicate<Account> ownerFilter = account -> accountFilterDTO.getOwnerEmail() == null || account.getOwner().getEmail().contains(accountFilterDTO.getOwnerEmail());
        Predicate<Account> combinedFilter = statusFilter.and(currencyFilter).and(accountStatusFilter).and(ownerFilter);
        filteredAccount = accounts.stream()
                .filter(combinedFilter)
                .toList();
        List<AccountDTO> accountDTOS = new LinkedList<>();
        for(Account account : filteredAccount){
            accountDTOS.add(accountService.toDTO(account));
        }
        return accountDTOS;
    }

    @Override
    public List<CustomerDTO> findFilteredCustomer(CustomerFilterDTO customerFilterDTO) {
        List<Customer> customers = customerService.findAll();
        List<Customer> filteredCustomer;
        Predicate<Customer> statusFilter = customer -> customerFilterDTO.getStatuses() == null || customerFilterDTO.getStatuses().isEmpty() || customerFilterDTO.getStatuses().contains(customer.getStatus());
        Predicate<Customer> nameFilter = customer -> customerFilterDTO.getNameFilter() == null || customer.getName().contains(customerFilterDTO.getNameFilter());
        Predicate<Customer> phoneNumberFilter = customer -> customerFilterDTO.getPhoneNumberFilter() == null || customer.getPhoneNumber().contains(customerFilterDTO.getPhoneNumberFilter());
        Predicate<Customer> addressFilter = customer -> customerFilterDTO.getAddressFilter() == null || customer.getAddress().contains(customerFilterDTO.getAddressFilter());
        Predicate<Customer> emailFilter = customer -> customerFilterDTO.getEmailFilter() == null || customer.getEmail().contains(customerFilterDTO.getEmailFilter());
        Predicate<Customer> combinedFilter = statusFilter.and(nameFilter).and(phoneNumberFilter).and(addressFilter).and(emailFilter);
        filteredCustomer = customers.stream()
                .filter(combinedFilter)
                .toList();
        List<CustomerDTO> customerDTOS = new LinkedList<>();
        for(Customer customer : filteredCustomer){
            customerDTOS.add(customerService.toDTO(customer));
        }
        return customerDTOS;
     }
    @Override
    public List<PaymentDTO> findFilteredPayments(PaymentFilterDTO paymentFilterDTO){
        List<Payment> payments = paymentService.findAll();
        List<Payment> filteredPayment;
        Predicate<Payment> statusFilter = payment -> paymentFilterDTO.getStatuses() == null  || paymentFilterDTO.getStatuses().contains(payment.getStatus());
        Predicate<Payment> currencyFilter = payment -> paymentFilterDTO.getCurrencies() == null  || paymentFilterDTO.getCurrencies().contains(payment.getCurrency());
        Predicate<Payment> systemReferenceFilter = payment -> paymentFilterDTO.getSystemReferenceFilter() == null || payment.getSystemReference().contains(paymentFilterDTO.getSystemReferenceFilter());
        Predicate<Payment> combinedFilter = statusFilter.and(currencyFilter).and(systemReferenceFilter);
        filteredPayment = payments.stream()
                .filter(combinedFilter)
                .toList();
        List<PaymentDTO> paymentDTOS = new LinkedList<>();
        for(Payment payment : filteredPayment){
            paymentDTOS.add(paymentService.toDTO(payment));
        }
        return paymentDTOS;
    }


}
