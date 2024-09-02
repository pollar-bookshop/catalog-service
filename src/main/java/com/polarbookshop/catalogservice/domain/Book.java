package com.polarbookshop.catalogservice.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Book(
        @NotBlank(message = "The Book ISBN must be defined.")
        @Pattern(
                regexp = "^([0-9]{10}|[0-9]{13})$",
                message = "The ISBN format must be valid"
        )
        String isbn, // 책을 고유하게 식별

        @NotBlank(message = "The Book title must be defined.")
        String title,

        @NotBlank(message = "The Book author must be defined.")
        String author,

        @NotNull(message = "The Book price must be defined.")
        @Positive(
                message = "The book price must be grater than zero"
        )
        Double price
) {}
