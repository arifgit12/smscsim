package com.smpp.smscsim.spring.auto;

import com.smpp.smscsim.spring.DeliveryReceiptScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smpp.smscsim.spring.ResponseMessageIdGenerator;

@Component
public class SmscGlobalConfiguration {

	@Autowired
	private DelayedRequestSenderImpl deliverSender;

	@Autowired
	private ResponseMessageIdGenerator messageIdGenerator;

	@Autowired
	private SmppSessionManager sessionManager;

    @Autowired
    private DeliveryReceiptScheduler deliveryReceiptScheduler;

	public DelayedRequestSenderImpl getDeliverSender() {
		return deliverSender;
	}

	public void setDeliverSender(DelayedRequestSenderImpl deliverSender) {
		this.deliverSender = deliverSender;
	}

	public ResponseMessageIdGenerator getMessageIdGenerator() {
		return messageIdGenerator;
	}

	public void setMessageIdGenerator(ResponseMessageIdGenerator messageIdGenerator) {
		this.messageIdGenerator = messageIdGenerator;
	}

	public SmppSessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SmppSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

    public DeliveryReceiptScheduler getDeliveryReceiptScheduler() {
        return deliveryReceiptScheduler;
    }

    public void setDeliveryReceiptScheduler(DeliveryReceiptScheduler deliveryReceiptScheduler) {
        this.deliveryReceiptScheduler = deliveryReceiptScheduler;
    }
}
