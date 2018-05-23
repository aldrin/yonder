package co.aldrin.nobel.server;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;
import org.springframework.util.StringUtils;

import static co.aldrin.nobel.client.Middleware.*;

/**
 * Stuff in the middle (server side)
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Configuration
@RequiredArgsConstructor
public class Middleware {

    private static final String QUEUE = "nobel-laureates";

    private final BackendService backendService;
    private final ConnectionFactory connectionFactory;

    @Bean
    Queue queue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    MessageListenerAdapter listenerAdapter() {
        MessageListenerAdapter adapter = new MessageListenerAdapter(backendService, "handle");
        adapter.setMessageConverter(new Jackson2JsonMessageConverter());
        return adapter;
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTE);
    }

    @Bean
    SimpleMessageListenerContainer container(MessageListenerAdapter listen, ErrorHandler errorHandler) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDefaultRequeueRejected(false);
        container.setErrorHandler(errorHandler);
        container.setMessageListener(listen);
        container.setQueueNames(QUEUE);
        return container;
    }

    @Bean
    ErrorHandler errorHandler(RabbitTemplate template) {
        return t -> {
            if (t instanceof ListenerExecutionFailedException) {
                ListenerExecutionFailedException ex = (ListenerExecutionFailedException) t;
                MessageProperties failed = ex.getFailedMessage().getMessageProperties();
                String replyQueue = failed.getReplyTo();
                if (StringUtils.hasText(replyQueue)) {
                    // Get the reason why we are here
                    String reason = ex.getCause().getMessage();

                    // Set the error headers to signal this is an error
                    MessageProperties properties = new MessageProperties();
                    properties.setCorrelationId(failed.getCorrelationId());
                    properties.setHeader(ERROR, reason);

                    // Make an error message and send it back
                    Message message = template.getMessageConverter().toMessage(reason, properties);
                    template.send(replyQueue, message);
                }
            }
        };
    }
}
