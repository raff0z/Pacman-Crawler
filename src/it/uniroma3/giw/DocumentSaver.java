package it.uniroma3.giw;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DocumentSaver {
	
	private int counter;
	private String crawlerPath;
	private static final int STRING_LENGTH = 5;
	
	public DocumentSaver() {
		this.counter = 0;
		
		Properties conf = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/pacman_configuration.properties");
			if(inputStream == null)
				System.out.println("qua");
			
			conf.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}		

		this.crawlerPath = conf.getProperty("crawler-path");
		
		cleanPath();
	}

	
	public int getCounter() {
		return counter;
	}


	public void setCounter(int counter) {
		this.counter = counter;
	}


	public void save(HtmlPage page) {
		
		try {
			String s = this.crawlerPath + getStringFromCounter(counter) +".html";
			
			
			page.save(new File(s));
			counter++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private String getStringFromCounter(int counter) {
		int zeroToAdd = 4;
		
		if(counter > 0) 
			zeroToAdd = STRING_LENGTH - (int) Math.log10(counter) - 1;
		
		
		String output = "";
		
		while(zeroToAdd > 0) {
			output+="0";
			zeroToAdd--;
		}
		
		return output+""+counter;
		
	}
	
	private void cleanPath() {
		File dir = new File(crawlerPath);
		
		deleteFolder(dir);
		System.out.println("Fatto");
	}


	private void deleteFolder(File d) {
		// TODO Auto-generated method stub
		for(File f : d.listFiles()) {
			if(!f.isFile())
				deleteFolder(f);
			f.delete();		
		}
	}
}
