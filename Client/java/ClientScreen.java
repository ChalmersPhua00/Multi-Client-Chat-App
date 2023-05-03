import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientScreen extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	Client c;

	@Override
	public void start(Stage stage) {
		TextField nameField = new TextField();
		nameField.getStyleClass().add("tf");
		Label name = new Label("Username:");
		name.getStyleClass().add("l");
		Button join = new Button("Join");
		join.getStyleClass().add("b");

		VBox vbox = new VBox(10, name, nameField, join);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(10));

		join.setOnAction(event -> {
			String username = nameField.getText();
			if (!username.isEmpty()) {
				showClientScreen(stage, username);
			}
		});

		vbox.getStyleClass().add("vb");
		Scene scene = new Scene(vbox, 300, 500);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle("New User");
		stage.show();
	}

	private void showClientScreen(Stage stage, String username) {
		Label online = new Label("Online");
		online.getStyleClass().add("l");
		ListView<String> clientsLV = new ListView<>();
		clientsLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		clientsLV.setPrefHeight(200);
		Label chat = new Label("Chat");
		chat.getStyleClass().add("l");
		ListView<String> chatLV = new ListView<>();
		chatLV.setFocusTraversable(false);
		chatLV.setMouseTransparent(true);

		TextField message = new TextField();
		message.getStyleClass().add("tf");
		Button send = new Button("Send");
		send.getStyleClass().add("b");

		HBox messageBox = new HBox(10, message, send);
		HBox.setHgrow(message, Priority.ALWAYS);

		try {
			c = new Client(data -> {
				Platform.runLater(() -> {
					if (data.toString().contains(":")) {
						chatLV.getItems().add(data.toString());
					} else {
						if (!Objects.equals(data.toString(), username) && !Objects.equals(data.toString(), "NIL")) {
							if (data.toString().charAt(0) == '-') {
								clientsLV.getItems().remove(data.toString().substring(1));
							} else if (!clientsLV.getItems().contains(data.toString())) {
								clientsLV.getItems().add(data.toString());
							}
						}
					}
				});
			}, username);
			c.start();
		} catch (Exception ignored) {
		}

		send.setOnAction(event -> {
			StringBuilder text = new StringBuilder(username + ": " + message.getText() + "/" + username);
			ObservableList<String> to = clientsLV.getSelectionModel().getSelectedItems();
			for (String client : to) {
				text.append("/").append(client);
			}
			if (text.length() > 0) {
				c.sendStr(text.toString());
				message.clear();
			}
		});

		VBox vbox = new VBox(10, online, clientsLV, chat, chatLV, messageBox);
		vbox.setPadding(new Insets(10));
		vbox.getStyleClass().add("vb");
		Scene scene = new Scene(vbox, 300, 500);
		scene.getStylesheets().add("style.css");
		stage.setScene(scene);
		stage.setTitle(username);
		stage.show();
	}
}