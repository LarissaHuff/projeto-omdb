package com.omdbapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FilmeMaiorNotaDTO {
    private String titulo;
    private Double nota;
}
