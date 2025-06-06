/*package com.AgeniusAgent.Agenius.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rabbitconfig {

    @Bean
    public TopicExchange phaseExchange() {
        return new TopicExchange("phase-exchange");
    }

    @Bean
    public Queue phaseDeadlineQueue() {
        return new Queue("phase-deadline-queue");
    }

    @Bean
    public Binding binding(Queue phaseDeadlineQueue, TopicExchange phaseExchange) {
        return BindingBuilder
                .bind(phaseDeadlineQueue)
                .to(phaseExchange)
                .with("phase.deadline.reached");
    }

    // Add this bean to configure message conversion using Jackson
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Set the container factory to use the above message converter
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
}
 */package com.AgeniusAgent.Agenius.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
public class Rabbitconfig implements RabbitListenerConfigurer {

    // Exchange bean
    @Bean
    public TopicExchange phaseExchange() {
        return new TopicExchange("phase-exchange");
    }

    // Queue bean
    @Bean
    public Queue phaseDeadlineQueue() {
        return new Queue("phase-deadline-queue");
    }

    // Binding between exchange and queue
    @Bean
    public Binding binding(Queue phaseDeadlineQueue, TopicExchange phaseExchange) {
        return BindingBuilder
                .bind(phaseDeadlineQueue)
                .to(phaseExchange)
                .with("phase.deadline.reached");
    }

    // JSON message converter for RabbitMQ
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitListener container factory to use JSON message converter
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    // Configure RabbitTemplate to use JSON converter as well (optional but recommended)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    // This configures the @RabbitListener methods to use the JSON converter to deserialize messages into objects
    @Bean
    public MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }

    // Link the MessageHandlerMethodFactory to RabbitListeners
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }
}
