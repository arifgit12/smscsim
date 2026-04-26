package com.smpp.smscsim.config;

import com.cloudhopper.smpp.SmppServerConfiguration;
import com.smpp.smscsim.spring.SmscRateSender;
import com.smpp.smscsim.spring.auto.ResponseMessageIdGeneratorImpl;
import com.smpp.smscsim.spring.auto.SmppSessionManager;
import com.smpp.smscsim.spring.message.DeliverSegmentedMessageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class SmscConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SmppServerConfiguration smppServerConfiguration(
            @Value("${smsc.server.port:10001}") int port,
            @Value("${smsc.server.max-connection-size:10}") int maxConnectionSize,
            @Value("${smsc.server.non-blocking-sockets-enabled:true}") boolean nonBlockingSocketsEnabled,
            @Value("${smsc.server.default-request-expiry-timeout:30000}") long defaultRequestExpiryTimeout,
            @Value("${smsc.server.default-window-monitor-interval:15000}") long defaultWindowMonitorInterval,
            @Value("${smsc.server.default-window-size:100}") int defaultWindowSize,
            @Value("${smsc.server.default-window-wait-timeout:30000}") long defaultWindowWaitTimeout,
            @Value("${smsc.server.default-session-counters-enabled:true}") boolean defaultSessionCountersEnabled,
            @Value("${smsc.server.jmx-enabled:true}") boolean jmxEnabled) {
        SmppServerConfiguration config = new SmppServerConfiguration();
        config.setPort(port);
        config.setMaxConnectionSize(maxConnectionSize);
        config.setNonBlockingSocketsEnabled(nonBlockingSocketsEnabled);
        config.setDefaultRequestExpiryTimeout(defaultRequestExpiryTimeout);
        config.setDefaultWindowMonitorInterval(defaultWindowMonitorInterval);
        config.setDefaultWindowSize(defaultWindowSize);
        config.setDefaultWindowWaitTimeout(defaultWindowWaitTimeout);
        config.setDefaultSessionCountersEnabled(defaultSessionCountersEnabled);
        config.setJmxEnabled(jmxEnabled);
        config.setJmxDomain("SMSC_1");
        return config;
    }

    @Bean
    public ResponseMessageIdGeneratorImpl messageIdGenerator() {
        return new ResponseMessageIdGeneratorImpl();
    }

    @Bean
    public SmscRateSender rateSender(
            SmppSessionManager sessionManager,
            @Value("${smsc.rate-sender.segments:2}") int numberOfSegments,
            @Value("${smsc.rate-sender.dest-address-digits:1111}") String destAddressDigits,
            @Value("${smsc.rate-sender.pool-size:0}") int poolSize) {
        SmscRateSender rateSender = new SmscRateSender();
        if (poolSize > 0) {
            rateSender.setSchedulerPoolSize(poolSize);
        }
        DeliverSegmentedMessageFactory factory = new DeliverSegmentedMessageFactory();
        factory.setNumberOfSegments(numberOfSegments);
        factory.setDestAddressDigits(destAddressDigits);
        factory.setSessionManager(sessionManager);
        rateSender.setMessageFactory(factory);
        return rateSender;
    }
}
