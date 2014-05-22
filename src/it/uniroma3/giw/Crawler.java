package it.uniroma3.giw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
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
	
	private Set<String> visitedPage;
	
	public Crawler(String firstPage, String foldierName){
		this.firstPageUrl = firstPage;
		this.documentSaver = new DocumentSaver(foldierName);
		this.pageToVisit = new LinkedList<HtmlAnchor>();
		this.visitedPage = new HashSet<String>();
	}	
	
	public WebClient getNewWebClient(){
		WebClient webClient = new WebClient();
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setAppletEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		return webClient;
	}

	public void doCrawling() {
		
		WebClient webClient = this.getNewWebClient();		
		HtmlPage firstPage;

		try {
			firstPage = webClient.getPage(this.firstPageUrl);
			this.save(firstPage);
			this.visitedPage.add(firstPage.getUrl().toString());
			String s = firstPage.getUrl().toString();
			
			this.pageToVisit.addAll(firstPage.getAnchors());
			HtmlPage htmlPage = this.getPageFromList(this.pageToVisit, 0);
			
			while (htmlPage != null){
				
				try {
//					System.out.println(htmlPage.getTitleText());
					System.out.println(htmlPage.getUrl().toString());
					this.save(htmlPage);
						
					do {
						htmlPage = this.getNextPage(htmlPage);
					} while (htmlPage!=null && this.visitedPage.contains(htmlPage.getUrl().toString()));
					
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
	

	private HtmlPage getNextPage(HtmlPage htmlPage) throws IOException{
		
		//Controllo se ho raggiunto il limite di pagine salvate
		if (this.pageSavedNumbers == this.CRAWLER_MAX_LENGTH)
			return null;
		
		List<HtmlAnchor> newAnchors = htmlPage.getAnchors();
		
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
		
		this.pageToVisit.addAll(newAnchors);
		return nextPage;		
	}

	private void save(HtmlPage page) {
		if (this.documentSaver.save(page)){
			this.pageSavedNumbers++;		
			this.visitedPage.add(page.getUrl().toString());
		}
	}
	
}