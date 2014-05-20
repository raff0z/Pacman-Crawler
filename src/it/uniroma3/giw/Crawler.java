package it.uniroma3.giw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler {
	public static void main(String[] args) {
		long start = System.currentTimeMillis()/1000;

		doCrawling();

		long end = System.currentTimeMillis()/1000;
		System.out.println("Tempo impiegato: " + (end-start));
	}

	private static void doCrawling() {
		String firstPageUrl = "http://htmlunit.sourceforge.net/";

		DocumentSaver ds = new DocumentSaver();
		// Test
		final WebClient webClient = new WebClient();
		HtmlPage firstPage;
		// List<HtmlPage> pages = new ArrayList<HtmlPage>();

		try {
			firstPage = webClient.getPage(firstPageUrl);

			// List<DomAttr> pages = (List<DomAttr>)
			// page.getByXPath("//a/@href");
			//System.out.println(page.getByXPath("//a[contains(@href, '.htm')]").get(0).getClass());

			List<HtmlAnchor> anchors = (List<HtmlAnchor>) firstPage
					.getByXPath("//a[contains(@href, '.htm')]");
			// List<HtmlAnchor> anchors = page.getAnchors();

			HtmlPage htmlPage;

			for (HtmlAnchor anchor : anchors) {

				try {

					htmlPage = anchor.click();

					System.out.println(htmlPage.getTitleText());

					ds.save(htmlPage);
				} catch (Exception e) {
					System.out.println("ERRORE: "+anchor.getHrefAttribute());
				}

			}



		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		webClient.closeAllWindows();
	}
}