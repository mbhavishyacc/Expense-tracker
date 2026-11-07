package com.bhavishya.expensetracker.model;

public record User(long userId, String username, String email, String passwordHash) { }
