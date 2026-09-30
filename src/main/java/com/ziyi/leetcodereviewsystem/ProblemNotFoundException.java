package com.ziyi.leetcodereviewsystem;

public class ProblemNotFoundException extends RuntimeException {
    public ProblemNotFoundException(Integer id) {
        super("Problem not found: " + id);
    }
}
