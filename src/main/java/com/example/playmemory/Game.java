package com.example.playmemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private List<Card> cards; // Liste aller Karten
    private List<Player> players; // Liste aller Spieler
    private int currentPlayerIndex; // Aktueller Spieler

    public Game(List<String> cardValues, List<String> playerNames) {
        // Karten initialisieren
        cards = new ArrayList<>();
        for (String value : cardValues) {
            cards.add(new Card(value));
            cards.add(new Card(value)); // Paare erstellen
        }
        Collections.shuffle(cards); // Karten mischen

        // Spieler initialisieren
        players = new ArrayList<>();
        for (String name : playerNames) {
            players.add(new Player(name));
        }

        currentPlayerIndex = 0;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        return cards.stream().allMatch(Card::isMatched);
    }
}