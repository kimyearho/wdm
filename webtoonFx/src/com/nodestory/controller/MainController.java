package com.nodestory.controller;

import com.nodestory.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {

	@FXML
	NaverEventController naverEventController;

	@FXML
	NaverWebtoonSelectController naverWebtoonSelectController;
	
	@FXML
	ManualController manualController;

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
	
	/**
	 *  웹툰선택 팝업에서 가져온 데이터
	 * 
	 * @param title
	 * @param code
	 */
	public void getCellValue(String title, String code) {
		{
			// 웹툰코드 세팅
			naverEventController.codeField.setText(code);
			
			// 웹툰제목 세팅
			naverEventController.titleField.setText(title);

			// 편수 세팅
			naverEventController.episodeSelect(code);
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

			FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/Notice.fxml"));
			StackPane page = (StackPane) loader.load();

			Stage dialog = new Stage();
			Scene scene = new Scene(page);
			dialog.getIcons().add(new Image("file:wdm.png"));
			dialog.setTitle("공지사항");
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.setResizable(false);
			dialog.setScene(scene);
			dialog.setAlwaysOnTop(true);
			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@FXML
	public void showManualCode() {
		{
			try {

				FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ManualCode.fxml"));
				AnchorPane page = (AnchorPane) loader.load();

				Stage dialog = new Stage();
				Scene scene = new Scene(page);
				dialog.getIcons().add(new Image("file:wdm.png"));
				dialog.setTitle("웹툰코드 직접등록");
				dialog.initModality(Modality.WINDOW_MODAL);
				dialog.setResizable(false);
				dialog.setAlwaysOnTop(true);
				dialog.setScene(scene);

				dialog.show();
				
				ManualController manualController = loader.getController();
				manualController.setMainController(this);
				manualController.setDialogStageStage(dialog);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
