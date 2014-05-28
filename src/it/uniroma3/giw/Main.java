package it.uniroma3.giw;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis()/1000;
		
		String sportPage = "http://www.tuttomercatoweb.com/serie-a/";
		String newsPage = "http://www.repubblica.it/cronaca/";
		String moviePage = "http://www.mymovies.it/film/";
		String gamePage = "http://www.spaziogames.it/recensioni_videogiochi/";
		
		String sportPathToSkip = "\\?&start=.*";
		String newsPathToSkip = "[0-9]*/$";
		String moviePathToSkip = "([^\\d].*|[0-9]{4}/$|.*\\?.*|[0-9]{4}/.+/.+)";		
		String gamePathToSkip = "(index|articoli).aspx.*"; 
		
		Crawler crawler = new Crawler(sportPage, "sport", sportPathToSkip);
		crawler.doCrawling();
		
		crawler = new Crawler(newsPage, "news", newsPathToSkip);
		crawler.doCrawling();
		
		crawler = new Crawler(moviePage, "movie", moviePathToSkip);
		crawler.doCrawling();
		
		crawler = new Crawler(gamePage, "game", gamePathToSkip);
		crawler.doCrawling();
		
		long end = System.currentTimeMillis()/1000;		
		

		System.out.println("Tempo totale impiegato: " + (end-start));

//		System.out.println("http://www.mymovies.it/film/2014/".matches(moviePage + moviePathToSkip));//true
//		System.out.println("http://www.mymovies.it/film/drammatici/".matches(moviePage + moviePathToSkip));//true
//		System.out.println("http://www.mymovies.it/film/2014/lemeraviglie/".matches(moviePage/* + moviePathToSkip*/));//false
//		
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/index.aspx".matches(gamePage + gamePathToSkip));//true
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/index.aspx?c=multi&p=2".matches(gamePage + gamePathToSkip));//true
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/console_multi_piattaforma/16253/watch-dogs.aspx".matches(gamePage + gamePathToSkip));//false

	}

}
