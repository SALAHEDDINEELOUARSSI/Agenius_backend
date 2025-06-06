/*package com.example.jobofferservice.util;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange phaseExchange() {
        return new TopicExchange("phase-exchange");
    }

   /*  @Bean
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
}

*/
package com.example.jobofferservice.util;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class RabbitConfig {

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

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter()); // ✅ Use JSON
        return rabbitTemplate;
    }
}
