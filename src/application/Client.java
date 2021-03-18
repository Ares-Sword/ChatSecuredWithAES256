package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client extends Application {
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private TextField tf = new TextField();
	private ServerSocket serverSocket;
	private Socket socket;
	private TextArea ta = new TextArea();
	private Button b = new Button("Send");
	private Thread t;
	private static SecretKeySpec secretKey;
	private static byte[] key;
	private String WordsFromServer;

	public void Message(String x) {

		Platform.runLater(() -> {
			ta.appendText(x);

		});
	}

	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setTop(b);
		root.setBottom(ta);
		root.setCenter(tf);
		ta.setPrefHeight(200);
		ta.setEditable(false);
		tf.setMaxWidth(300);
		tf.setOnKeyPressed(this::enter);
		b.setTranslateX(-4);
		b.setTranslateY(99.5);
		b.setOnAction(this::click);
		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Client");
		primaryStage.show();
		startClient();

	}

	public void startClient() {
		try {

			Socket socket = new Socket("127.0.0.1", port);// please replace port with a port number
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			t = new Thread(new Runnable() {
				public void run() {
					try {
						while (true) {
							String secretKey = "asdasdsada";
							// Receive message from the server
							WordsFromServer = in.readUTF();
							String WordsFromServer1 = Client.decrypt(WordsFromServer, secretKey);
							Platform.runLater(() -> {

								ta.appendText(WordsFromServer1 + "\n");

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

	public void click(ActionEvent event) {
		Platform.runLater(() -> {
			try {
				String secretKey = "asdasdsada";
				// to Server
				String msg = tf.getText();
				String msg1 = Client.encrypt(msg, secretKey);
				out.writeUTF(msg1);
				Message("You: " + msg + "\n");
				tf.clear();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	public void enter(KeyEvent ke) {
		if (ke.getCode().equals(KeyCode.ENTER)) {
			Platform.runLater(() -> {
				try {
					String secretKey = "asdasdsada";
					// to Server
					String msg = tf.getText();
					String msg1 = Client.encrypt(msg, secretKey);
					out.writeUTF(msg1);
					Message("You: " + msg + "\n");
					tf.clear();

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public static void main(String args[]) throws UnknownHostException, IOException {
		launch(args);

	}

	public static String encrypt(String text, String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] x = md.digest(s.getBytes(StandardCharsets.UTF_8));
			key = Arrays.copyOf(x, 16); // block size:16 bytes
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
