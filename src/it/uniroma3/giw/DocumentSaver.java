package it.uniroma3.giw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DocumentSaver {
	
	private int counter;
	private String crawlerPath;
	private String id2UrlPath;
	private static final int STRING_LENGTH = 5;
	private File id2Url;
	
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
		this.id2UrlPath = conf.getProperty("id2url-path");
		
		this.id2Url = new File(id2UrlPath + "id2url.txt");
		cleanPath();
		try {
			new File(this.id2UrlPath).mkdir();
			this.id2Url.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public int getCounter() {
		return counter;
	}


	public void setCounter(int counter) {
		this.counter = counter;
	}


	public void save(HtmlPage page, String url) {
		
		try {
			String nameFile = this.crawlerPath + getStringFromCounter(counter) +".html";
			
			
			page.save(new File(nameFile));
			
			this.append(id2UrlPath + "id2url.txt", nameFile + " -> " + url);
			
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
		
		if(this.id2Url.exists()){
			this.id2Url.delete();
			new File(this.id2UrlPath).delete();
		}
		
		System.out.println("Fatto");
	}


	private void deleteFolder(File d) {
		for(File f : d.listFiles()) {
			if(!f.isFile())
				deleteFolder(f);
			f.delete();		
		}
	}
	
	
	private void append(String fileName, String toAppend) {
		try {			
			FileWriter fw = new FileWriter(fileName,true); 
			fw.write(toAppend+ "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
