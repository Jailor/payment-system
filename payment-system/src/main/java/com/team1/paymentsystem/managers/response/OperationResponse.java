package com.team1.paymentsystem.managers.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class OperationResponse {
    @Setter
    private Object dataObject;
    @Getter
    @Setter
    private List<ErrorInfo> errors;

    public OperationResponse(){
        this.dataObject = null;
        this.errors = new LinkedList<>();
    }
    public OperationResponse(Object dataObject){
        this.dataObject = dataObject;
        this.errors = new LinkedList<>();
    }
    public Object getObject(){
        if(errors.isEmpty()){
            return dataObject;
        }
        return null;
    }
    public boolean isValid(){
        return errors.isEmpty();
    }

    public void addError(ErrorInfo errorInfo){
        errors.add(errorInfo);
    }
    public void addErrors(List<ErrorInfo> errorInfos){
        errors.addAll(errorInfos);
    }
}
