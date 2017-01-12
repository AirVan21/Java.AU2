package ru.spbau.javacourse.torrent.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SharedGUI {

    public static void createWarningDialog(Stage stage, String message) {
        final Stage dialog = new Stage();

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Warning");

        StackPane pane = new StackPane();
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        pane.getChildren().add(new Text(message));
        dialog.setScene(new Scene(pane, 300, 250));
        dialog.show();
    }
}
