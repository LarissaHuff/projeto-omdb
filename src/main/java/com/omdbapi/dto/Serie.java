package com.omdbapi.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Serie {
    @SerializedName("Title")
    private String titulo;

    @SerializedName("totalSeasons")
    private String numeroTemporadas;

    @SerializedName("Genre")
    private String genero;

    @SerializedName("Year")
    private String ano;

    @SerializedName("imdbRating")
    private String notaImdb;

}
