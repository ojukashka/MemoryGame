package com.example.playmemory;

import java.io.Serializable;

public class Card implements Serializable {
    private String value; // Speichert den Bildpfad
    private boolean isRevealed = false; // Status, ob die Karte aufgedeckt ist
    private boolean isMatched = false; // Status, ob die Karte ein Paar bildet

    // Konstruktor
    public Card(String value) {
        this.value = value; // Der Bildpfad der Karte wird als Wert gespeichert
    }

    // Gibt den Wert (Bildpfad) der Karte zurück
    public String getValue() {
        return value;
    }

    // Gibt zurück, ob die Karte aufgedeckt ist
    public boolean isRevealed() {
        return isRevealed;
    }

    // Setzt den Status der Karte (aufgedeckt oder verdeckt)
    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }

    // Gibt zurück, ob die Karte ein Paar gebildet hat
    public boolean isMatched() {
        return isMatched;
    }

    // Setzt den Status der Karte (Teil eines Paares)
    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
