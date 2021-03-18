package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	private static SecretKeySpec secretKey;
	private static byte[] key;

	public void Message(String x) {

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
		tf.setOnKeyPressed(this::enter);
		butt.setTranslateX(-4);
		butt.setTranslateY(99.5);
		butt.setOnAction(this::click);
		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		ta.setEditable(false);
		startServer();

	}

	public void startServer() {
		t = new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(port);// please replace port with a port number
					Platform.runLater(() -> ta.appendText("Server started... " + "\n"));

					Socket socket = serverSocket.accept();

					Platform.runLater(() ->

					ta.appendText("Client accepted ! " + "\n"));
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());

					while (true) {
						String secretKey = "asdasdsada";
						// Receive message from the client
						String message = in.readUTF();
						String message1 = Server.decrypt(message, secretKey);
						Platform.runLater(() -> {
							ta.appendText("Client: " + message1 + "\n");
						});
					}

				} catch (IOException i) {
					i.printStackTrace();
				}
			}
		});
		t.start();

	}

	public void enter(KeyEvent ke) {
		if (ke.getCode().equals(KeyCode.ENTER)) {
			Platform.runLater(() -> {
				try {
					String secretKey = "asdasdsada";
					// to Client
					String msg = tf.getText();
					String msg1 = Server.encrypt(msg, secretKey);

					out.writeUTF(msg1);

					Message("Server: " + msg + "\n");
					tf.clear();

				} catch (IOException i) {
					i.printStackTrace();
				}
			});
		}
	}

	public void click(ActionEvent event) {

		Platform.runLater(() -> {
			try {
				String secretKey = "asdasdsada";
				// to Client
				String msg = tf.getText();
				String msg1 = Server.encrypt(msg, secretKey);

				out.writeUTF(msg1);

				Message("Server: " + msg + "\n");
				tf.clear();

			} catch (IOException i) {
				i.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {

		launch(args);

	}

	public static String encrypt(String text, String s) {
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] x = md.digest(s.getBytes(StandardCharsets.UTF_8));
			key = Arrays.copyOf(x, 16);// block size:16 bytes
			secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] Text = cipher.doFinal(text.getBytes());
			return Base64.getEncoder().encodeToString(Text);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	public static String decrypt(String text, String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] x = md.digest(s.getBytes(StandardCharsets.UTF_8));
			key = Arrays.copyOf(x, 16);// block size:16 bytes

			secretKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] Text = cipher.doFinal(Base64.getDecoder().decode(text));
			return new String(Text);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

}
