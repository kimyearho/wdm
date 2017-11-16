package com.nodestory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.nodestory.commons.Constants;
import com.nodestory.utils.AlertSupport;
import com.nodestory.utils.ApplicationAPI;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private Parent rootNode;
	private FXMLLoader fxmlLoader;

	public void init() throws IOException {
		fxmlLoader = new FXMLLoader(getClass().getResource("views/Main.fxml"));
		rootNode = fxmlLoader.load();
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		/* 앱 버전 */
		Properties prop = new Properties();
		FileInputStream fis = new FileInputStream(Constants.SystemMessage.PROPERTY_PATH);
		prop.load(new java.io.BufferedInputStream(fis));
		String current = prop.getProperty(Constants.SystemMessage.PROPERTY_APP_KEY);

		/* 업데이트 앱 버전 체크 */
		ApplicationAPI api = new ApplicationAPI();
		Map<String, String> result = api.appCheck();
		String now = result.get("new_version");

		if (current != null && now != null) {
			if (!current.equals(now)) {
				AlertSupport alert = new AlertSupport(Constants.AlertInfoMessage.VERSION_UPDATE_MESSAGE);
				int c = alert.alertConfirm();
				if (c == 1) {
					Runtime runTime = Runtime.getRuntime();
					try {
						primaryStage.close();
						runTime.exec(Constants.SystemMessage.UPDATE_APP_PATH);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// 업데이트를 하지 않을 때.
					run(primaryStage, current);
				}
			} else {
				// 최신버전일때
				run(primaryStage, current);
			}
		}
	}

	// 애플리케이션 실행
	private void run(Stage primaryStage, String current) {
		try {

			Scene scene = new Scene(rootNode);
			primaryStage.setTitle("웹툰 다운로드 매니저 FX  v" + current);
			primaryStage.getIcons().add(new Image("file:wdm.png"));
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.sizeToScene();
			primaryStage.show();

			// Stage 종료 이벤트
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					// 자바의 가상 윈도우 커맨드를 통해서, 크롬엔진 드라이버를 종료한다.
					Runtime runTime = Runtime.getRuntime();
					try {
						runTime.exec("taskkill /f /im phantomjs.exe");
						runTime.exec("taskkill /f /im javaw.exe");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
