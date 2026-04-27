package com.smpp.smscsim;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.smpp.smscsim.server.SmscServer;
import com.smpp.smscsim.service.auto.SmscGlobalConfiguration;
import com.cloudhopper.smpp.SmppServerConfiguration;

/**
 * SMSC SMPP simulator that supports:
 * <ul>
 * <li>For each submit request it receives it sends a delivery receipt with configurable delay. </li>
 * <li>Supports listening on multiple ports and multiple applications. </li>
 * <li>Sending of segmented SMPP messages, supports segmentation set via optional parameter or via UDH00 or UDH08</li>
 * <li>Outgoing (MO) messages are sent to connected set of RX and TRX connections with the same application/system ID.
 * RoundRobin is used to rotate between them.</li>
 * <li>Controlling MO messages sent by simulator with JMX commands - start, stop, send message stream and so on</li>
 * </ul>
 *
 * Sample usage: java --add-opens java.base/java.nio=ALL-UNNAMED -jar smscsim.jar --smsc.ports=34567,34568
 *
 * @author Matous Voldrich
 */
@SpringBootApplication
public class ServerMain {
	private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

	private final List<SmscServer> smppServers = new ArrayList<>();

	static public void main(String[] args) {
		SpringApplication.run(ServerMain.class, args);
	}

	@Bean
	public CommandLineRunner startSmppServers(
			SmscGlobalConfiguration smscConfiguration,
			ObjectProvider<SmppServerConfiguration> serverConfigProvider,
			@Value("${smsc.ports}") String ports) {
		return args -> {
			String[] portArray = ports.split(",");
			for (String portStr : portArray) {
				int port = Integer.parseInt(portStr.trim());
				SmppServerConfiguration serverConfig = serverConfigProvider.getObject();
				serverConfig.setPort(port);
				serverConfig.setJmxDomain("SMSC_" + port);
				SmscServer smscServer = new SmscServer(smscConfiguration, serverConfig);
				smppServers.add(smscServer);
			}

			logger.info("Starting SMPP servers...");
			for (SmscServer smppServer : smppServers) {
				smppServer.start();
			}
			logger.info("SMPP servers started on ports: {}", ports);
		};
	}

	@PreDestroy
	public void shutdown() {
		logger.info("Stopping SMPP servers...");
		for (SmscServer smppServer : smppServers) {
			try {
				smppServer.stop();
				smppServer.destroy();
			} catch (Exception e) {
				logger.error("Error stopping SMPP server", e);
			}
		}
		logger.info("SMPP servers stopped");
	}
}
