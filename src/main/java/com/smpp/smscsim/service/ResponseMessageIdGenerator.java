package com.smpp.smscsim.service;

public interface ResponseMessageIdGenerator {

	String getNextMessageId(String systemId);

}