package lfkimura.rabbitmq_consumer.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageQueueHandler {
	

	private static final Logger LOG = LoggerFactory.getLogger(MessageQueueHandler.class);

	public void handleMessage(final String message) throws Exception {
		LOG.info("Message arrived : [{}] ",message);
	}

}
