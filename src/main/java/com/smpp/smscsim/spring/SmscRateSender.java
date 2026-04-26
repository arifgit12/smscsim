package com.smpp.smscsim.spring;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.smpp.smscsim.spring.message.MessageFactory;

/**
 * Sends Deliver pdu's at specific rate
 **/
@ManagedResource(objectName = "smscsim:name=SmscRateSender")
public class SmscRateSender extends BaseSender {

	private ScheduledThreadPoolExecutor sendScheduler;

	private int schedulerPoolSize = Runtime.getRuntime().availableProcessors();

	private MessageFactory messageFactory;

	@ManagedOperation
	public void sendAtRate(final int ratePerSecond) throws Exception {
		startWithRate(ratePerSecond);
	}

	@ManagedOperation
	public void stop() {
		resetSendScheduler();
	}

	private void startWithRate(final int ratePerSecond) {
		if (ratePerSecond <= 0) {
			logger.warn("Invalid rate");
			return;
		}

		resetSendScheduler();

		Runnable runnable = () -> {
			try {
				send(getMessageFactory().createMessage());
			} catch (Exception ex) {
				logger.warn("Failed to send message", ex);
			}
		};

		long delay = 1000000 / ratePerSecond;
		sendScheduler.scheduleAtFixedRate(runnable, 0, delay, TimeUnit.MICROSECONDS);
	}

	private void resetSendScheduler() {
		if (sendScheduler != null && !sendScheduler.isTerminating()) {
			// Cancel scheduled but not started task, and avoid new ones
			sendScheduler.shutdown();

			// Wait for the running tasks
			try {
				sendScheduler.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				logger.warn("Interupt during awating termination", ex);
			}

			// Interrupt the threads and shutdown the scheduler
			sendScheduler.shutdownNow();
		}
		sendScheduler = new ScheduledThreadPoolExecutor(schedulerPoolSize);
	}

	public int getSchedulerPoolSize() {
		return schedulerPoolSize;
	}

	public void setSchedulerPoolSize(int schedulerPoolSize) {
		this.schedulerPoolSize = schedulerPoolSize;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
}
