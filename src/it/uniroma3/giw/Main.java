package it.uniroma3.giw;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis()/1000;
		
		String sportPage = "http://www.tuttomercatoweb.com/serie-a/";
		String newsPage = "http://www.repubblica.it/cronaca/";
		String moviePage = "http://www.mymovies.it/film/";
		String gamePage = "http://www.spaziogames.it/recensioni_videogiochi/";
		
		String sportPathToSkip = "\\?&start=.*";
		String newsPathToSkip = ".*/[^news]/.*";
		String moviePathToSkip = "([^\\d].*|[0-9]{4}/$|.*\\?.*|[0-9]{4}/.+/.+)";		
		String gamePathToSkip = "(index|articoli).aspx.*"; 
		
		String sportPathToAnalize = "";
		String newsPathToAnalize = "([0-9]*/|.*#/?)$";  
		String moviePathToAnalize = "";		
		String gamePathToAnalize = ""; 
		
		Crawler crawler = new Crawler(sportPage, "sport", sportPathToSkip, sportPathToAnalize);
		crawler.doCrawling();
		
		crawler = new Crawler(newsPage, "news", newsPathToSkip, newsPathToAnalize);
		crawler.doCrawling();
//		
//		crawler = new Crawler(moviePage, "movie", moviePathToSkip, moviePathToAnalize);
//		crawler.doCrawling();
//		
//		crawler = new Crawler(gamePage, "game", gamePathToSkip, gamePathToAnalize);
//		crawler.doCrawling();
		
		long end = System.currentTimeMillis()/1000;		
		

		System.out.println("Tempo totale impiegato: " + (end-start));
		
		System.out.println("http://www.repubblica.it/cronaca/2014/05/28/foto/l_arrivo_dei_bambini_dal_congo_gli_abbracci_con_i_familiari-87463951/1/".matches(newsPage + newsPathToSkip));
		System.out.println("http://www.repubblica.it/cronaca/2014/05/22/news/affari_e_politica_i_segreti_di_o_ninno_che_fanno_tremare_l_impero_dei_clan-86814890/".matches(newsPage + newsPathToSkip));
		
//		System.out.println("http://www.mymovies.it/film/2014/".matches(moviePage + moviePathToSkip));//true
//		System.out.println("http://www.mymovies.it/film/drammatici/".matches(moviePage + moviePathToSkip));//true
//		System.out.println("http://www.mymovies.it/film/2014/lemeraviglie/".matches(moviePage/* + moviePathToSkip*/));//false
//		
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/index.aspx".matches(gamePage + gamePathToSkip));//true
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/index.aspx?c=multi&p=2".matches(gamePage + gamePathToSkip));//true
//		System.out.println("http://www.spaziogames.it/recensioni_videogiochi/console_multi_piattaforma/16253/watch-dogs.aspx".matches(gamePage + gamePathToSkip));//false

	}

}
