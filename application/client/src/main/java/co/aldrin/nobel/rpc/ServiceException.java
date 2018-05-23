package co.aldrin.nobel.rpc;

/**
 * When things go wrong
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
public class ServiceException extends Exception {

    public ServiceException(Exception cause) {
        super(cause);
    }
}
