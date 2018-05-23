package co.aldrin.nobel.server;

import co.aldrin.nobel.Laureate;
import co.aldrin.nobel.rpc.Request;
import co.aldrin.nobel.rpc.Response;
import co.aldrin.nobel.rpc.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * The backend service.
 *
 * @author Aldrin D'Souza
 * @since 0.1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BackendService implements Service {

    private final Repository repository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<Map<String, List<Laureate>>> type = new TypeReference<Map<String, List<Laureate>>>() {
    };

    /**
     * Handle the incoming request
     */
    @Override
    public Response handle(Request request) {
        List<Laureate> laureates = repository.findByPrizeYearAndCategory(request.getYear(), request.getCategory());

        if (laureates.isEmpty()) {
            log.error("No results found for request: {}", request);
            throw new IllegalArgumentException("No matching results");
        }

        Response response = new Response();
        response.setLaureates(laureates);

        log.info("Processed: {}, Results: {}", request, laureates.size());
        return response;
    }

    /**
     * At startup, load the data.
     */
    @Retryable
    @EventListener(ApplicationReadyEvent.class)
    void atStartup() throws IOException {

        URL json = this.getClass().getResource("/laureate.json");
        Map<String, List<Laureate>> laureates = mapper.readValue(json, type);

        repository.deleteAll();
        repository.saveAll(laureates.get("laureates"));

        log.info("{} laureates are available.", repository.count());
    }

}
