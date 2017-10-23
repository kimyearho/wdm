package com.nodestory.commons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.github.axet.wget.WGet;

import javafx.scene.control.TextArea;

public class CommonService {

	/**
	 * 네이버 웹툰을 다운로드 받기 위해 연결함.
	 * 
	 * @param webCode
	 *            - 웹툰코드
	 * @param i
	 *            - 편수
	 * @return
	 */
	public Connection getConnection(String webCode, int i) {
		return Jsoup.connect("http://comic.naver.com/webtoon/detail.nhn?titleId=" + webCode + "&no=" + i);
	}
	
	/**
	 * 네이버 웹툰 연결
	 * 
	 * @param webCode
	 * @return
	 */
	public Connection getConnection(String webCode) {
		return Jsoup.connect("http://comic.naver.com/webtoon/detail.nhn?titleId=" + webCode);
	}

	/**
	 * wGet 모듈을 이용해 다운로드 구현
	 * 
	 * @param imgUrl
	 *            - 이미지 URL
	 * @param path
	 *            - 다운로드 경로
	 */
	public void wgetDirectDownload(String imgUrl, String path) {
		int slashIndex = imgUrl.lastIndexOf('/');
		String fileName = imgUrl.substring(slashIndex + 1);
		try {

			URL url = new URL(imgUrl);
			File target = new File(path + File.separator + fileName);
			WGet w = new WGet(url, target);
			w.download();
		} catch (MalformedURLException e) {
		} catch (RuntimeException allDownloadExceptions) {
			allDownloadExceptions.printStackTrace();
		}

	}

	/**
	 * 조각난 이미지들을 하나로 합치는 메소드
	 * 
	 * @param imgList
	 *            - 이미지주소들이 담겨있는 리스트
	 * @param downloadDir
	 *            - 조각난 이미지들 다운로드 위치
	 * @param title
	 *            - 웹툰의 제목
	 */
	public boolean webtoonMergeStart(List<String> imgList, 
			String downloadDir, String title, String s_fileName, int ep, TextArea log) {
		boolean flag = false;
		List<String> fileList = new ArrayList<String>();

		try {
			int maxheight = 0;
			String fileName = "";

			for (int i = 0; i < imgList.size(); i++) {
				if (!imgList.get(i).matches(".*_s.*")) {
					fileName = ((String) imgList.get(i)).toString()
							.substring(((String) imgList.get(i)).toString().lastIndexOf('/') + 1);
				} else {
					fileName = s_fileName;
				}

				BufferedImage bufferFiles = ImageIO.read(new File(downloadDir + File.separator + fileName));
				int height = bufferFiles.getHeight();

				maxheight += height;
				fileList.add(fileName);
			}
			BufferedImage image = null;
			if (fileList.size() > 3) {
				image = ImageIO.read(new File(downloadDir + File.separator + (String) fileList.get(1)));
			} else {
				image = ImageIO.read(new File(downloadDir + File.separator + (String) fileList.get(0)));
			}
			BufferedImage canvasImage = new BufferedImage(image.getWidth(), maxheight, 1);
			int max = 0;
			for (int i = 0; i < fileList.size(); i++) {
				BufferedImage bufferImage = ImageIO
						.read(new File(downloadDir + File.separator + (String) fileList.get(i)));
				Graphics2D graphics = (Graphics2D) canvasImage.getGraphics();
				graphics.setBackground(Color.WHITE);
				if (i == 0) {
					graphics.drawImage(bufferImage, 0, 0, null);
				} else {
					int height = ImageIO.read(new File(downloadDir + File.separator + (String) fileList.get(i - 1)))
							.getHeight();
					max += height;
					graphics.drawImage(bufferImage, 0, max, null);
				}
			} // end for
			String saveImg = ep + "화 - " + title.trim() + ".png";
			ImageIO.write(canvasImage, "PNG", new File(downloadDir + File.separator + saveImg));

			File[] originalFiles = new File(downloadDir).listFiles();
			for (int i = 0; i < originalFiles.length; i++) {
				if ((!originalFiles[i].toString().equals(downloadDir + File.separator + saveImg))
						&& (!originalFiles[i].getName().matches(".*" + title.trim() + ".*"))
						|| originalFiles[i].toString().equals(downloadDir + File.separator + s_fileName)) {
					originalFiles[i].delete();
				}
			}
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

}
