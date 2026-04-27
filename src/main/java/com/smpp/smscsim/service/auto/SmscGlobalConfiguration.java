package com.smpp.smscsim.service.auto;

import com.smpp.smscsim.service.DeliveryReceiptScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.smpp.smscsim.service.ResponseMessageIdGenerator;

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

	@Value("${smsc.auth.system-id:stct}")
	private String systemId;

	@Value("${smsc.auth.password:stct1234}")
	private String password;

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

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
