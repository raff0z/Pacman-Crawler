package it.uniroma3.giw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Properties;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DocumentSaver {

	private int counter;
	private String crawlerPath;
	private String id2UrlPath;
	private static final int STRING_LENGTH = 5;
	private File id2Url;

	public DocumentSaver(String foldierName) {
		this.counter = 0;

		Properties conf = new Properties();
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/pacman_configuration.properties");
			conf.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}		

		this.crawlerPath = conf.getProperty("crawler-path");
		this.id2UrlPath = conf.getProperty("id2url-path");

		this.crawlerPath += foldierName + File.separator + "pages" + File.separator;
		this.id2UrlPath += foldierName + File.separator;

		this.id2Url = new File(this.id2UrlPath + File.separator + "id2url.txt");
		this.cleanPath();
		try {
			new File(this.id2UrlPath).mkdir();
			this.id2Url.createNewFile();
			new File(this.crawlerPath).mkdir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getCounter() {
		return this.counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public boolean save(HtmlPage page) {
		String nameFile = this.crawlerPath + getStringFromCounter(this.counter) +".html";
		try {			
			page.save(new File(nameFile));			
			this.updateID2URL(nameFile, page.getUrl().toString());
			return true;
		} catch (MalformedURLException e1){
			//Se non riesce, provo a salvare a mano
			this.manuallySave(page, nameFile);
			this.updateID2URL(nameFile, page.getUrl().toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void updateID2URL(String nameFile, String url){
		this.append(this.id2UrlPath + "id2url.txt", nameFile + " -> " + url);			
		this.counter++;
	}

	private void manuallySave(HtmlPage page, String nameFile) {
		String htmlCode = page.asXml();
		try {
			FileOutputStream file = new FileOutputStream(nameFile);
			PrintStream stream = new PrintStream(file);
			stream.print(htmlCode);
			stream.close();
		} catch (FileNotFoundException e) {
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
		File dir = new File(this.crawlerPath);
		if (dir.exists())
			deleteFolder(dir);

		if(this.id2Url.exists()){
			this.id2Url.delete();
			new File(this.id2UrlPath).delete();
		}
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
