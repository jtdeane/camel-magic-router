camel-magic-router
=======================

Built with Java 8+ and Apache Camel (2.15.3)

Tested with JUnit (4.11)

Runs within ActiveMQ (5.12.0)

1) Update activemq.xml

`<import resource="camel.xml"/>`

2) Add camel.xml to ActiveMQ conf directory


3) Copy camel-magic-router-1.0.0.jar to ActiveMQ lib directory


4) Executes with JMeter (2.13); requires activemq-all-5.12.0.jar in lib directory

>Note that viewing message content in ActiveMQ Web Console is incompatible with Java 8 but will be fixed in ActiveMQ 5.13 (upgrading Jetty to 9+). Until then use the hawtio (http://hawt.io) to view message content.  