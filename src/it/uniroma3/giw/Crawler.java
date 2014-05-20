package it.uniroma3.giw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler {
    public static void main(String[] args) {
	doCrawling();
    }

    private static void doCrawling() {
	String firstPage = "http://htmlunit.sourceforge.net/";

	// Test
	final WebClient webClient = new WebClient();
	HtmlPage page;
	// List<HtmlPage> pages = new ArrayList<HtmlPage>();
	try {
	    page = webClient.getPage(firstPage);

	    // List<DomAttr> pages = (List<DomAttr>)
	    // page.getByXPath("//a/@href");
	    System.out.println(page.getByXPath("//a[contains(@href, '.htm')]")
		    .get(0).getClass());
	    List<HtmlAnchor> anchors = (List<HtmlAnchor>) page
		    .getByXPath("//a[contains(@href, '.htm')]");
	    // List<HtmlAnchor> anchors = page.getAnchors();

	    for (HtmlAnchor anchor : anchors) {
		try {
		    HtmlPage htmlPage = webClient.getPage(anchor
			    .getHrefAttribute());
		    System.out.println(htmlPage.getTitleText());

		} catch (MalformedURLException e) {
		    HtmlPage htmlPage = webClient.getPage(firstPage
			    + anchor.getHrefAttribute());
		    System.out.println(htmlPage.getTitleText());

		}
	    }
	} catch (FailingHttpStatusCodeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (MalformedURLException e) {

	} catch (IOException e) {

	}

	webClient.closeAllWindows();
    }
}
