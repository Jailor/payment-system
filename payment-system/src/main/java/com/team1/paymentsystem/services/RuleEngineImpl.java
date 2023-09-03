package com.team1.paymentsystem.services;

import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.services.entities.PaymentService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


@Service
@Log
public class RuleEngineImpl implements RuleEngine{
    @Autowired
    PaymentService paymentService;
    private static final int TRANSACTION_COUNT_THRESHOLD_FREQUENCY = 3;
    private static final long TIME_WINDOW_SECONDS = 60;
    private static final int HOURS_THRESHOLD = 8;

    @Override
    public boolean checkFraud(Payment payment) {
        List<Payment> pastPayments = paymentService
                .findByDebitAccountNumberOrderedTimestampCompleted(payment.getDebitAccount().getAccountNumber());
        boolean result = false;
        if(pastPayments.size() > 0){
            result = checkUnderApproval(pastPayments, payment) ||
                    checkTimeWindow(pastPayments, payment) ||
                    checkUnusualHours(pastPayments, payment);
        }
        return result;
    }

    private boolean checkUnderApproval(List<Payment> pastPayments, Payment currentPayment) {
        // check if at least 2 previous payment were within 5% less of the approval threshold
        // and that the current payment is within 5% less of the approval threshold
        int count = 0;
        for(Payment payment : pastPayments){
            if(payment.getNeededApproval()){
                break;
            }
            long approveThreshold = payment.getCurrency().getApproveThreshold();
            if(approveThreshold * 0.95 <= payment.getAmount() && payment.getAmount() < approveThreshold){
                count++;
            }
            else break;
        }

        long approveThreshold = currentPayment.getCurrency().getApproveThreshold();
        if(count >= 2 && approveThreshold * 0.95 <= currentPayment.getAmount()
                && currentPayment.getAmount() < approveThreshold){
            log.info("Fraud detected by being under the approval limit: " + currentPayment);
            return true;
        }
        return false;
    }

    private boolean checkTimeWindow(List<Payment> pastPayments, Payment currentPayment) {
        // Filter past payments within the specified time window
        List<Payment> recentPayments = filterPaymentsWithinTimeWindow(pastPayments, currentPayment);

        boolean fraud = false;
        // Check if the number of recent payments exceeds the threshold
        if (recentPayments.size() >= TRANSACTION_COUNT_THRESHOLD_FREQUENCY) {
            log.info("Fraud detected by unusually large amount of transactions: " + currentPayment);
            fraud = true; // Block the current transaction
        }
        return fraud;
    }

    private List<Payment> filterPaymentsWithinTimeWindow(List<Payment> pastPayments, Payment currentPayment) {
        List<Payment> recentPayments = new LinkedList<>();

        // Calculate the time limit for the time window
        LocalDateTime timeLimit  = currentPayment.getTimeStamp().minusSeconds(TIME_WINDOW_SECONDS);

        // Iterate through past payments and add those within the time window
        for (Payment payment : pastPayments) {
            if (payment.getTimeStamp().isAfter(timeLimit)) {
                recentPayments.add(payment);
            }
        }

        return recentPayments;
    }

    private boolean checkUnusualHours(List<Payment> pastPayments, Payment payment) {
        // Calculate the median transaction hour across all payments
        int medianHour = calculateMedianTransactionHour(pastPayments);

        // Get the transaction timestamp
        LocalDateTime paymentTimestamp = payment.getTimeStamp();

        // Calculate the hour of the current transaction
        int currentHour = paymentTimestamp.getHour();

        // Calculate the standard deviation of transaction hours
        double stdDeviation = calculateStandardDeviation(pastPayments);

        // Define the threshold for identifying unusual hours in regard to standard deviation
        double threshold = 3 * stdDeviation;


        int hourDifference = Math.min((currentHour - medianHour + 24) % 24, (medianHour - currentHour + 24) % 24);

        // return hourDifference > threshold;
        boolean fraud = hourDifference >= HOURS_THRESHOLD;
        if(fraud) log.info("Fraud detected by unusual hours: " + payment);
        return fraud;
    }


    private int calculateMedianTransactionHour(List<Payment> payments) {
        // Extract hours from transaction timestamps
        List<Integer> hours = payments.stream()
                .map(payment -> payment.getTimeStamp().getHour()).sorted().toList();

        // Calculate the median of the extracted hours
        int middle = hours.size() / 2;
        if (hours.size() % 2 == 1) {
            return hours.get(middle);
        } else {
            return (hours.get(middle - 1) + hours.get(middle)) / 2;
        }
    }

    private double calculateStandardDeviation(List<Payment> payments) {
        // Calculate the mean of transaction hours
        double mean = payments.stream()
                .mapToDouble(payment -> payment.getTimeStamp().getHour())
                .average()
                .orElse(0);

        // Calculate the squared differences from the mean
        double squaredDiffs = payments.stream()
                .mapToDouble(payment -> Math.pow(payment.getTimeStamp().getHour() - mean, 2))
                .sum();

        // Calculate the variance and standard deviation
        double variance = squaredDiffs / (payments.size() - 1);
        return Math.sqrt(variance);
    }


}


