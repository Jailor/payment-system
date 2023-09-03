package com.team1.paymentsystem.dto.json;

import com.team1.paymentsystem.states.ProfileRight;

import java.util.List;

public class MaximalSetData {
    private List<ProfileRight> ADMINISTRATOR;
    private List<ProfileRight> EMPLOYEE;
    private List<ProfileRight> CUSTOMER;

    public List<ProfileRight> getAdministratorRights() {
        return ADMINISTRATOR;
    }

    public void setAdministratorRights(List<ProfileRight> ADMINISTRATOR) {
        this.ADMINISTRATOR = ADMINISTRATOR;
    }

    public List<ProfileRight> getEmployeeRights() {
        return EMPLOYEE;
    }

    public void setEmployeeRights(List<ProfileRight> EMPLOYEE) {
        this.EMPLOYEE = EMPLOYEE;
    }

    public List<ProfileRight> getCustomerRights() {
        return CUSTOMER;
    }

    public void setCustomerRights(List<ProfileRight> CUSTOMER) {
        this.CUSTOMER = CUSTOMER;
    }
}
