package co.aldrin.nobel.client;

import co.aldrin.nobel.rpc.Request;
import co.aldrin.nobel.rpc.Response;
import co.aldrin.nobel.rpc.Service;
import co.aldrin.nobel.rpc.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A client side stub for the Nobel service
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class Client implements Service {

    private final String host;
    private final int timeout;

    public static void main(String[] args) throws ServiceException {
        if (args.length > 1) {
            Request request = new Request(Integer.parseInt(args[0]), args[1], "standalone");
            log.info("{} {}", request, new Client("localhost", 1000).handle(request));
        } else {
            log.error("Usage: client <year> <category>");
        }
    }

    @Override
    public Response handle(Request request) throws ServiceException {
        return Middleware.send(host, request, timeout);
    }
}
