package com.akshith.seatreservationlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HoldConcurrencyIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @LocalServerPort
    private int port;

    @Test
    void two_parallel_holds_same_seat_only_one_wins() throws Exception {
        String url = "http://localhost:" + port + "/holds";
        String body = "{\"eventId\":\"event_123\",\"seatNo\":\"A1\",\"ttlSeconds\":120}";

        ExecutorService ex = Executors.newFixedThreadPool(2);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            futures.add(ex.submit(() -> postJson(url, body)));
        }

        int s1 = futures.get(0).get(15, TimeUnit.SECONDS);
        int s2 = futures.get(1).get(15, TimeUnit.SECONDS);

        ex.shutdownNow();

        int created = 0;
        int conflict = 0;

        if (s1 == 201) created++;
        if (s2 == 201) created++;
        if (s1 == 409) conflict++;
        if (s2 == 409) conflict++;

        Assertions.assertEquals(1, created);
        Assertions.assertEquals(1, conflict);
    }

    private int postJson(String url, String json) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.statusCode();
    }
}