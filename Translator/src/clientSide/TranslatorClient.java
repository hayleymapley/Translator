package clientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TranslatorClient extends Application {

	private int startWidth = 600;
	private int startHeight = 400;
	
	private VBox titlePane = new VBox();
	private Text title = new Text();
	
	private Button translateButton = new Button();
	
	private BorderPane parentPane = new BorderPane();
	
	private VBox englishPane = new VBox();
	private Text english = new Text();
	private Text untranslatedText = new Text();
	private TextField textField = new TextField();
	
	private VBox spanishPane = new VBox();
	private Text spanish = new Text();
	private Text translatedText = new Text();
	
	@Override
	public void start(Stage primaryStage) {
		
		try {

			initialisePanes();
			
			translateButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					String token = textField.getText();
					textField.clear();
					untranslatedText.setText(token+"\n");
					try {
						String translated = requestTranslation(token);
						String fixedN = translated.replace("n~", "ñ");
						String fixedA = fixedN.replace("a/", "á");
						String fixedE = fixedA.replace("e/", "é");
						String fixedI = fixedE.replace("i/", "í");
						String fixedO = fixedI.replace("o/", "ó");
						String fixedFinal = fixedO.replace("u/", "ú");
						translatedText.setText(fixedFinal);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			Scene scene = new Scene(parentPane,startWidth,startHeight);

			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("Translator: English-Spanish");
			primaryStage.show();

		} catch(Exception e) {

			e.printStackTrace();
		}
		
	}
	
	public void initialisePanes() {
		
		title.setText("Translator\n");
		title.setTextAlignment(TextAlignment.CENTER);
		title.setFont(Font.font(30));
		
		english.setText("English\n");
		english.setFont(Font.font(25));
		
		untranslatedText.setText("\n");
		
		spanish.setText("Spanish\n");
		spanish.setFont(Font.font(25));
		
		translatedText.setText("[Translation will appear here]");
		
		translateButton.setText("Translate");
		translateButton.setDefaultButton(true);
		BooleanBinding booleanBinding = textField.textProperty().isEqualTo("");
		translateButton.disableProperty().bind(booleanBinding);
		
		parentPane.setPadding(new Insets(25,25,25,25));
		parentPane.setStyle("-fx-padding: 10;" +
				"-fx-border-style: solid inside;" +
				"-fx-border-width: 2;" +
				"-fx-border-insets: 5;" +
				"-fx-border-radius: 5;" +
				"-fx-border-color: blue;");
		
		titlePane.getChildren().add(title);
		
		englishPane.getChildren().addAll(english, untranslatedText, textField, translateButton);
		englishPane.setAlignment(Pos.TOP_CENTER);
		englishPane.setPadding(new Insets(25,25,25,25));
		VBox.setMargin(translateButton, new Insets(50,50,50,50));
		
		spanishPane.getChildren().addAll(spanish, translatedText);
		spanishPane.setAlignment(Pos.TOP_CENTER);
		spanishPane.setPadding(new Insets(25,25,25,25));
		
		parentPane.setTop(title);
		parentPane.setLeft(englishPane);
		parentPane.setCenter(spanishPane);
	}
	
	public static String requestTranslation(String token) throws IOException {
		
		String serverAddress = "127.0.0.1";
		int port = 9090;
		Socket s = new Socket(serverAddress, port);
		
		PrintWriter out =
				new PrintWriter(s.getOutputStream(), true);
		
		out.println(token);
		
		BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

		StringBuilder answerBuilder = new StringBuilder();

		String line = "";
		
		while ((line = input.readLine()) != null) {
			answerBuilder.append(line);
			answerBuilder.append('\n');
		}

		String answer = answerBuilder.toString();
		
		s.close();
		
		return answer;
	}

	public static void main(String[] args) {
		
		launch(args);
	}
}
