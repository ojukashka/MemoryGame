package com.example.playmemory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Play extends Application {
    private int gridSize = 4; // Standardgröße des Spielfelds
    private List<Card> cards; // Kartenliste
    private Card firstSelectedCard = null; // Erste aufgedeckte Karte
    private Button firstSelectedButton = null; // Button der ersten Karte

    private Player[] players;
    private int currentPlayerIndex = 0; // Index des aktuellen Spielers
    private Label playerLabel; // Anzeige des aktuellen Spielers
    private Label scoreLabel; // Anzeige der Punktestände
    private GridPane gameBoard;

    private Stage primaryStage; // Globale Referenz für die Stage

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Stage speichern
        setupNewGame();
    }

    private void setupNewGame() {
        // Abfrage der Levelgröße
        gridSize = promptForGridSize();

        // Anzahl der Spieler abfragen
        int playerCount = promptForPlayerCount();
        players = new Player[playerCount];

        // Spielernamen abfragen
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player(promptForPlayerName("Spieler " + (i + 1)));
        }

        // Karten initialisieren
        cards = generateCards();

        gameBoard = new GridPane();

        // Spielfeld erstellen
        renderGameBoard();

        // Labels für Spieler und Punktestand
        playerLabel = new Label();
        playerLabel.getStyleClass().add("player-label"); // CSS-Klasse hinzufügen
        scoreLabel = new Label();
        scoreLabel.getStyleClass().add("score-label"); // CSS-Klasse hinzufügen
        updatePlayerLabel();
        updateScoreLabel();

        // Menü mit Buttons für Aktionen und Status
        VBox actionMenu = createActionMenu();

        // Hauptlayout
        HBox root = new HBox(actionMenu, gameBoard);
        root.setSpacing(20);
        root.getStyleClass().add("root"); // CSS-Klasse hinzufügen

        Scene scene = new Scene(root, 1000, 800);
        URL cssURL = getClass().getResource("/com/example/playmemory/styles.css");
        if (cssURL != null) {
            System.out.println("CSS gefunden: " + cssURL);
            scene.getStylesheets().add(cssURL.toExternalForm());
        } else {
            System.out.println("CSS nicht gefunden!");
        }

        primaryStage.setTitle("Memory Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private VBox createActionMenu() {
        VBox menu = new VBox();
        menu.setSpacing(10); // Weniger Abstand
        menu.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-border-color: #ccc;");

        Label menuTitle = new Label("Aktionen");
        menuTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Button newGameButton = new Button("Neues Spiel");
        newGameButton.setOnAction(e -> promptForSaveAndStartNewGame());

        Button exitGameButton = new Button("Spiel beenden");
        exitGameButton.setOnAction(e -> promptForSaveAndExit());

        Label statusTitle = new Label("Spielstatus");
        statusTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        playerLabel = new Label("Aktueller Spieler: ");
        playerLabel.getStyleClass().add("player-label");

        scoreLabel = new Label("Punktestand:");
        scoreLabel.getStyleClass().add("score-label");

        ScrollPane scrollPane = new ScrollPane(scoreLabel); // Scrollbar für lange Punktestände
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200); // Maximale Höhe für Punktestand

        menu.getChildren().addAll(
                menuTitle, newGameButton, exitGameButton,
                statusTitle, playerLabel, scrollPane
        );

        return menu;
    }


    private int promptForGridSize() {
        TextInputDialog dialog = new TextInputDialog("4");
        dialog.setTitle("Schwierigkeitsstufe wählen");
        dialog.setHeaderText("Spielfeldgröße auswählen");
        dialog.setContentText("Bitte gib die Größe des Spielfelds ein (4, 6, 8):");

        Optional<String> result = dialog.showAndWait();
        return result.map(Integer::parseInt).orElse(4); // Standardwert ist 4
    }

    private int promptForPlayerCount() {
        TextInputDialog dialog = new TextInputDialog("2");
        dialog.setTitle("Spielereinstellungen");
        dialog.setHeaderText("Anzahl der Spieler auswählen");
        dialog.setContentText("Bitte gib die Anzahl der Spieler ein (2-4):");

        Optional<String> result = dialog.showAndWait();
        return result.map(Integer::parseInt).orElse(2); // Standardwert ist 2
    }

    private String promptForPlayerName(String playerPrompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Spielername");
        dialog.setHeaderText(playerPrompt);
        dialog.setContentText("Bitte gib den Namen ein:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse(playerPrompt); // Standardname verwenden, falls keine Eingabe erfolgt
    }

    private void renderGameBoard() {
        gameBoard.getChildren().clear(); // Altes Spielfeld löschen
        int index = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Card card = cards.get(index++);
                Button cardButton = new Button();

                resetCard(card, cardButton); // Setze die Rückseite der Karte

                cardButton.setOnAction(e -> handleCardClick(card, cardButton));
                gameBoard.add(cardButton, col, row);
                cardButton.getStyleClass().add("game-card");
            }
        }
    }

    private List<Card> generateCards() {
        List<Card> cardList = new ArrayList<>();
        int pairs = (gridSize * gridSize) / 2;

        for (int i = 1; i <= pairs; i++) {
            String imagePath = "/com/example/playmemory/images/card" + i + ".png";
            cardList.add(new Card(imagePath));
            cardList.add(new Card(imagePath));
        }

        Collections.shuffle(cardList); // Karten mischen
        return cardList;
    }

    private void handleCardClick(Card card, Button cardButton) {
        if (card.isRevealed() || card.isMatched()) {
            return; // Ignoriere Klicks auf bereits umgedrehte oder gefundene Karten
        }

        // Zeige das Vorderseiten-Bild
        URL frontImageUrl = getClass().getResource(card.getValue());
        if (frontImageUrl == null) {
            System.out.println("Fehler: Kartenbild nicht gefunden: " + card.getValue());
            return;
        }
        Image frontImage = new Image(frontImageUrl.toExternalForm());
        ImageView frontImageView = new ImageView(frontImage);
        frontImageView.setFitWidth(100);
        frontImageView.setFitHeight(100);

        cardButton.setGraphic(frontImageView); // Zeige Vorderseite
        card.setRevealed(true);

        if (firstSelectedCard == null) {
            firstSelectedCard = card;
            firstSelectedButton = cardButton;
        } else {
            if (firstSelectedCard.getValue().equals(card.getValue())) {
                // Karten stimmen überein
                firstSelectedCard.setMatched(true);
                card.setMatched(true);

                // Punkte für den aktuellen Spieler erhöhen
                players[currentPlayerIndex].addScore(1);
                updateScoreLabel(); // Aktualisiere die Anzeige der Punktestände

            } else {
                // Karten stimmen nicht überein: Rückseite wiederherstellen
                Card tempFirstCard = firstSelectedCard;
                Button tempFirstButton = firstSelectedButton;

                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // Wartezeit von 1 Sekunde
                    } catch (InterruptedException ignored) {
                    }
                    Platform.runLater(() -> {
                        resetCard(tempFirstCard, tempFirstButton);
                        resetCard(card, cardButton);
                    });
                }).start();
            }
            firstSelectedCard = null;
            firstSelectedButton = null;

            // Spielerwechsel
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            updatePlayerLabel();
        }

        if (isGameOver()) {
            showGameOverMessage();
        }
    }

    private void resetCard(Card card, Button cardButton) {
        String backImagePath = "/com/example/playmemory/images/cardBack.png";
        URL backImageUrl = getClass().getResource(backImagePath);
        if (backImageUrl == null) {
            System.out.println("Fehler: Rückseitenbild nicht gefunden: " + backImagePath);
            return;
        }
        Image backImage = new Image(backImageUrl.toExternalForm());
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitWidth(100);
        backImageView.setFitHeight(100);
        cardButton.setGraphic(backImageView);
        card.setRevealed(false);
    }

    private void updatePlayerLabel() {
        playerLabel.setText("Aktueller Spieler: " + players[currentPlayerIndex].getName());
    }

    private void updateScoreLabel() {
        StringBuilder scoreText = new StringBuilder("Punktestand:\n");
        for (Player player : players) {
            scoreText.append(player.getName()).append(": ").append(player.getScore()).append(" Punkte\n");
        }
        scoreLabel.setText(scoreText.toString().trim());
    }


    private boolean isGameOver() {
        boolean allMatched = cards.stream().allMatch(Card::isMatched);
        System.out.println("Überprüfung Spielende: " + allMatched);
        return allMatched;
    }

    private void showGameOverMessage() {
        // Spieler nach Punkten sortieren (absteigend)
        List<Player> sortedPlayers = new ArrayList<>(List.of(players));
        sortedPlayers.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        // Nachricht erstellen
        StringBuilder message = new StringBuilder("Spiel beendet! \n\nEndstand:\n");
        Player winner = sortedPlayers.get(0);
        message.append("Gewinner: ").append(winner.getName()).append(" mit ").append(winner.getScore()).append(" Punkten!\n\n");

        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            message.append(i + 1).append(". ").append(player.getName())
                    .append(": ").append(player.getScore()).append(" Punkte\n");
        }

        // Nachricht anzeigen
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spielende");
        alert.setHeaderText("Das Spiel ist vorbei!");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }


    private void resetGameState() {
        firstSelectedCard = null;
        firstSelectedButton = null;

        for (Card card : cards) {
            card.setMatched(false);
            card.setRevealed(false);
        }

        for (Player player : players) {
            player.resetScore(); // Punkte zurücksetzen
        }

        currentPlayerIndex = 0;
        updatePlayerLabel();
        updateScoreLabel();
    }

    private void promptForSaveAndStartNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Neues Spiel");
        alert.setHeaderText("Möchtest du den aktuellen Spielstand speichern?");
        alert.setContentText("Wähle eine Option.");

        ButtonType saveButton = new ButtonType("Speichern");
        ButtonType discardButton = new ButtonType("Verwerfen");
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            saveGame();
        }

        if (result.isPresent() && (result.get() == saveButton || result.get() == discardButton)) {
            setupNewGame();
        }
    }

    private void promptForSaveAndExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Spiel beenden");
        alert.setHeaderText("Möchtest du den aktuellen Spielstand speichern?");
        alert.setContentText("Wähle eine Option.");

        ButtonType saveButton = new ButtonType("Speichern");
        ButtonType discardButton = new ButtonType("Verwerfen");
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            saveGame();
        }

        if (result.isPresent() && (result.get() == saveButton || result.get() == discardButton)) {
            Platform.exit();
        }
    }

    private void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("memory_game_save.dat"))) {
            out.writeObject(players);
            out.writeObject(cards);
            out.writeInt(currentPlayerIndex);
            System.out.println("Spielstand gespeichert!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
