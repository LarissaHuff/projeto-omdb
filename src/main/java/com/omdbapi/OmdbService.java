package com.omdbapi;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OmdbService {

    private static final String API_KEY = "9549dd8a";

    public String getTitulo(String titulo) {

        String url = String.format("http://www.omdbapi.com/?t=%s&apikey=%s", titulo, API_KEY)
                .replace(" ", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            return HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

