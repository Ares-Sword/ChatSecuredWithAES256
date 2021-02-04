package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client extends Application {
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private String WordsFromServer = "";
	private TextField tf = new TextField();
	private ServerSocket serverSocket;
	private Socket socket;
	private TextArea ta = new TextArea();
	private Button b = new Button("Send");
	private Thread t;

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
		b.setOnAction(e -> {
			Platform.runLater(() -> {
				try {
					out.writeUTF(tf.getText());

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});

		});
		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

		try {

			Socket socket = new Socket("127.0.0.1", 7878);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			t = new Thread(new Runnable() {
				public void run() {
					try {
						while (true) {
							WordsFromServer = in.readUTF();

							Platform.runLater(() -> {

								ta.appendText(WordsFromServer + "\n");

							});

						}
					} catch (IOException i) {
						ta.appendText(i.toString() + "\n");
					}
				}
			});
			t.start();

		} catch (IOException iex) {
			ta.appendText(iex.toString() + '\n');
		}

	}

	public static void main(String args[]) throws UnknownHostException, IOException {
		launch(args);

	}

}
