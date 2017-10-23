package com.nodestory.controller;

import com.nodestory.service.NaverWebtoonSelectService;
import com.nodestory.utils.AlertSupport;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class NaverWebtoonSelectController {

	private MainController root;

	@FXML
	private TableView<NaverWebtoonSelectService> table;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellMonday;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellTuesday;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellWednesday;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellThursday;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellFriday;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellSaturday;

	@FXML
	private TableColumn<NaverWebtoonSelectService, String> cellSunday;

	@FXML
	private TextField cellValue;

	@FXML
	private Button checkButton;

	@FXML
	private TextField filterField;

	private Stage dialogStage;

	public void setDialogStageStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainController(MainController mainController) {
		root = mainController;
	}

	@SuppressWarnings("rawtypes")
	public void showWebtoonListDialog(ObservableList<NaverWebtoonSelectService> data) {
		try {

			// cell init
			cellMonday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("monday"));
			cellTuesday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("tuesday"));
			cellWednesday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("wednesday"));
			cellThursday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("thursday"));
			cellFriday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("friday"));
			cellSaturday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("saturday"));
			cellSunday.setCellValueFactory(new PropertyValueFactory<NaverWebtoonSelectService, String>("sunday"));

			// selection for single cells instead of single
			table.getSelectionModel().setCellSelectionEnabled(true);
			table.setItems(data);

			// 셀 추적
			final ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
			TableViewSelectionModel<NaverWebtoonSelectService> selectionModel = table.getSelectionModel();

			// 셀 체인지 이벤트
			selectedCells.addListener(new ListChangeListener<TablePosition>() {
				@Override
				public void onChanged(Change change) {
					for (TablePosition pos : selectedCells) {
						switch (pos.getColumn()) {
						case 0: // 월
							cellValue.setText(selectionModel.getSelectedItem().getMonday());
							break;
						case 1: // 화
							cellValue.setText(selectionModel.getSelectedItem().getTuesday());
							break;
						case 2: // 수
							cellValue.setText(selectionModel.getSelectedItem().getWednesday());
							break;
						case 3: // 목
							cellValue.setText(selectionModel.getSelectedItem().getThursday());
							break;
						case 4: // 금
							cellValue.setText(selectionModel.getSelectedItem().getFriday());
							break;
						case 5: // 토
							cellValue.setText(selectionModel.getSelectedItem().getSaturday());
							break;
						case 6: // 일
							cellValue.setText(selectionModel.getSelectedItem().getSunday());
							break;
						}
					}
				};
			});
			// 동적 셀 리스트 변환
			ObservableList<NaverWebtoonSelectService> data1 = table.getItems();
			filterField.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						if (oldValue != null && (newValue.length() < oldValue.length())) {
							table.setItems(data1);
						}
						String value = newValue.toLowerCase();
						ObservableList<NaverWebtoonSelectService> subentries = FXCollections.observableArrayList();
						long count = table.getColumns().stream().count();
						for (int i = 0; i < table.getItems().size(); i++) {
							for (int j = 0; j < count; j++) {
								String entry = "" + table.getColumns().get(j).getCellData(i);
								String parseTitle = entry.split("-")[0];
								if (replaceStrAll(parseTitle).toLowerCase().contains(replaceStrAll(value))) {
									subentries.add(table.getItems().get(i));
									break;
								}
							}
						}
						table.setItems(subentries);
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void closeDialogData() {
		if(!cellValue.getText().equals("")) {
			dialogStage.close();
			
			root.getCellValue(cellValue.getText());
		} else {
			AlertSupport alert = new AlertSupport("웹툰을 선택하세요.");
			alert.alertInfoMsg();
		}
	}

	public String replaceStrAll(String text) {
		text = text.replaceAll(" ", "");
		return text;
	}

}
