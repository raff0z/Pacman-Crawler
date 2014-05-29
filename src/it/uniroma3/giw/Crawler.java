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

	private Set<String> pageCached;
	private String pathToSkip;
	private String pathToAnalize;
	private int pageSavedNumbers;
	private WebClient webClient;

	public Crawler(String firstPage, String folderName, String pathToSkip, String pathToAnalize){
		this.firstPageUrl = firstPage;
		this.documentSaver = new DocumentSaver(folderName);
		this.pageToVisit = new LinkedList<HtmlAnchor>();
		this.pageCached = new HashSet<String>();
		this.pathToSkip = pathToSkip;
		this.pathToAnalize = pathToAnalize;
		this.pageSavedNumbers = 0;
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

		this.webClient = this.getNewWebClient();		
		HtmlPage htmlPage;
		String url;
		try {

			htmlPage = this.webClient.getPage(this.firstPageUrl);
			
			//Aggiungo la pagina nella cache per analizzarla una seconda volta
			this.pageCached.add(this.firstPageUrl);
			
			//La prima pagina non va mai salvata, ma va analizzata per estrarre i link
			this.extractAnchors(htmlPage);

			htmlPage = getPageFromList(this.pageToVisit, htmlPage);

			while (htmlPage != null){
				try {
					url = htmlPage.getUrl().toString();
					
					
					this.extractAnchors(htmlPage);

					//In lista ci sono solo pagine da analizzare e da salvare, non da saltare
					if (!url.matches(this.firstPageUrl + this.pathToAnalize)){
						System.out.println(url);
						this.save(htmlPage);
					}
//					else //Creato solo per la stampa
//						System.out.println("Visitata ma non salvata: " + url);

					htmlPage = getPageFromList(this.pageToVisit, htmlPage);

				} catch (IOException e){
					//Salta semplecemente la pagina, senza fare alcunchè
				}


			} 	

		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println(e.getMessage());
		} 

		this.webClient.closeAllWindows();
	}


	private void extractAnchors(HtmlPage htmlPage) {
		String url;
		for (HtmlAnchor anchor : htmlPage.getAnchors()){
			try{

				url = HtmlAnchor.getTargetUrl(anchor.getHrefAttribute(), htmlPage).toString();
				if (url.contains(firstPageUrl) && 					//Se appartiene al dominio
						(!this.pageCached.contains(url)) &&		//se non è stata già vista
						(!url.matches(this.firstPageUrl + this.pathToSkip)) ){ //e se non è da scartare
					this.pageToVisit.add(anchor);
				}

			} catch (Exception e){
				//Salta semplecemente la pagina, senza fare alcunchè
			}
		}			
	}

	private void save(HtmlPage page) {
		if (this.documentSaver.save(page)){
			this.pageSavedNumbers++;		
		}
	}

	private HtmlPage getPageFromList(List<HtmlAnchor> list, HtmlPage htmlPage) throws IOException{
		if (this.pageSavedNumbers == this.CRAWLER_MAX_LENGTH)
			return null;
		
		HtmlAnchor anchor;
		String url;
		
		//Controllo che la pagina non sia stata già visitata
		do {
			if (list.isEmpty())
				return null;
			
			anchor = list.get(0);
			list.remove(0);
			url = HtmlAnchor.getTargetUrl(anchor.getHrefAttribute(), htmlPage).toString();				
			
			//Se c'è il # lo elimina
			if (url.charAt(url.length()-1) == '#')
				url = url.substring(0, url.length()-2);
			
			//Controlla se l'url ha lo / finale
			if (url.charAt(url.length()-1) != '/')
				url += "/";
			
			//System.out.println("Contains: " + this.pageCached.contains(url) + " " + url + " cache: " + this.pageCached);
		} while (this.pageCached.contains(url));
		
		this.pageCached.add(url);

		HtmlPage page = anchor.click();
		return page;
	}
	
}