package co.aldrin.nobel.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A request
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    /**
     * The prize year
     */
    private int year;

    /**
     * The prize category
     */
    private String category;

    /**
     * A tracing context
     */
    private String context;
}
