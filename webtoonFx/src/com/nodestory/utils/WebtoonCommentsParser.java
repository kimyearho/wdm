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
			for (int i = 0; i < liNode.size(); i++) {
				String li = liNode.get(i).getText();
				String[] node = li.split("\n");
				if (!node[0].matches(".*[(].*")) {
					String comment = "[B] " + node[0] + node[1] + ": " + node[2].split("BEST")[1] + "\n";
					sb.append(convertInlineMsg(comment));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webDriver.quit();
		}
		return sb.toString();
	}
	
	/**
	 * <pre>
	 *     입력한 메시지의 문자열을 일정크기마다 개행을 넣어서 문장을 완성한다.
	 *     문자열의 길이가 기준치가 안된다면 그대로 출력한다.
	 * </pre>
	 * 
	 * @param msg - 입력한 문자열
	 * @return sb - 개행이 들어간 문자열
	 */
	public String convertInlineMsg(String msg) {

		int len = 50;
		int m_size = msg.length();

		int count = 0;

		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();

		if (m_size >= len) {
			for (int i = 0; i < m_size; i++) {
				// System.out.println("("+len+") (" + sb.length() + ") " + sb.toString());
				if (sb.length() >= len) {
					sb2.append(sb.toString() + "\n");
					sb.setLength(0);
					sb.append(msg.charAt(i));
				} else {
					sb.append(msg.charAt(i));
				}
			}
			if(sb2 != null) {
				sb2.append(sb.toString());
			}
		} else {
			sb2.append(msg);
		}

		return sb2.toString();
	}

}
