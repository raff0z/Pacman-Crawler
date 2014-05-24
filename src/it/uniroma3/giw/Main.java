package it.uniroma3.giw;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis()/1000;
		
		String sportPage = "http://www.tuttomercatoweb.com/serie-a/";
//		String gamePage = "http://www.spaziogames.it/";
		
		Crawler crawler = new Crawler(sportPage, "sport");
		crawler.doCrawling();
		long endSport = System.currentTimeMillis()/1000;		
		System.out.println("Tempo impiegato per Sport: " + (endSport-start));
		
//		crawler = new Crawler(gamePage, "game");
//		crawler.doCrawling();
		long end = System.currentTimeMillis()/1000;		
//		System.out.println("Tempo impiegato per Games: " + (end-endSport));
		

		System.out.println("Tempo totale impiegato: " + (end-start));
	}

}
