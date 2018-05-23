package co.aldrin.nobel.server;

import co.aldrin.nobel.Laureate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository access interface
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
public interface Repository extends CrudRepository<Laureate, String> {

    /**
     * Find laureates for the given country.
     */
    @Query("{$or: [{'bornCountry': ?0}, {'diedCountry': ?0}]}")
    List<Laureate> findByCountry(String country);

    /**
     * Find laureates for a given year.
     */
    @Query("{'prizes.year': ?0}")
    List<Laureate> findByPrizeYear(int year);

    /**
     * Find laureates for a given year.
     */
    @Query("{'prizes.year': ?0, 'prizes.category': ?1}")
    List<Laureate> findByPrizeYearAndCategory(int year, String category);
}
