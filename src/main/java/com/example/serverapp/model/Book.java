package com.example.serverapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @JsonProperty("title")
    private String title;
    @JsonProperty("image_url")
    private String image_url;
    @JsonProperty("name")
    private String author;
}
