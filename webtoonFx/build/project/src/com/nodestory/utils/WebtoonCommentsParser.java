package com.nodestory.utils;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * 웹툰 코멘트 파싱
 */
public class WebtoonCommentsParser {

	public String webtoonComments(String webCode, String ep) {

		WebDriver webDriver = null;
		StringBuffer sb = null;

		try {

			// PhantomJSDriver 사용하기위한 설정
			DesiredCapabilities DesireCaps = new DesiredCapabilities();
			DesireCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "c:\\wdmFx\\supports\\phantomjs.exe");

			webDriver = new PhantomJSDriver(DesireCaps);
			webDriver.manage().window().setSize(new Dimension(1920, 1080));
			webDriver.get("http://comic.naver.com/comment/comment.nhn?titleId=" + webCode + "&no=" + ep);
			Thread.sleep(3000);

			WebElement ulNode = webDriver.findElement(By.xpath("//ul[@class='u_cbox_list']"));
			List<WebElement> liNode = ulNode.findElements(By.tagName("li"));

			sb = new StringBuffer();
			sb.append("\n");
			sb.append("=============================== [BEST 댓글] ===============================\n");
			sb.append("\n");
			for (int i = 0; i < liNode.size(); i++) {
				String li = liNode.get(i).getText();
				String[] node = li.split("\n");
				if (!node[0].matches(".*[(].*")) {
					String comment = i + 1 + ". " + node[0] + node[1] + ": " + node[2].split("BEST")[1] + "\n";
					System.out.println(comment);
					sb.append(comment);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webDriver.quit();
		}
		return sb.toString();
	}

}
