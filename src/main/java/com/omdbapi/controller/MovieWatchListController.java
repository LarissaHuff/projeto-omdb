package com.omdbapi.controller;

import com.google.gson.Gson;
import com.omdbapi.OmdbService;
import com.omdbapi.dto.Filme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movie-list")
public class MovieWatchListController {
    @Autowired
    private OmdbService omdbService;
    private List<Filme> movieWatchList = new ArrayList<>();
    private Gson gson = new Gson();

    @GetMapping
    public List<Filme> getMovieWatchList() {
        return this.movieWatchList;
    }

    @PostMapping
    public void addMovie(@RequestParam String title) {
        String titulo = omdbService.getTitulo(title);
        Filme filme = gson.fromJson(titulo, Filme.class);

        movieWatchList.add(filme);
    }

    @PutMapping("/titulo/{title}/nota/{nota}")
    public void avaliaFilmeVisto(@PathVariable String title, @PathVariable Double nota) {
        for (int i = 0; i < movieWatchList.size(); i++) {
            if (movieWatchList.get(i).getTitulo().equalsIgnoreCase(title)) {
                movieWatchList.get(i).setVisto(true);
                movieWatchList.get(i).setNota(nota);
            }

        }
    }

    @GetMapping("/assistidos")
    public List<Filme> filmesVistos() {
        return movieWatchList.stream()
                .filter(Filme::getVisto)
                .collect(Collectors.toList());
    }

    @GetMapping("/nao-assistidos")
    public List<Filme> filmesNaoVistos() {
        return movieWatchList.stream()
                .filter(it -> !it.getVisto())
                .collect(Collectors.toList());
    }

    @PostMapping("/save")
    public void save() throws IOException {
        OutputStream os = new FileOutputStream("./watchList.csv", false);
        Writer wr = new OutputStreamWriter(os);
        BufferedWriter br = new BufferedWriter(wr);

        for (int i = 0; i < movieWatchList.size(); i++) {
            Filme filme = movieWatchList.get(i);
            if (filme.getNota() == null) {
                filme.setNota(0.0);
            }

            br.write(filme.getTitulo() + ";" + filme.getVisto() + ";" + filme.getNota());
            br.newLine();
        }

        br.close();
    }

    @PostMapping("/load")
    public void load() throws IOException {
        if (!this.movieWatchList.isEmpty()) {
            throw new RuntimeException("lista deve estar vazia.");
        }

        FileInputStream fis = new FileInputStream("./watchList.csv");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");

                String titulo = omdbService.getTitulo(split[0]);
                Filme filme = gson.fromJson(titulo, Filme.class);

                filme.setVisto(Boolean.parseBoolean(split[1]));
                filme.setNota(Double.parseDouble(split[2]));

                movieWatchList.add(filme);
            }
        }
    }
}
