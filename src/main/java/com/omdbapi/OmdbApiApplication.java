package com.omdbapi;

import com.google.gson.Gson;
import com.omdbapi.dto.Serie;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OmdbApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(OmdbApiApplication.class, args);
	}
}
