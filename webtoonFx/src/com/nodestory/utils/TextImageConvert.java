package com.nodestory.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextImageConvert {

	// 다운로드경로, 파일명, 웹툰코드, 현재편수
	public String textToImage(String downDir, String fileName, String webCode, int ep) {
		
		String s_ep = String.valueOf(ep);

		// 파싱할 웹툰 페이지에 베댓을 가져온다. 웹툰코드와 해당편수코드를 전달한다.
		WebtoonCommentsParser parser = new WebtoonCommentsParser();
		String sb = parser.webtoonComments(webCode, s_ep);
		System.out.println(sb.toString().length());

		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();

		Font font = new Font("맑은 고딕", Font.PLAIN, 14);
		g2d.setFont(font);

		FontMetrics fm = g2d.getFontMetrics();
		int width = 690, height = 650;
		g2d.dispose();

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);

		fm = g2d.getFontMetrics();
		g2d.setBackground(Color.BLACK);
		g2d.setColor(Color.WHITE);

		// 그리기
		drawString(g2d, sb, 0, 0);
		g2d.dispose();
		
		String s_fileName = fileName.trim() + "_s.jpg";

		try {
			// 파일생성
			ImageIO.write(img, "jpg", new File(downDir + File.separator + s_fileName));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return s_fileName;
	}

	/**
	 * 문자열을 이미지로 변환한다.
	 */
	public static void drawString(Graphics g, String text, int x, int y) {
		for (String line : text.split("\n")) {
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
		}
	}

}
