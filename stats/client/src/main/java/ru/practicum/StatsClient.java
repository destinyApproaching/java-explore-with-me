package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class StatsClient {
    private final WebClient webClient;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public ResponseEntity<List<ViewStatsDto>> getStat(String start, String end, List<String> uris, boolean unique) {
        return webClient.get()
                .uri(UriComponentsBuilder
                        .fromUriString("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build().toUriString())
                .retrieve()
                .toEntityList(ViewStatsDto.class)
                .block();
    }

    public ResponseEntity<EndpointHitDto> hitStat(EndpointHitDto endpointHitDto) {
        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .retrieve()
                .toEntity(EndpointHitDto.class)
                .block();
    }
}