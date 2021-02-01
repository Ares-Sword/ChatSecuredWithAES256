package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

	private TextField tf = new TextField();
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private TextArea ta = new TextArea();
	private Button butt = new Button("Send");

	@Override 
	public void start(Stage primaryStage) {

		BorderPane root = new BorderPane();
		root.setTop(butt);
		root.setBottom(ta);
		root.setCenter(tf);
		ta.setPrefHeight(200);
		tf.setMaxWidth(300);
		butt.setTranslateX(-4);
		butt.setTranslateY(99.5);
		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

		butt.setOnAction(e -> {

			Platform.runLater(() -> {
				try {

					out.writeUTF(tf.getText());
					showMessage("server: " + tf.getText() + "\n");

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		});

		ta.setEditable(false);
		new Thread(() -> {
			try {
		
				serverSocket = new ServerSocket(7878);
				Platform.runLater(() -> ta.appendText("Server started at " + "\n"));
				Socket socket = serverSocket.accept();
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());

				while (true) {
					// Receive message from the client
					String message = in.readUTF();

					out.writeUTF(message);
					
					Platform.runLater(() -> {
						ta.appendText("Client: " + message + "\n");
					});
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}).start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public void showMessage(String message) {

		Platform.runLater(() -> {
			ta.appendText(message);

		});
	}

}
