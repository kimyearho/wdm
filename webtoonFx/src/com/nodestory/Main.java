package com.nodestory;

import java.io.IOException;
import java.util.Map;

import com.nodestory.utils.AlertSupport;
import com.nodestory.utils.ApplicationAPI;

import javafx.application.Application;
import javafx.application.Platform;
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
	
	/* API KEY */
	private static final String apiKey = "xdadbfxkjng7%^ikk2#a23sd^578-=_+-!@#%^12##$qeksmsk1";

	public void init() throws IOException {
		fxmlLoader = new FXMLLoader(getClass().getResource("views/Main.fxml"));
		rootNode = fxmlLoader.load();
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
					
		/* 앱 버전 체크 */
		ApplicationAPI api = new ApplicationAPI();
		Map<String, String> value = api.appCheck(apiKey);
		
		// 앱 버전
		String version  = value.get("version");
		
		// 1: 정상, 0: 사용 불가능
		int count = Integer.parseInt(String.valueOf(value.get("count")));
		if (count ==  0) {
			AlertSupport alert = new AlertSupport("이 버전은 사용할 수 없습니다.\n새 버전을 다운로드 받아주세요.");
			int check = alert.alertErrorConfirm();
			if (check > 0) {
				Platform.exit();
			}
		}
		
		try {

			Scene scene = new Scene(rootNode);
			primaryStage.setTitle("웹툰 다운로드 매니저 FX  v" + version);
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
