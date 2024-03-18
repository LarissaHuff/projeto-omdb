package com.omdbapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FilmeDiferencaDTO {
    private String titulo;
    private Double diferenca;
    private String notaImdb;
    private Double nota;

}
