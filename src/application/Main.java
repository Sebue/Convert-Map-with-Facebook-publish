package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	private static Stage stage;
	
	public static Stage getStage(){
		return stage;
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		stage = primaryStage;
		Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
