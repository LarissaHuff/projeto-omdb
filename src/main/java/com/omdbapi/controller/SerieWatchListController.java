package com.omdbapi.controller;

import com.google.gson.Gson;
import com.omdbapi.OmdbService;
import com.omdbapi.dto.Serie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/watchlist")
public class SerieWatchListController {
    @Autowired
    private OmdbService omdbService;

    private List<Serie> serieWatchList = new ArrayList<>();

    private Gson gson = new Gson();

    @GetMapping
    public List<Serie> getSerieWatchList() {

        return this.serieWatchList;
    }

    @PostMapping
    public void insertIntoWatchList(@RequestParam String title) {
        String response = omdbService.getTitulo(title);

        Serie serie = gson.fromJson(response, Serie.class);
        serieWatchList.add(serie);
    }




}
