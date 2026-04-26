package com.smpp.smscsim.spring;

public interface ResponseMessageIdGenerator {

	String getNextMessageId(String systemId);

}