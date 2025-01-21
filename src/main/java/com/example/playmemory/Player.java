package com.example.playmemory;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int score;

    public Player(String name) {
        this.name = name;
        this.score = 0; // Anfangsstand
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void addScore(int points) {
        this.score += points;
    }
}
