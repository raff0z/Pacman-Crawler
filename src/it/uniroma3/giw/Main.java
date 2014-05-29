package it.uniroma3.giw;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis()/1000;
		
		String sportPage = "http://www.tuttomercatoweb.com/serie-a/";
		String newsPage = "http://www.repubblica.it/cronaca/";
		String moviePage = "http://www.mymovies.it/film/";
		String gamePage = "http://www.spaziogames.it/recensioni_videogiochi/";
		
		String sportPathToSkip = "\\?&start=.*";
		String newsPathToSkip = ".*/[^news]/.*";
		String moviePathToSkip = "([^\\d].*|.*\\?.*|[0-9]{4}/.+/.+)";		
		String gamePathToSkip = ".*#.*"; 
		
		String sportPathToAnalize = "";
		String newsPathToAnalize = "([0-9]*/|.*#/?)$";  
		String moviePathToAnalize = "[0-9]{4}/$";		
		String gamePathToAnalize = "(index|articoli).aspx.*|"; 
		
		Crawler crawler = new Crawler(sportPage, "sport", sportPathToSkip, sportPathToAnalize);
		crawler.doCrawling();
		
		crawler = new Crawler(newsPage, "news", newsPathToSkip, newsPathToAnalize);
		crawler.doCrawling();
		
		crawler = new Crawler(moviePage, "movie", moviePathToSkip, moviePathToAnalize);
		crawler.doCrawling();
		
		crawler = new Crawler(gamePage, "game", gamePathToSkip, gamePathToAnalize);
		crawler.doCrawling();
		
		long end = System.currentTimeMillis()/1000;		
		System.out.println("Tempo totale impiegato: " + (end-start));
		

		
		
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/console_multi_piattaforma/16253/watch-dogs.aspx".matches(gamePage + gamePathToAnalize));
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/console_multi_piattaforma/15570/broken-sword-the-serpent-s-curse-pt-i.aspx#lst".matches(gamePage + gamePathToAnalize));
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/console_multi_piattaforma/16253/watch-dogs.aspx".matches(gamePage + gamePathToSkip));
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/console_multi_piattaforma/15570/broken-sword-the-serpent-s-curse-pt-i.aspx#lst".matches(gamePage + gamePathToSkip));
	}	
	
}
