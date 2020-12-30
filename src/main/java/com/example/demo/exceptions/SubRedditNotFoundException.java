package com.example.demo.exceptions;

public class SubRedditNotFoundException extends RuntimeException {
    public SubRedditNotFoundException(String message) {
        super(message);
    }
}
