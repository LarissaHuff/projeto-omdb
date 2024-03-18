package com.omdbapi.controller;

import com.google.gson.Gson;
import com.omdbapi.OmdbService;
import com.omdbapi.dto.Filme;
import com.omdbapi.dto.FilmeDiferencaDTO;
import com.omdbapi.dto.FilmeMaiorNotaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
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

    @PutMapping
    public void avaliaFilmeVisto(@RequestParam String title, @RequestParam Double nota) {
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

    @GetMapping("/por-ano")
    public List<Filme> filmesPorAno(@RequestParam String ano) {
        return movieWatchList.stream()
                .filter(it -> Objects.equals(it.getAno(), ano))
                .collect(Collectors.toList());
    }

    @GetMapping("/quantidade")
    public long quantidadeFilmesNaLista() {
        return movieWatchList.stream()
                .count();
    }

    @GetMapping("/ordena")
    public List<Filme> ordenaPelaNotaPessoal() {
        return movieWatchList.stream()
                .sorted(Comparator.comparing(Filme::getNota).reversed())
                .toList();
    }

    @GetMapping("/ordena-imdb")
    public List<Filme> ordenaPelaNotaImbd() {
        return movieWatchList.stream()
                .sorted(Comparator.comparing(Filme::getNotaImdb).reversed())
                .toList();
    }


    @GetMapping("/pesquisa-iniciais")
    public Filme pesquisaPorIniciais(@RequestParam String iniciaisTitulo) {
        for (int i = 0; i < movieWatchList.size(); i++) {
            String titulo = movieWatchList.get(i).getTitulo();
            if (temMesmasIniciais(iniciaisTitulo, titulo)) {
                return movieWatchList.get(i);
            }
        }
        return null;
    }

    private static boolean temMesmasIniciais(String iniciaisTitulo, String titulo) {
        for (int i = 0; i < iniciaisTitulo.length(); i++) {
            if (iniciaisTitulo.charAt(i) != titulo.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @GetMapping("/categoria")
    private Map<String, Integer> quantosFilmesPorCategoria(@RequestParam(required = false) Boolean visto) {

        Map<String, Integer> filmesPorCategoria = new HashMap<>();

        for (int i = 0; i < movieWatchList.size(); i++) {
            String genero = movieWatchList.get(i).getGenero();

            if (filmesPorCategoria.containsKey(genero)) {
                Integer quantidadeAteAgora = filmesPorCategoria.get(genero);
                filmesPorCategoria.put(genero, quantidadeAteAgora + 1);

            }
            else {
                filmesPorCategoria.put(genero, 1);
            }

        }
        return filmesPorCategoria;
    }

    @GetMapping("/maior-nota-pessoal")
    private FilmeMaiorNotaDTO filmeComMaiorNotaPessoal() {

        Double maiorNota = movieWatchList.get(0).getNota();
        FilmeMaiorNotaDTO filme = new FilmeMaiorNotaDTO(null, null);

        for (int i = 1; i < movieWatchList.size(); i++) {
            Filme filmeDaVez = movieWatchList.get(i);
            if (maiorNota < filmeDaVez.getNota()) {
                maiorNota = filmeDaVez.getNota();
                filme.setTitulo(filmeDaVez.getTitulo());
                filme.setNota(filmeDaVez.getNota());
            }
        }
        return filme;
    }

    @GetMapping("/maior-nota-imdb")
    private Filme filmeComMaiorNotaImdb(){

        return movieWatchList.stream()
                .filter(it->!it.getNotaImdb().equals("N/A"))
                .max(Comparator.comparing(Filme::getNotaImdb))
                .orElseThrow();
    }

    @GetMapping("/categoria/melhor-avaliada")
    private Map.Entry<String, Double> categoriaMaisBemAvaliada() {
        Map<String, Double> categoriaNotas = new HashMap<>();

        for (int i = 0; i < movieWatchList.size(); i++) {
            String genero = movieWatchList.get(i).getGenero();
            Double nota = movieWatchList.get(i).getNota();

            Double categoriaNota = categoriaNotas.get(genero);
            if (categoriaNota == null) {
                categoriaNotas.put(genero, nota);
            } else {
                double soma = nota + categoriaNota;
                categoriaNotas.put(genero, soma);
            }
        }

        return categoriaNotas.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();
    }

    @GetMapping("/diferenca")
    private FilmeDiferencaDTO maiorDiferencaEntreNotaPessoalEimdb() {


        Double maior = getDiferenca(movieWatchList.get(0));
        Filme filme = movieWatchList.get(0);

        for (int i = 1; i < movieWatchList.size(); i++) {
            if (!filme.getVisto()) {
                continue;
            }

            Double diferenca = getDiferenca(movieWatchList.get(i));

            if (maior < diferenca) {
                maior = diferenca;
                filme = movieWatchList.get(i);
            }

        }
        return new FilmeDiferencaDTO(filme.getTitulo(), maior, filme.getNotaImdb(), filme.getNota());

    }

    private static Double getDiferenca(Filme filme) {
        if (filme.getNotaImdb() == null || filme.getNotaImdb().equals("N/A")) {
            return 0.0;
        }
        Double notaImdb = Double.parseDouble(filme.getNotaImdb());
        return Math.abs(
                notaImdb - filme.getNota());
    }


    @DeleteMapping
    public void remover(@RequestParam String titulo) {
        for (int i = 0; i < movieWatchList.size(); i++) {
            if (movieWatchList.get(i).getTitulo().equalsIgnoreCase(titulo)) {
                movieWatchList.remove(i);
                save();
            }
        }
    }

    @PostMapping("/save")
    public void save() {
        try (OutputStream os = new FileOutputStream("./watchList.csv", false);
             Writer wr = new OutputStreamWriter(os);
             BufferedWriter br = new BufferedWriter(wr)) {

            for (int i = 0; i < movieWatchList.size(); i++) {
                Filme filme = movieWatchList.get(i);
                if (filme.getNota() == null) {
                    filme.setNota(0.0);
                }

                br.write(filme.getTitulo() + ";" + filme.getVisto() + ";" + filme.getNota());
                br.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
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
