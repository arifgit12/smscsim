package com.smpp.smscsim.spring.auto;

import com.smpp.smscsim.spring.DeliveryReceiptScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Mavo on 2.8.2014.
 */
@Component
public class RandomDeliveryReceiptScheduler implements DeliveryReceiptScheduler {

    @Value("${smsc.delivery-receipt.min-delay-ms:5000}")
    private int minDelayMs = 5000;

    @Value("${smsc.delivery-receipt.random-delta-ms:5000}")
    private int randomDeltaMs =  5000;

    @Override
    public long getDeliveryTimeMillis() {
        return System.currentTimeMillis() + minDelayMs + (int) (ThreadLocalRandom.current().nextDouble() * randomDeltaMs);
    }

    public int getMinDelayMs() {
        return minDelayMs;
    }

    public void setMinDelayMs(int minDelayMs) {
        this.minDelayMs = minDelayMs;
    }

    public int getRandomDeltaMs() {
        return randomDeltaMs;
    }

    public void setRandomDeltaMs(int randomDeltaMs) {
        this.randomDeltaMs = randomDeltaMs;
    }
}
