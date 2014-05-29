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


	/**
	 * 
	 * @param firstPage
	 * @param folderName
	 * @param pathToSkip sono le pagine da scartare completamente
	 * @param pathToAnalize sono le pagine da cui bisogna prendere le informazioni, senza salvare la pagina stessa
	 */
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
			//this.pageToVisitSet.add(htmlPage.getUrl().toString());
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
				//	else //Creato solo per la stampa
				//		System.out.println("Visitata ma non salvata: " + url);

					//this.pageCached.add(url);
					
					//System.out.println("cache: " + this.pageCached.toString());
					//System.out.println();
					
					htmlPage = getPageFromList(this.pageToVisit, htmlPage);

				} catch (IOException e){
					
				}


			} 	

		} catch (FailingHttpStatusCodeException | IOException e) {
			//e.printStackTrace();
		} 

		this.webClient.closeAllWindows();
	}


	private void extractAnchors(HtmlPage htmlPage) {
		String url;
		//List<HtmlAnchor> filteredAnchors = new LinkedList<HtmlAnchor>();
		for (HtmlAnchor anchor : htmlPage.getAnchors()){
			try{

				url = HtmlAnchor.getTargetUrl(anchor.getHrefAttribute(), htmlPage).toString();
				if (url.contains(firstPageUrl) && 					//Se appartiene al dominio
						(!this.pageCached.contains(url)) &&		//se non è stata già vista
						(!url.matches(this.firstPageUrl + this.pathToSkip)) ){ //e se non è da scartare
					//filteredAnchors.add(anchor);
					this.pageToVisit.add(anchor);
					//getire i duplicati qua dentro
				}

			} catch (Exception e){
				//e.printStackTrace();
			}
		}			
		//this.addToPageToVisit(filteredAnchors, htmlPage);
	}

	/**
	 * Aggiunge le ancore alla lista delle pagine da visitare, aggiungendogli la parte iniziale dell'Url
	 * @param newAnchors = la lista di ancore da aggiungere
	 * @param htmlPage = la pagina iniziale
	 * @throws MalformedURLException
	 */
	private void addToPageToVisit(List<HtmlAnchor> newAnchors, HtmlPage htmlPage){
		for(HtmlAnchor anchor : newAnchors) {
			try {
				String target = HtmlAnchor.getTargetUrl(anchor.getHrefAttribute(), htmlPage).toString();
				if(!this.pageCached.contains(target)) {
					this.pageToVisit.add(anchor);
					this.pageCached.add(target);
				}
			} catch (MalformedURLException e){
				//e.printStackTrace();;
			}
		}
	}

	private void save(HtmlPage page) {
		if (this.documentSaver.save(page)){
			System.out.println("salvo");
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

	//
	//
	//	/**
	//	 * Filtra le ancore per rimanere all'interno del dominio della pagina iniziale
	//	 * @param anchors = la lista di ancore da filtrare
	//	 * @param htmlPage = la pagina da cui parte
	//	 * @return
	//	 * @throws MalformedURLException
	//	 */
	//	private List<HtmlAnchor> filterAnchors(List<HtmlAnchor> anchors, HtmlPage htmlPage) throws MalformedURLException {
	//		List<HtmlAnchor> newAnchors = new ArrayList<HtmlAnchor>();
	//		for (HtmlAnchor htmlAnchor : anchors) {
	//			String target = HtmlAnchor.getTargetUrl(htmlAnchor.getHrefAttribute(), htmlPage).toString();
	//			//			boolean booooo1 = target.contains(firstPageUrl);
	//			//			if (booooo1)
	//			//				System.out.println();
	//			//			boolean booooo2 = !this.pageToVisitSet.contains(target);
	//			//			boolean booooo3 = !target.matches(this.firstPageUrl + this.pathToSkip);
	//			if ((target.contains(firstPageUrl)&&(!this.pageToVisitSet.contains(target)) && 
	//					(!target.matches(this.firstPageUrl + this.pathToSkip)))) {
	//				newAnchors.add(htmlAnchor);
	//			}
	//		}
	//		return newAnchors;
	//	}

}