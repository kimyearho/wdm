package com.nodestory.controller;

import com.nodestory.Main;
import com.nodestory.utils.AlertSupport;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

	@FXML
	NaverEventController naverEventController;

	@FXML
	NaverWebtoonSelectController naverWebtoonSelectController;

	@FXML
	public StackPane stackpane;

	@FXML
	public Button btnCancel;
	
	@FXML
	public ProgressIndicator bar;

	@FXML
	private void initialize() {
		naverEventController.init(this);
	}

	/**
	 * 웹툰선택 팝업에서 가져온 셀 데이터 및 편수 세팅
	 * 
	 * @param cellValue
	 */
	public void getCellValue(String cellValue) {
		{
			// 웹툰코드 세팅
			naverEventController.codeField.setText(cellValue.split("-")[1]);
			
			// 웹툰제목 세팅
			naverEventController.titleField.setText(cellValue.split("-")[0]);

			// 편수 세팅
			naverEventController.episodeSelect(cellValue.split("-")[1]);
		}
	}
	
	// 모달 닫기
	public void closeModal() {
		stackpane.setVisible(false);
		stackpane.setOpacity(0);
		btnCancel.setDisable(true);
		bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	}
	
	@FXML
	public void showNoticeDialog() {
		
		try {

			// 다이얼로그 FXML 로드
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/Notice.fxml"));
			StackPane page = (StackPane) loader.load();

			Stage dialog = new Stage();
			Scene scene = new Scene(page);
			dialog.setTitle("공지사항");
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.setResizable(false);
			dialog.setScene(scene);

			// 다이얼로그를 보여준다.
			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@FXML
	public void showFeedback() {
		{
			AlertSupport alert = new AlertSupport("준비 중 입니다.");
			alert.alertInfoMsg();
		}
	}
}
