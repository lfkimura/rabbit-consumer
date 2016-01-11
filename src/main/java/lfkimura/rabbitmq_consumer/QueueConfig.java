package lfkimura.rabbitmq_consumer;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import lfkimura.rabbitmq_consumer.amqp.MessageQueueHandler;


@Configuration
@PropertySources({ @PropertySource("classpath:default.properties"),
		@PropertySource(value = "file:${external.config}", ignoreResourceNotFound = true) })
public class QueueConfig implements InitializingBean {

	@Autowired
	private Environment env;

	@Value(value = "${spring.rabbitmq.addresses}")
	private String rabbitmqAddresses;

	@Value(value = "${rabbitmq.queue.name}")
	private String queueName;

	@Value(value = "${rabbitmq.queue.consumers}")
	private Integer queueConsumers;

	@Autowired
	private CachingConnectionFactory connectionFactory;

	@Override
	public void afterPropertiesSet() {
		try {
			System.setProperty("archaius.configurationSource.additionalUrls", env.getProperty("archaius.config"));
		} catch (SecurityException | IllegalArgumentException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public CachingConnectionFactory connectionFactory() {
		final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(rabbitmqAddresses);
		return connectionFactory;
	}

	@PostConstruct
	public void initIt() {
		try {
			System.setProperty("archaius.configurationSource.additionalUrls", env.getProperty("archaius.config"));
		} catch (SecurityException | IllegalArgumentException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public SimpleMessageListenerContainer getMessageFromQueue(
			final MessageListenerAdapter message) {
		final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setConcurrentConsumers(queueConsumers);
		container.setQueueNames(queueName);
		container.setMessageListener(message);
		return container;
	}

	@Bean
	public MessageListenerAdapter listenerAdapterJiraUpdates(MessageQueueHandler handler) {
		final MessageListenerAdapter adapter = new MessageListenerAdapter(handler);
		adapter.setMessageConverter(new Jackson2JsonMessageConverter());
		return adapter;
	}
	@Bean
	public MessageQueueHandler messageQueueHandler() {
		return new MessageQueueHandler();
	}

}
