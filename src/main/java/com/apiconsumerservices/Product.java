package com.apiconsumerservices;

import lombok.Data;

import java.util.List;


@Data
public class Product {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private Double discountPercentage;
    private Double rating;
    private Long stock;
    private String brand;
    private String category;
    private String thumbnail;
    private List<String> images;

}
