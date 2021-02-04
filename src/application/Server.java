package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class Server extends Application {

	private TextField tf = new TextField();
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private TextArea ta = new TextArea();
	private Button butt = new Button("Send");
	private Thread t;

	void Msg(String x) {

		Platform.runLater(() -> {
			ta.appendText(x);

		});
	}

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
		butt.setOnAction(e -> {

			Platform.runLater(() -> {
				try {

					out.writeUTF(tf.getText());
					Msg("server " + tf.getText() + "\n");

				} catch (IOException i) {
					i.printStackTrace();
				}
			});
		});

		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		ta.setEditable(false);

		t = new Thread(new Runnable() {
			public void run() {
				try {

					serverSocket = new ServerSocket(7878);
					Platform.runLater(() -> ta.appendText("Server started ! " + "\n"));
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
				} catch (IOException i) {
					i.printStackTrace();
				}
			}
		});
		t.start();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
