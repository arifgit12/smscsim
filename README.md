SMSC SMPP Simulator
=======

Simple SMSC SMPP simulator written in Java, basic features:

* Listens on specified ports for SMPP messages and sends back OK responses.
* Supports listening on multiple ports.
* Thanks to Cloudhopper SMPP library detailed realtime statistics of SMPP traffic is available through JMX.

**Delivery Receipt features**:

* For each submit (MT) request it receives, it sends a delivery receipt after fixed/random delay (configurable).
* Outgoing delivery receipt messages are sent to connected set of RX and TRX connections with the same application/system ID
as had the submit message that triggered the delivery receipt.
* RoundRobin is used to rotate between available matching connections.

**Deliver features, segmented messages**:

* Supports sending of deliver (MO) messages to connected clients.
* Deliver messages may be segmented SMPP messages, supports segmentation set via optional parameter or via UDH00 or UDH08.
* MO messages sent by simulator to connected clients are controlled with JMX commands - start, stop, send message stream and so on.

Simulator was used as a testing tool to test proper handling of delivery receipts and segmented deliver messages.

## Requirements

* JDK 17 or later
* Maven 3.x

## How to build

    mvn clean package

This produces an executable fat JAR at `target/smscsim.jar`.

## How to run

Start SMPP servers on ports 34567, 34568 and 34569:

    java --add-opens java.base/java.nio=ALL-UNNAMED \
         --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
         --add-opens java.base/java.lang=ALL-UNNAMED \
         -jar target/smscsim.jar --smsc.ports=34567,34568,34569

The `--add-opens` flags are required for Netty 3.x compatibility with Java 17+.

## Configuration

All settings can be configured via command-line properties or `application.yml`:

| Property | Default | Description |
|----------|---------|-------------|
| `smsc.ports` | `10001` | Comma-separated list of SMPP server ports |
| `smsc.server.max-connection-size` | `10` | Max connections per server |
| `smsc.server.default-window-size` | `100` | SMPP window size |
| `smsc.server.jmx-enabled` | `true` | Enable JMX for SMPP servers |
| `smsc.message-id.initial-value` | `1` | Starting message ID |
| `smsc.delivery-receipt.min-delay-ms` | `5000` | Minimum delivery receipt delay (ms) |
| `smsc.delivery-receipt.random-delta-ms` | `5000` | Random additional delay (ms) |
| `smsc.rate-sender.segments` | `2` | Number of segments for MO messages |
| `smsc.rate-sender.dest-address-digits` | `1111` | Destination address for MO messages |

Example with custom settings:

    java --add-opens java.base/java.nio=ALL-UNNAMED \
         --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
         --add-opens java.base/java.lang=ALL-UNNAMED \
         -jar target/smscsim.jar \
         --smsc.ports=34567,34568 \
         --smsc.delivery-receipt.min-delay-ms=1000

## Actuator

Spring Boot Actuator is available for health monitoring:

    GET http://localhost:8080/actuator/health

The health endpoint includes SMPP session count information.

## How to use JMX features

Download monitoring tool [VisualVM](https://visualvm.github.io/)
and install additional MBeans browser plugin.

## Tests

    mvn test

Integration test verifies basic delivery receipt functionality across single and multi-session scenarios. See `ServerMainTest`.

## Libraries

* [Cloudhopper SMPP](https://github.com/fizzed/cloudhopper-smpp) (fizzed maintained fork)
* [Spring Boot](https://spring.io/projects/spring-boot) 3.3
* [Google Guava](https://github.com/google/guava)

## License
