package com.smpp.smscsim.service.auto;

import java.time.Instant;
import java.util.UUID;

import com.smpp.smscsim.service.ResponseMessageIdGenerator;

/**
 * Generates unique message IDs in the format {systemId}-{epochSeconds}-{uuid8}.
 */
public class ResponseMessageIdGeneratorImpl implements ResponseMessageIdGenerator {

	@Override
	public String getNextMessageId(String systemId) {
		return systemId + "-" + Instant.now().getEpochSecond() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
	}

}
