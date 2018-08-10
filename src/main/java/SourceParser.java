package main.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class SourceParser {
	String url = "";
	public SourceParser(String url){
		this.url = url;
	}
	
    public  ArrayList<UrlInfo> parseLinks(String url ){

		ArrayList<UrlInfo> pInfo = new ArrayList<UrlInfo>();
		
		System.out.println("Fetching "+ url +" ...");
        Document doc = null;
		try {
			doc = (Document) Jsoup.connect(url).get();
		} catch (IOException e) {
			System.out.println("Something has gone wrong and the program was unable to fetch any data. Check "+url+" in the debug code");
			e.printStackTrace();
		
		}
        Elements links = doc.select("[rel=nofollow]");//Grab all the external links references.

        for(Element link : links){ //Throw all the links and titles of the links into an object so I can process them one at a time later. 
        	UrlInfo tempInfo = new UrlInfo();
        	try {
				tempInfo.setURL(link.attr("href"));
			} catch (Exception e) {
				e.printStackTrace();
			}
        	tempInfo.setURLTitle(link.text());
        	pInfo.add(tempInfo);
        	
        }
        
		return pInfo;
    }
    
    private static HashSet<String> shingleWebsite(String websiteText, int shingleSize){
		HashSet<String> shingledSet = new HashSet<String>();
		int numShingles = (websiteText.length()-shingleSize); //The actual formula is length-shingleSize-1 but the <= makes it so we don't need that -1.
		
		for(int i = 0; i<=numShingles; i++){
			shingledSet.add(websiteText.substring(i, i+shingleSize));
		}

		return shingledSet;
    	
    }
    
    public  float computeSimilarity(String URL1, String URL2){
    	float SIMILARITY = 0;
    	final int SHINGLESIZE = 9; //Quick and easy to spot so I can tweak the shingle sizes later.
    	Document doc = null;
    	
    	

    	try {
    		doc = Jsoup.connect(URL1).ignoreContentType(true).get(); //Normally the ignoreContentType is set to false but sometimes links lead directly to pdfs or some otherwise unsupported text. Force jsoup to pull something from the website.
    	} catch (IOException e) {
    		System.out.println("Something went wrong with the first url connection in the computeSimilarity function. Check " +URL1 +" in the debug code");
    		e.printStackTrace();
    	}//First URL
    	Document myDoc = Jsoup.parse(doc.toString());
    	String firstSiteText = myDoc.text();
    	firstSiteText = firstSiteText.toLowerCase().replaceAll("\\s+", " ").replaceAll("\\p{P}", ""); //Convert to lower case then replace all the multiple spaces with one space then strip punctuation.

    	

    	try {
    		doc = Jsoup.connect(URL2).ignoreContentType(true).get();
    	} catch (IOException e) {
    		System.out.println("Something went wrong with the second url connection in the computeSimilarity function. Check " +URL2 +" in the debug code.");
    		e.printStackTrace();
    	}//Second URL. May as well recycle the same variables 

    	myDoc = Jsoup.parse(doc.toString());
    	String secondSiteText = myDoc.text();
    	secondSiteText = secondSiteText.toLowerCase().replaceAll("\\s+", " ").replaceAll("\\p{P}", ""); //Convert to lower case then replace all the multiple spaces with one space then strip punctuation.
    	
    	
    	
    	HashSet<String> firstSite = shingleWebsite( firstSiteText,  SHINGLESIZE);
    	HashSet<String> secondSite = shingleWebsite( secondSiteText,  SHINGLESIZE);
    	
    	HashSet<String> tempSet = new HashSet<String>(firstSite); //Have to preserve the sets until we calculate the union.
    	tempSet.retainAll(secondSite);//Convert the temp set into the union of firstSite and secondSite;

    	
    	int x = tempSet.size(); //Get the size of the intersection of firstSite and secondSite.
    	//System.out.println(x);
    	firstSite.addAll(secondSite);//Convert firstSite into the union of firstSite and SecondSite.
    	int y = firstSite.size();
    	//System.out.println(y);
    	
    	SIMILARITY = (float)x/y; //Since x and y are both int we need to convert them to float to get the proper answer. Otherwise it's always either 0 or 1. 
    	return SIMILARITY;
    }
    
}
