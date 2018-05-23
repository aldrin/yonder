package co.aldrin.nobel.aws;

import co.aldrin.nobel.client.Client;
import co.aldrin.nobel.rpc.Request;
import co.aldrin.nobel.rpc.Response;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.Map;

/**
 * An AWS Lambda wrapper to interface with the backend service.
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
public class Lambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectWriter json = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private final String host = System.getenv().getOrDefault("INFRA", "localhost");
    private final APIGatewayProxyResponseEvent ok = new APIGatewayProxyResponseEvent().withStatusCode(200);
    private final APIGatewayProxyResponseEvent badInput = new APIGatewayProxyResponseEvent().withStatusCode(400);
    private final APIGatewayProxyResponseEvent serverError = new APIGatewayProxyResponseEvent().withStatusCode(500);

    /**
     * The lambda.
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiRequest, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Received: " + apiRequest);

        // Extract backend service request
        Request request;
        try {
            request = translate(apiRequest);
        } catch (Exception e) {
            logger.log("Bad input: " + e.getMessage());
            return badInput.withBody(e.getMessage());
        }

        // Process backend service request
        try {
            Client client = new Client(host, context.getRemainingTimeInMillis());
            logger.log("Incoming: " + request);

            Response response = client.handle(request);

            logger.log("Outgoing: " + request);
            return ok.withBody(json.writeValueAsString(response.getLaureates()));
        } catch (Exception e) {
            logger.log("Failure: " + e.getMessage());
            return serverError.withBody(e.getMessage());
        }
    }


    /**
     * Convert the incoming API request to the service request
     */
    private Request translate(APIGatewayProxyRequestEvent apiRequest) {
        Request request = new Request();
        Map<String, String> parameters = apiRequest.getPathParameters();
        request.setContext(apiRequest.getRequestContext().getRequestId());
        request.setCategory(parameters.getOrDefault("category", "peace"));
        request.setYear(Integer.parseInt(parameters.getOrDefault("year", "No year given")));
        return request;
    }
}
