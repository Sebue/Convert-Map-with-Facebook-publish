/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Media;
import facebook4j.PhotoUpdate;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import model.FacebookHelper;
import model.MapConverter;

public class MainWindowController implements Initializable {
	File wynik;
	File selectedFile;
	@FXML
	ImageView inputImage;
	@FXML
	ImageView outputImage;
	@FXML
	ProgressBar progressBar;
	
	Facebook fejs;
    
    @FXML
    private void handleGenerate(ActionEvent event) {
		BufferedImage mapa;

		try {
			mapa = ImageIO.read(selectedFile);
			MapConverter mapka = new MapConverter(mapa);
			wynik = new File("wynik.png");
			ImageIO.write(mapka.getConvertedMap(), "png", wynik);
			Image img = new Image(wynik.toURI().toString());
			outputImage.setImage(img);
			fejs = new FacebookFactory().getInstance();
			showPopup();
			
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Incorrect used generator");
			alert.setContentText("Pick graphic file first!");
			alert.showAndWait();
		}
    }
    
    @FXML
    private void handlePath(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graphics files (*.gif, *.jpg, *.png)", "*.png", "*.jpg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Graphic File");
        selectedFile = fileChooser.showOpenDialog(Main.getStage());

        if (selectedFile != null) {
        	Image img = new Image(selectedFile.toURI().toString());
        	inputImage.setImage(img);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    public void showPopup() {
        JDialog dialog = new JDialog((Window)null);
        JFXPanel jfxPanel = new JFXPanel();
        
        dialog.add(jfxPanel);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(null);

        dialog.setVisible(true);
        showFBLogin(jfxPanel, dialog);
    }
    
    private void showFBLogin(JFXPanel jfxPanel, Window dialog) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.load(FacebookHelper.INSTANCE.getUrlToLogin());
        
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) ->{
        	connectWithFacebook(dialog, engine, newState);
        });
        
        jfxPanel.setScene(new Scene(new BorderPane(webView, null, null, null, null)));
        
    }

	private void connectWithFacebook(Window dialog, WebEngine engine, State newState) {
		if(newState == Worker.State.SUCCEEDED){
			org.w3c.dom.Document doc = engine.getDocument();
			if(doc.getDocumentURI().contains(FacebookHelper.LOGIN_SUCCESS_URL_RESPONSE_PATTERN)){
				String modifiedUrl = doc.getDocumentURI().replace(FacebookHelper.LOGIN_SUCCESS_URL_RESPONSE_PATTERN, "");
				modifiedUrl = modifiedUrl.substring(0, modifiedUrl.length()-4);
				fejs.setOAuthAccessToken(FacebookHelper.INSTANCE.getAccessToken(modifiedUrl));
				Media media = new Media(wynik);
				PhotoUpdate pu = new PhotoUpdate(media);
				pu.setMessage("Photo " + selectedFile.getName() + " was converted by RAD_Mapa.");
				try {
					fejs.postPhoto(pu);
				} catch (FacebookException e) {
					e.printStackTrace();
				}
				dialog.dispose();
			}
		}
	}    
}
