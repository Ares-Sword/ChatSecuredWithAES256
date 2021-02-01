package application;

import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client extends Application {
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private String serverMessage = "";
	private TextField tf = new TextField();
	private ServerSocket serverSocket;
	private Socket socket;
	private TextArea ta = new TextArea();
	private Button b = new Button("Send");

	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setTop(b);
		root.setBottom(ta);
		root.setCenter(tf);
		ta.setPrefHeight(200);
		tf.setMaxWidth(300);
		b.setTranslateX(-4);
		b.setTranslateY(99.5);
		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		b.setOnAction(e -> {
			Platform.runLater(() -> {
				try {
					out.writeUTF(tf.getText());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});

		});

		try {

			Socket socket = new Socket("127.0.0.1", 7878);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			new Thread(() -> {
				try {
					while (true) {
						serverMessage = in.readUTF();

							Platform.runLater(() -> {
							
								ta.appendText( serverMessage + "\n");
								
							
						});
					
					}
				} catch (IOException e) {
					ta.appendText(e.toString() + "\n");
				}

			}).start();
		} catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}
	}

	public static void main(String args[]) throws UnknownHostException, IOException {
		launch(args);

	}

	public void showMessage(String message) {

		Platform.runLater(() -> {
			ta.appendText(message + "\n");

		});
	}
}
