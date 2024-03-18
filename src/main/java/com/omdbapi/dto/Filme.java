package com.omdbapi.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filme {
    @SerializedName("Title")
    private String titulo;

    @SerializedName("Genre")
    private String genero;

    @SerializedName("Year")
    private String ano;

    @SerializedName("imdbRating")
    private String notaImdb;

    @SerializedName("Director")
    private String diretor;

    @SerializedName("Language")
    private String linguagem;

    @SerializedName("Country")
    private String pais;

    private Boolean visto = false;

    private Double nota;

}
