package co.aldrin.nobel.rpc;


/**
 * An RPC service
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
public interface Service {

    /**
     * Handle the incoming request
     *
     * @param request A selection criteria
     * @return A list of laureates
     */
    Response handle(Request request) throws ServiceException;
}
