package com.team1.paymentsystem.states;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.team1.paymentsystem.states.ApplicationConstants.currencies;

@Getter
@Setter
public class Currency extends AbstractState{
    private int fractionDigits;
    private long approveThreshold;

    @JsonCreator
    public Currency(@JsonProperty("name")  String name,
                    @JsonProperty("fractionDigits")  int fractionDigits,
                    @JsonProperty("approveThreshold")  long approveThreshold) {
        super(name);
        this.fractionDigits = fractionDigits;
        this.approveThreshold = approveThreshold;
    }
    public Currency(String name, int fractionDigits) {
        super(name);
        this.fractionDigits = fractionDigits;
        Currency maybeCurrency = getCurrency(name);
        if(maybeCurrency != null)
            this.approveThreshold = maybeCurrency.approveThreshold;
    }
    @JsonCreator
    public Currency(String name){
        super(name);
        Currency maybeCurrency = getCurrency(name);
        if(maybeCurrency != null){
            this.fractionDigits = maybeCurrency.fractionDigits;
            this.approveThreshold = maybeCurrency.approveThreshold;
        }
    }
    public static Currency EUR;
    public static Currency RON;
    public static Currency USD;

    public static Currency getCurrency(String currency){
        return switch (currency) {
            case "EUR" -> EUR;
            case "RON" -> RON;
            case "USD" -> USD;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return "Currency{" +
                "fractionDigits=" + fractionDigits +
                ", approveThreshold=" + approveThreshold +
                ", name='" + name + '\'' +
                '}';
    }
}
