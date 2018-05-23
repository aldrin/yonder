package co.aldrin.nobel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * A Nobel Laureate
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Laureate {

    private String id;

    private String firstname;

    private String surname;

    private String born;

    private String died;

    private String bornCountry;

    private String bornCity;

    private String diedCountry;

    private String diedCity;

    private String gender;

    private List<Prize> prizes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prize {

        private int year;

        private String category;

        private String share;

        private String motivation;
    }
}
