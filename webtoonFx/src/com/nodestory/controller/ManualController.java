package com.nodestory.controller;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import com.nodestory.commons.CommonService;
import com.nodestory.utils.AlertSupport;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class ManualController {
	
	public MainController root;
	
	public Stage stage;
	
	public String title;

	public void setMainController(MainController mainController) {
		root = mainController;
	}
	
	public void setDialogStageStage(Stage dialogStage) {
		this.stage = dialogStage;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@FXML
	public TextField codeInputField;
	
	@FXML
	public Label wTitle;
	
	@FXML
	public TextArea wDesc;
	
	@FXML
	public ImageView thumbnail;
	
	@FXML
	public WebView webview;
	
	/**
	 * 웹툰조회
	 */
	public void getWebtoon() {
		
		String codeText = codeInputField.getText();
		
		if(!"".equals(codeText)) {
			CommonService cs = new CommonService();
			
			Connection conn = cs.getConnection(codeText);
			conn.timeout(5000);
			
			Document doc = null;
			
			wDesc.setWrapText(true);
			
			try {
				
				doc = conn.get();
				
				String title = doc.select("title").text().split("::")[0];
				setTitle(title);
				
				String author = doc.select("div.detail h2 > span").text();
				wTitle.setText(title + "(" + author + ")");
				
				String desc = doc.select("div.detail p").text();
				wDesc.setText(desc);
				
				String img = doc.select("div.thumb > a img").attr("src");
				thumbnail.setImage(new Image(img, true));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					AlertSupport alert = new AlertSupport("웹툰코드를 입력하세요.");
					alert.alertInfoMsg(stage);
				}
			});
		}
	}
	
	/**
	 * 네이버 웹툰으로 이동
	 * 이슈1: fxml 경로 이슈가 있어 코드로 디자인구성 
	 */
	public void getWebtoonSite() {
		
		try {
			
			StackPane root = new StackPane();
			AnchorPane anchorPane = new AnchorPane();
			root.getChildren().add(anchorPane);
			
			TextField field = new TextField();
			field.setPrefWidth(1280);
			field.setEditable(false);
			field.setFocusTraversable(false);

	        WebView view = new WebView();
	        view.setMaxSize(1275, 810);
	        
	        WebEngine engine = view.getEngine();
	        engine.load("http://comic.naver.com/index.nhn");
	        
	        root.getChildren().add(view);
	        anchorPane.getChildren().add(field);
	        
	        engine.load("http://comic.naver.com/index.nhn");
	        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
	        	if (Worker.State.SUCCEEDED.equals(newValue)) {
	        		field.setText(engine.getLocation());
	        	}
	        });
	        
	        Scene scene = new Scene(root, 1270, 850);
	        Stage stage = new Stage();
	        stage.setScene(scene);
	        stage.setTitle("네이버 웹툰");
	        stage.setAlwaysOnTop(true);
	        stage.setResizable(false);
	        stage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 창 닫기
	 */
	public void getWebtoonClose() {
		if(!codeInputField.getText().equals("") ) {
			stage.close();
			root.getCellValue(this.title, codeInputField.getText());
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					AlertSupport alert = new AlertSupport("웹툰코드를 입력하세요.");
					alert.alertInfoMsg(stage);
				}
			});
		}
	}

}
