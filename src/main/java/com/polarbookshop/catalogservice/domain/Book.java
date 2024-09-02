package com.polarbookshop.catalogservice.domain;

public record Book(
        String isbn, // 책을 고유하게 식별
        String title,
        String author,
        Double price
) {}
