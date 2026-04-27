package com.smpp.smscsim.service.auto;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.smpp.smscsim.server.DelayedRecord;
import com.smpp.smscsim.server.DelayedRequestSender;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.PduRequest;

@Component
public class DelayedRequestSenderImpl extends DelayedRequestSender<DelayedRecord> {

	private static final Logger logger = LoggerFactory.getLogger(DelayedRequestSenderImpl.class);

	@Autowired
	private SmppSessionManager sessionManager;

	private long sendTimoutMilis = 1000;

	public DelayedRequestSenderImpl(@Value("${smsc.delivery-sender.worker-count:4}") int workerCount) {
		super(workerCount);
	}

	@Override
	protected void handleDelayedRecord(DelayedRecord delayedRecord) throws Exception {
		SmppSession session = delayedRecord.getUsedSession(sessionManager);
		PduRequest request = delayedRecord.getRequest(sessionManager.getNextSequenceNumber());
		if (session != null && session.isBound()) {
			session.sendRequestPdu(request, sendTimoutMilis, false);
		} else {
			logger.info("Session does not exist or is not bound {}. Request not sent {}", session, request);
		}
	}

	public SmppSessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SmppSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@PostConstruct
	public void init() {
		start();
	}

	@PreDestroy
	public void cleanUp() {
		stop();
	}
}
