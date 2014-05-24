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
	private int pageSavedNumbers = 0;
	private List<HtmlAnchor> pageToVisit;
	private final double TRESHOLD = 0.2; 

	private Set<String> pageToVisitSet;

	public Crawler(String firstPage, String folderName){
		this.firstPageUrl = firstPage;
		this.documentSaver = new DocumentSaver(folderName);
		this.pageToVisit = new LinkedList<HtmlAnchor>();
		this.pageToVisitSet = new HashSet<String>();
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
			this.save(firstPage);
			System.out.println(firstPage.getUrl().toString());
			this.pageToVisitSet.add(firstPage.getUrl().toString());
			
			List<HtmlAnchor> anchors = filterAnchors(firstPage.getAnchors(), firstPage);
			
			this.addToPageToVisit(anchors ,firstPage );
			
			HtmlPage htmlPage = this.getPageFromList(this.pageToVisit, 0);

			while (htmlPage != null){

				try {
					System.out.println(htmlPage.getUrl().toString());
					this.save(htmlPage);


					htmlPage = this.getNextPage(htmlPage);


				} catch (Exception e) {
					e.printStackTrace();
				}				
			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		} 

		webClient.closeAllWindows();
	}

	/**
	 * Preleva ed elimina l'oggetto dalla lista
	 * @param list 
	 * @return
	 * @throws IOException 
	 */
	private HtmlPage getPageFromList(List<HtmlAnchor> list, int index) throws IOException{
		HtmlPage page = list.get(index).click();
		list.remove(index);
		return page;
	}

	/**
	 * Prende la prossima pagina da visitare
	 * @param htmlPage = la pagina da cui partire
	 * @return
	 * @throws IOException
	 */
	private HtmlPage getNextPage(HtmlPage htmlPage) throws IOException{

		//Controllo se ho raggiunto il limite di pagine salvate
		if (this.pageSavedNumbers == this.CRAWLER_MAX_LENGTH)
			return null;

		List<HtmlAnchor> newAnchors = filterAnchors(htmlPage.getAnchors(), htmlPage);
		this.addToPageToVisit(newAnchors, htmlPage);

		HtmlPage nextPage;
		int randomIndex;
		double randomSurfer = Math.random();
		if (randomSurfer<TRESHOLD){
			randomIndex = (int) (Math.random() * this.pageToVisit.size());
			nextPage = this.getPageFromList(this.pageToVisit, randomIndex);	
		} else {
			randomIndex = (int) (Math.random() * newAnchors.size());
			
			nextPage = this.getPageFromList(newAnchors, randomIndex);
		}

		return nextPage;		
	}

	private void save(HtmlPage page) {
		if (this.documentSaver.save(page)){
			this.pageSavedNumbers++;		
		}
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
    private List<HtmlAnchor> filterAnchors(List<HtmlAnchor> anchors,
	    HtmlPage htmlPage) throws MalformedURLException {
	List<HtmlAnchor> newAnchors = new ArrayList<HtmlAnchor>();
	for (HtmlAnchor htmlAnchor : anchors) {
	    String target = HtmlAnchor.getTargetUrl(
		    htmlAnchor.getHrefAttribute(), htmlPage).toString();
	    if (target.contains(firstPageUrl)) {
		newAnchors.add(htmlAnchor);
	    }
	}
	return newAnchors;
    }

}