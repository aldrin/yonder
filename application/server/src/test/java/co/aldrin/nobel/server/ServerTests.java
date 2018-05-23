package co.aldrin.nobel.server;

import co.aldrin.nobel.client.Client;
import co.aldrin.nobel.rpc.Request;
import co.aldrin.nobel.rpc.Response;
import co.aldrin.nobel.rpc.ServiceException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@AutoConfigureDataMongo
@RunWith(SpringRunner.class)
public class ServerTests {

    @Autowired
    private Repository repository;

    @Test
    public void contextLoads() {
        assertEquals(repository.count(), 922);
    }

    @Test
    public void queriesWork() {
        assertEquals(8, repository.findByCountry("India").size());
        assertEquals(13, repository.findByPrizeYear(2013).size());
        assertEquals(1, repository.findByPrizeYearAndCategory(2016, "literature").size());
    }

    @Test
    @Ignore("Requires RabbitMQ on localhost")
    public void clientQuery() throws ServiceException {
        Client client = new Client("localhost", 1);
        Request request = new Request(2016, "literature", "test");
        Response response = client.handle(request);
        assertEquals(response.getLaureates().size(), 1);
        assertEquals(response.getLaureates().get(0).getSurname(), "Dylan");
    }
}
