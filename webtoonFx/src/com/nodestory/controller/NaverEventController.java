package com.nodestory.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


import org.apache.poi.ss.formula.functions.T;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.nodestory.Main;
import com.nodestory.commons.CommonService;
import com.nodestory.commons.Constants;
import com.nodestory.service.NaverWebtoonSelectService;
import com.nodestory.utils.AlertSupport;
import com.nodestory.utils.ButtonCell;
import com.nodestory.utils.ExcelReader;
import com.nodestory.utils.TextImageConvert;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class NaverEventController extends ListCell<T> implements Initializable {

	public MainController root;

	public void init(MainController mainController) {
		root = mainController;
	}

	@FXML
	public TextField codeField;

	@FXML
	public TextField titleField;

	@FXML
	public TextField sEp;

	@FXML
	public TextField dEp;

	@FXML
	public TextArea log;

	@FXML
	public CheckBox folderCheck;

	@FXML
	public CheckBox bestCommentCheck;

	@FXML
	public CheckBox jobListCheck;

	@FXML
	public CheckBox systemCheck;

	@FXML
	public Button downloadBtn;

	@FXML
	public Button jobListAddBtn;

	@FXML
	public ListView<String> jobWebtoonList;

	// 리스트뷰에 보여질 맵 정보
	public Map<String, Object> jobMap = new LinkedHashMap<String, Object>();

	// 리스트뷰에 보여질 데이터 리스트
	public ObservableList<String> listItems = FXCollections.observableArrayList();

	// 임시로 저장하기 위한 리스트
	public List<Map<String, Object>> fakeList = new ArrayList<Map<String, Object>>();

	public boolean commentFlag = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 데이터 리스트를 동적으로 삽입하여 작업리스트에 보여준다.
		jobWebtoonList.setItems(listItems);

		// 작업리스트에 추가한 웹툰을 버튼형태로 만들어 삽입한다.
		jobWebtoonList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> list) {
				return new ButtonCell(fakeList, jobWebtoonList, downloadBtn);
			}
		});
	}

	@FXML
	public void systemShutDown() {

		if (systemCheck.isSelected()) {

			AlertSupport alert = new AlertSupport(
					"이 기능을 사용하면 웹툰 다운로드 완료 시 시스템(컴퓨터)를 자동종료 합니다. 많은 편수의 웹툰을 다운로드 받을 때 유용합니다. 이 기능을 사용 하시겠습니까?");
			int count = alert.alertConfirm();
			if (count > 0) {
				systemCheck.setSelected(true);
			} else {
				systemCheck.setSelected(false);
			}
		}

	}

	@FXML
	public void jobListConfirm() {
		if (jobListCheck.isSelected()) {
			jobListAddBtn.setDisable(false);
			jobWebtoonList.setDisable(false);
			downloadBtn.setDisable(true);
			downloadBtn.setText("전체 다운로드");
		} else {

			if (fakeList.size() > 0) {
				AlertSupport alert = new AlertSupport("예약리스트를 비워주세요.");
				int count = alert.alertConfirm();
				if (count > 0) {
					jobListCheck.setSelected(true);
				} else {
					jobListCheck.setSelected(true);
				}
			} else {
				jobListAddBtn.setDisable(true);
				jobWebtoonList.setDisable(true);
				downloadBtn.setDisable(false);
				downloadBtn.setText("단일 다운로드");
			}
		}
	}

	@FXML
	public void commentConfirm() {
		if (bestCommentCheck.isSelected()) {
			AlertSupport alert = new AlertSupport(
					"베스트 댓글을 추가 하시겠습니까?\n배댓추가시 다운로드 속도가 조금 느려지며, 배댓 개행처리가 안되므로 긴글은 짤리게됩니다. \n\n(이 기능은 현재 베타버전입니다)");
			int count = alert.alertConfirm();
			if (count > 0) {
				// code
				commentFlag = true;
			} else {
				bestCommentCheck.setSelected(false);
			}
		} else {
			commentFlag = false;
		}
	}

	@FXML
	public void resetdBtn() {
		// 모든 항목을 클리어 한다.
		codeField.clear();
		titleField.clear();
		sEp.clear();
		dEp.clear();
		folderCheck.setSelected(false);
		bestCommentCheck.setSelected(false);
		jobWebtoonList.getItems().clear();
		jobWebtoonList.setDisable(true);
		fakeList.clear();
		jobListCheck.setSelected(false);

		jobListAddBtn.setDisable(true);
		downloadBtn.setText("단일 다운로드");
	}

	@FXML
	public void addJobList() {

		// 리스트 사이즈가 0보다 크면
		if (fakeList.size() > 0) {

			boolean flag = false;

			// 실제 데이터를 담아둘 맵 정보
			Map<String, Object> fakeMap = new LinkedHashMap<String, Object>();
			fakeMap.put("webtoonTitle", titleField.getText());
			fakeMap.put("webtoonCode", codeField.getText());
			fakeMap.put("webtoonStartEp", sEp.getText());
			fakeMap.put("webtoonEndEp", dEp.getText());

			// 1. fakeList에 저장된 퉵툰코드와, 등록하고자 하는 웹툰코드가 있는지 체크한다.
			for (int i = 0; i < fakeList.size(); i++) {
				Map<String, Object> fakeData = fakeList.get(i);
				if (fakeData.get("webtoonCode").equals(codeField.getText())) {
					AlertSupport alert = new AlertSupport(Constants.AlertInfoMessage.DUPLICATED_WEBTOON);
					alert.alertInfoMsg();
					flag = false;
					break;
				} else {
					flag = true;
				}
			}

			if (flag) {
				// 리스트뷰에 보여질 제목 데이터
				jobMap.put("webtoonTitle", titleField.getText());
				jobMap.put("sEp", sEp.getText());
				jobMap.put("dEp", dEp.getText());
				listItems.add(String.valueOf(jobMap.get("webtoonTitle") + " ("
						+ String.valueOf(jobMap.get("sEp") + " ~ " + String.valueOf(jobMap.get("dEp") + ")"))));

				// 리스트 추가
				fakeList.add(fakeMap);
			}

		} else {

			if (sEp.getText().equals("") || dEp.getText().equals("")) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						AlertSupport alert = new AlertSupport("시작편수나, 종료편수는 필수값입니다.\n웹툰을 선택하세요.");
						alert.alertInfoMsg();
					}
				});
			} else {

				// 작업리스트에 최소 1개이상 추가 된다면 버튼 활성화
				downloadBtn.setDisable(false);

				// 최초 입력시 실행
				jobMap.put("webtoonTitle", titleField.getText());
				jobMap.put("sEp", sEp.getText());
				jobMap.put("dEp", dEp.getText());
				listItems.add(String.valueOf(jobMap.get("webtoonTitle") + " ("
						+ String.valueOf(jobMap.get("sEp") + " ~ " + String.valueOf(jobMap.get("dEp") + ")"))));

				// 실제 데이터를 담아둘 맵 정보
				Map<String, Object> fakeMap = new LinkedHashMap<String, Object>();
				fakeMap.put("webtoonTitle", titleField.getText());
				fakeMap.put("webtoonCode", codeField.getText());
				fakeMap.put("webtoonStartEp", sEp.getText());
				fakeMap.put("webtoonEndEp", dEp.getText());
				fakeList.add(fakeMap);
			}

		}

	}

	@FXML
	public void showHandleDialog() {
		try {

			// 다이얼로그 FXML 로드
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/NaverWebtoon.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialog = new Stage();
			Scene scene = new Scene(page);
			dialog.setTitle("웹툰을 선택하세요.");
			dialog.getIcons().add(new Image("file:wdm.png"));
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.setResizable(false);
			dialog.setScene(scene);

			NaverWebtoonSelectService nav = null;
			List<NaverWebtoonSelectService> outputList = new ArrayList<NaverWebtoonSelectService>();
			List<Map<String, String>> webtoonList = ExcelReader.getExcelReader();

			for (int i = 0; i < webtoonList.size(); i++) {

				nav = new NaverWebtoonSelectService();
				Map<String, String> excelData = webtoonList.get(i);

				nav.setMonday(excelData.get("monday"));
				nav.setTuesday(excelData.get("tuesday"));
				nav.setWednesday(excelData.get("wednesday"));
				nav.setThursday(excelData.get("thursday"));
				nav.setFriday(excelData.get("friday"));
				nav.setSaturday(excelData.get("saturday"));
				nav.setSunday(excelData.get("sunday"));

				outputList.add(nav);

			}

			// 파싱한 데이터를 옵저블리스트에 담는다.
			ObservableList<NaverWebtoonSelectService> data = FXCollections.observableArrayList(outputList);

			// 서비스 컨트롤에 테이블에 보여주기 위한 데이터를 세팅한다.
			NaverWebtoonSelectController nwsController = loader.getController();
			nwsController.setDialogStageStage(dialog);
			nwsController.setMainController(root);
			nwsController.showWebtoonListDialog(data);

			// 다이얼로그를 보여준다.
			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 웹툰별 에피소드 입력
	 * 
	 * @param webCode
	 *            - 웹툰코드
	 */
	public void episodeSelect(String webCode) {
		try {

			Document document = Jsoup.connect("http://comic.naver.com/webtoon/list.nhn?titleId=" + webCode).get();
			Elements elements = document.select("ul.btn_group li").eq(1);
			String[] parseTag = elements.html().split("return");

			int index = parseTag[1].indexOf(")");
			String functionName = parseTag[1].substring(0, index);

			String firstEpisode = functionName.split(",")[1].replaceAll("'", "");
			sEp.setText(firstEpisode);

			elements = document.select("table.viewList > tbody > tr > td.title a").eq(0);
			String href = elements.attr("href");
			if ((!href.equals("")) || (href == null)) {
				String real_ep = href.split("no=")[1].split("&")[0];
				elements = document.select(".detail p");
				dEp.setText(real_ep);
			}

			sEp.setEditable(true);
			dEp.setEditable(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void webtoonDownload() {

		if (sEp.getText().equals("") || dEp.getText().equals("")) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					AlertSupport alert = new AlertSupport("시작편수나, 종료편수는 필수값입니다.\n웹툰을 선택하세요.");
					alert.alertInfoMsg();
				}
			});
		} else {

			AlertSupport info = new AlertSupport("이 웹툰으로 다운로드 하시겠습니까?\n다운로드중에는 취소할 수 없습니다.");
			int count = info.alertConfirm();

			if (count > 0) {

				Task<Void> task = new Task<Void>() {
					public Void call() throws Exception {

						boolean merge_flag = false;

						AlertSupport alert = new AlertSupport(root);

						// UI change code
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								alert.showDownloadModal();
							}
						});

						if (fakeList.size() == 0) {
							// 단일 다운로드

							int start = Integer.parseInt(sEp.getText());
							int end = Integer.parseInt(dEp.getText());

							String webCode = codeField.getText();
							String title = titleField.getText();
							
							CommonService cs = new CommonService();
							System.out.println(start);
							System.out.println(end);

							for (int i = start; i <= end; i++) {

								Connection con = cs.getConnection(webCode, i);
								con.timeout(5000);

								Document document = null;
								Elements elements = null;

								String dirPath = "";
								String path = "";

								List<String> hrefList = new ArrayList<String>();

								try {
									// HTML 파싱
									document = con.get();
									String fileName = i + "화" + " - " + title;
									if (!title.equals("네이버만화 : 네이버웹툰")) {
										path = new File(".").getCanonicalPath();
										if (!folderCheck.isSelected()) {
											dirPath = path + File.separator + "webtoons" + File.separator + title.trim()
													+ File.separator + fileName.trim();
										} else {
											dirPath = path + File.separator + "webtoons" + File.separator
													+ title.trim();
										}
										File root = new File(dirPath);
										if (!root.exists()) {
											root.mkdirs();
										}
										elements = document.getElementsByTag("img");
										for (Element element : elements) {
											String imgTag = element.attr("src").toString();
											if (imgTag.matches(".*http://imgcomic.*")) {
												String src = imgTag.replaceAll(".net", ".com");
												cs.wgetDirectDownload(src, root.getPath());
												hrefList.add(src);
											}
										}
										String s_fileName = "";
										if (commentFlag) {
											TextImageConvert convert = new TextImageConvert();
											s_fileName = convert.textToImage(dirPath, fileName, webCode, i);
											if (s_fileName != null) {
												hrefList.add(s_fileName);
											}
										}
										
										System.out.println(hrefList);
										
										merge_flag = cs.webtoonMergeStart(hrefList, dirPath, title, s_fileName, i, log);
										if (!merge_flag) {
											break;
										} else {
											if (i == end) {
												alert.closeDownloadModal();
												if (systemCheck.isSelected()) {
													String shutdownCmd = "shutdown -s";
													Runtime.getRuntime().exec(shutdownCmd);
												}
											}
										}
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							} // end for

						} else {
							// 예약 다운로드
							CommonService cs = new CommonService();

							for (int i = 0; i < fakeList.size(); i++) {

								Map<String, Object> jobMap = fakeList.get(i);

								int start = Integer.parseInt(String.valueOf(jobMap.get("webtoonStartEp")));
								int end = Integer.parseInt(String.valueOf(jobMap.get("webtoonEndEp")));

								String webCode = String.valueOf(jobMap.get("webtoonCode"));
								String title = String.valueOf(jobMap.get("webtoonTitle"));
								
								for (int k = start; k <= end; k++) {

									Connection con = cs.getConnection(webCode, k);
									con.timeout(5000);

									Document document = null;
									Elements elements = null;

									String dirPath = "";
									String path = "";

									List<String> hrefList = new ArrayList<String>();

									try {

										// HTML을 가져온다.
										document = con.get();

										String fileName = k + "화" + " - " + title;

										if (!title.equals("네이버만화 : 네이버웹툰")) {
											path = new File(".").getCanonicalPath();
											if (!folderCheck.isSelected()) {
												dirPath = path + File.separator + "webtoons" + File.separator
														+ title.trim() + File.separator + fileName.trim();
											} else {
												dirPath = path + File.separator + "webtoons" + File.separator
														+ title.trim();
											}

											File root = new File(dirPath);
											if (!root.exists()) {
												root.mkdirs();
											}

											elements = document.getElementsByTag("img");
											for (Element element : elements) {
												String imgTag = element.attr("src").toString();
												if (imgTag.matches(".*http://imgcomic.*")) {
													String src = imgTag.replaceAll(".net", ".com");
													cs.wgetDirectDownload(src, root.getPath());
													hrefList.add(src);
												}
											}

											String s_fileName = "";
											if (commentFlag) {
												TextImageConvert convert = new TextImageConvert();
												s_fileName = convert.textToImage(dirPath, fileName, webCode, k);

												if (s_fileName != null) {
													hrefList.add(s_fileName);
												}
											}
											merge_flag = cs.webtoonMergeStart(hrefList, dirPath, title, s_fileName, k, log);
											if (!merge_flag) {
												break;
											} 

										}

									} catch (Exception e) {
										e.printStackTrace();
									}

								} // end 2 for

								if (i == fakeList.size() - 1) {
									alert.closeDownloadModal();
									if (systemCheck.isSelected()) {
										String shutdownCmd = "shutdown -s";
										Runtime.getRuntime().exec(shutdownCmd);
									}
								}

							} // end 1 for

						}

						return null;
					}
				};

				Thread thread = new Thread(task);
				thread.start();

			}

		}

	}

}
