package it.uniroma3.giw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler {

	private String firstPageUrl;
	private DocumentSaver documentSaver;
	private final int CRAWLER_MAX_LENGTH = 10;
	private List<HtmlAnchor> pageToVisit;

	private Set<String> pageToVisitSet;
	private String pathToSkip;


	public Crawler(String firstPage, String folderName, String pathToSkip){
		this.firstPageUrl = firstPage;
		this.documentSaver = new DocumentSaver(folderName);
		this.pageToVisit = new LinkedList<HtmlAnchor>();
		this.pageToVisitSet = new HashSet<String>();
		this.pathToSkip = pathToSkip;
	}	


	public WebClient getNewWebClient(){
		WebClient webClient = new WebClient();
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setAppletEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		return webClient;
	}


	/**
	 * Inizia il crawling
	 */
	public void doCrawling() {

		WebClient webClient = this.getNewWebClient();		
		HtmlPage firstPage;

		try {
			firstPage = webClient.getPage(this.firstPageUrl);
			this.pageToVisitSet.add(firstPage.getUrl().toString());

			List<HtmlAnchor> anchors = filterAnchors(firstPage.getAnchors(), firstPage);

			this.addToPageToVisit(anchors ,firstPage );

			for(int i = 0 ; i < this.CRAWLER_MAX_LENGTH && !this.pageToVisit.isEmpty(); i++){
				HtmlPage htmlPage = getPageFromList(this.pageToVisit);
				try {
					System.out.println(htmlPage.getUrl().toString());
					
					if(!this.documentSaver.save(htmlPage)) {
						i--;
					}

					List<HtmlAnchor> newAnchors = filterAnchors(htmlPage.getAnchors(), htmlPage);
					this.addToPageToVisit(newAnchors, htmlPage);

				} catch (Exception e) {
					e.printStackTrace();
					i--;
				}	
			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		} 

		webClient.closeAllWindows();
	}
	
	private HtmlPage getPageFromList(List<HtmlAnchor> list) throws IOException{
		HtmlPage page = list.get(0).click();
		list.remove(0);
		return page;
	}


	/**
	 * Aggiunge le ancore alla lista delle pagine da visitare, aggiungendogli la parte iniziale dell'Url
	 * @param newAnchors = la lista di ancore da aggiungere
	 * @param htmlPage = la pagina iniziale
	 * @throws MalformedURLException
	 */
	private void addToPageToVisit(List<HtmlAnchor> newAnchors, HtmlPage htmlPage) throws MalformedURLException {
		for(HtmlAnchor anchor : newAnchors) {
			String target = HtmlAnchor.getTargetUrl(anchor.getHrefAttribute(), htmlPage).toString();
			if(!this.pageToVisitSet.contains(target)) {
				this.pageToVisit.add(anchor);
				this.pageToVisitSet.add(target);
			}
		}
	}


	/**
	 * Filtra le ancore per rimanere all'interno del dominio della pagina iniziale
	 * @param anchors = la lista di ancore da filtrare
	 * @param htmlPage = la pagina da cui parte
	 * @return
	 * @throws MalformedURLException
	 */
	private List<HtmlAnchor> filterAnchors(List<HtmlAnchor> anchors, HtmlPage htmlPage) throws MalformedURLException {
		List<HtmlAnchor> newAnchors = new ArrayList<HtmlAnchor>();
		for (HtmlAnchor htmlAnchor : anchors) {
			String target = HtmlAnchor.getTargetUrl(htmlAnchor.getHrefAttribute(), htmlPage).toString();
//			boolean booooo1 = target.contains(firstPageUrl);
//			if (booooo1)
//				System.out.println();
//			boolean booooo2 = !this.pageToVisitSet.contains(target);
//			boolean booooo3 = !target.matches(this.firstPageUrl + this.pathToSkip);
			if ((target.contains(firstPageUrl)&&(!this.pageToVisitSet.contains(target)) && 
					(!target.matches(this.firstPageUrl + this.pathToSkip)))) {
				newAnchors.add(htmlAnchor);
			}
		}
		return newAnchors;
	}

}