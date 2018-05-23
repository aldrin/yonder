package co.aldrin.nobel.rpc;

import co.aldrin.nobel.Laureate;
import lombok.Data;

import java.util.List;

/**
 * The response.
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Data
public class Response {

    /**
     * The laureates
     */
    private List<Laureate> laureates;
}
