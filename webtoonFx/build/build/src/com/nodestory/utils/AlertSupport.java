package com.nodestory.utils;

import java.util.Optional;

import com.nodestory.controller.MainController;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/*
 * Alert 다이얼로그를 사용하기 위한 클래스 
 */
public class AlertSupport {

	private String msg = "";
	private MainController root;

	public AlertSupport(String msg) {
		this.msg = msg;
	}

	public AlertSupport(MainController root) {
		this.root = root;
	}

	// info 타입 Alert 다이얼로그
	public void alertInfoMsg() {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림");
		alert.setHeaderText(null);
		alert.setContentText(this.msg);
		alert.showAndWait();

	}

	// error 타입 Alert 다이얼로그
	public void alertErrorMsg() {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Warning Dialog");
		alert.setHeaderText(null);
		alert.setContentText(this.msg);
		alert.showAndWait();

	}

	// error 타입 Alert 다이얼로그
	public int alertErrorConfirm() {

		int r = 0;

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("경고");
		alert.setHeaderText(null);
		alert.setContentText(this.msg);
		alert.showAndWait();

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			r = 1;
		}

		return r;

	}

	// 컨펌 다이얼로그
	public int alertConfirm() {
		int r = 0;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("확인 알림");
		alert.setHeaderText(null);
		alert.setContentText(this.msg);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			r = 1;
		}

		return r;
	}

	// 다운로드 모달을 띄운다.
	public void showDownloadModal() {
		root.stackpane.setVisible(true);
		root.stackpane.setOpacity(1);
	}

	public void closeDownloadModal() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				root.bar.setProgress(1);
			}
		});
		root.btnCancel.setDisable(false);
	}

}
