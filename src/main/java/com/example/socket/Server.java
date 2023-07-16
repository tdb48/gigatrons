package com.example.socket;

public enum Server {
    AUS("AUS"),
    FOREIGNER("FOREIGNER");

    private final String name;

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    Server(final String name) {
        this.name = name;
    }

}
