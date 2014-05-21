package it.uniroma3.giw;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis()/1000;
		
		Crawler crawler = new Crawler();
		crawler.doCrawling();
		long end = System.currentTimeMillis()/1000;
		
		System.out.println("Tempo impiegato: " + (end-start));
	}

}
