package co.aldrin.nobel.client;

import co.aldrin.nobel.rpc.Request;
import co.aldrin.nobel.rpc.Response;
import co.aldrin.nobel.rpc.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stuff in the middle (client side)
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Slf4j
public class Middleware {

    public static final String EXCHANGE = "nobel";
    public static final String ROUTE = "laureates";
    public static final String ERROR = "request-failed";

    private static final String CONTENT = "application/json";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Map<String, Object> HEADERS = Collections.singletonMap("__TypeId__", Request.class.getName());

    /**
     * Send the given request to the given host and expect it to reply back in the given time.
     */
    static Response send(String host, Request request, int timeoutMs) throws ServiceException {

        String id = request.getContext();

        // Open a connection to the host
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);

        try (Connection connection = factory.newConnection()) {

            log.info("Sending: {}", request);

            // Prepare for send
            Channel channel = connection.createChannel();
            String replyQueue = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .correlationId(request.getContext())
                    .contentType(CONTENT)
                    .replyTo(replyQueue)
                    .headers(HEADERS)
                    .build();

            // Send it out
            channel.basicPublish(EXCHANGE, ROUTE, props, JSON.writeValueAsString(request).getBytes(CHARSET));

            // Listen for the reply
            ReplyReader reader = new ReplyReader(channel, id);
            channel.basicConsume(replyQueue, true, reader);

            // Poll
            return reader.read(timeoutMs);

        } catch (Exception e) {
            log.error("Failure: {}", e.getMessage());
            throw new ServiceException(e);
        }
    }

    /**
     * The reply receiver
     */
    static class ReplyReader extends DefaultConsumer {
        final String id;
        final AtomicBoolean error = new AtomicBoolean();
        final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        ReplyReader(Channel channel, String id) {
            super(channel);
            this.id = id;
        }

        /**
         * Read the response
         */
        Response read(int timeoutMs) throws Exception {
            String response = queue.poll(timeoutMs, TimeUnit.MILLISECONDS);

            if (response == null) {
                throw new TimeoutException();
            }

            if (error.get()) {
                throw new IOException(response);
            }

            return JSON.readValue(response, Response.class);
        }

        /**
         * Read delivered reply.
         */
        @Override
        public void handleDelivery(String t, Envelope e, AMQP.BasicProperties p, byte[] body) {
            if (p.getCorrelationId().equals(id)) {
                error.set(p.getHeaders().containsKey(ERROR));
                queue.offer(new String(body, CHARSET));
            }
        }
    }
}
