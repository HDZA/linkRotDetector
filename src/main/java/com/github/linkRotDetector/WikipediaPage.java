package main.java.com.github.linkRotDetector;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.lang3.RandomStringUtils;

public class WikipediaPage {
	String url = "";
	ArrayList<UrlInfo> citationLinks = new ArrayList<>();
	SourceParser pageParser;
	
	public WikipediaPage(String url){
		this.url = url;
		pageParser = new SourceParser(url);
		citationLinks = pageParser.parseLinks(url);
	}
	public ArrayList<UrlInfo> getCitationLink(){
		return citationLinks;
	}
	
	private static boolean isBadConnection(int responseCode){
		// This section is for HTTP response codes that are lesser known and not covered in the HttpUrlConnection object.
		
		int HTTP_TOO_MANY_REQUESTS = 429;
		//------------------------------------
		
		if(		   responseCode == HttpURLConnection.HTTP_BAD_REQUEST||		////Error 400: Bad request, sometimes sites treat the random url generated as a url that deserves an error 500, assume a site that does this does not return soft 404's for the momment.
				   responseCode == HttpURLConnection.HTTP_FORBIDDEN ||      //Error 403: Forbidden
    	    	   responseCode == HttpURLConnection.HTTP_NOT_FOUND ||      //Error 404: Not found
    	    	   responseCode == HttpURLConnection.HTTP_GONE || 	        //Error 410: Gone
    	    	   responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR||  //Error 500: Http Internal Error / Http Server Error
    	    	   responseCode == HttpURLConnection.HTTP_NOT_IMPLEMENTED|| //Error 501: Http Not Implemented
    	    	   responseCode == HttpURLConnection.HTTP_BAD_GATEWAY||     //Error 502: Http Bad Gateway
    	    	   responseCode == HttpURLConnection.HTTP_UNAVAILABLE ||	//Error 503: Http Unavailable
    	    	   responseCode == HttpURLConnection.HTTP_GATEWAY_TIMEOUT ||//Error 504: Http Gateway Timeout
    	    	   responseCode == HttpURLConnection.HTTP_VERSION ||        //Error 505: Http Version not supported
    	    	   responseCode == HTTP_TOO_MANY_REQUESTS){		            //Error 429: Too many requests.
    	    		return true; 
		}
		
		return false;
	}
	

    public  boolean isLinkDead(UrlInfo url) { 
    	
    	
    	int numberOfRedirects = 0;
    	final int MAXREDIRECTS = 5;//If we ever go this deep into redirects then there's something wrong with the site or it's an infinite loop. Either way return true. 
    	final int MAXRANDOMSTRINGLENGTH = 25; //Length of the random string generated that's attached to url.getParent() in the second half of this function.
    	final int MAXWAITTIME = 3; //The max time you'll allow a connection to wait before it decides that the site is down
    	
    	HttpURLConnection.setFollowRedirects(false); //We don't want to follow any auto redirects.
    	URL testSite = null;
		try {
			testSite = new URL(url.getURL());
		} catch (MalformedURLException e) {
			System.out.println("The url that was tried is malformed somehow check "+url.getURL()+" in the debug code.");
			e.printStackTrace();
		}
		
    	HttpURLConnection connection = null;
    	int responseCode = 0;
    	
    	try{
        	 connection = (HttpURLConnection) testSite.openConnection();//Open a connection with the site so we can start testing.
        	 connection.setConnectTimeout(MAXWAITTIME*1000);
        	 responseCode = connection.getResponseCode(); //Way too many things to check, just assign it to an int and check that so we don't have to slow things down with constant gets.
    	}catch(java.net.SocketTimeoutException e ){
    		System.out.println("The website: "+testSite.toString()+" is temporarily down and not returning any checkable response codes, flag this link for a later checkup.");
    		e.printStackTrace();
    		return true;
    	}catch(IOException e ){
    		System.out.println("The openConnection in the first testSite is causing an error check: " +testSite.toString() +" In the debug code");
    		e.printStackTrace();
    		return true;
    	}
    	//HttpURLConnection connection = (HttpURLConnection) testSite.openConnection();//Open a connection with the site so we can start testing.
    	
    	
    	System.out.println(testSite);
    	System.out.println(responseCode);
    	

    	
    	if(isBadConnection(responseCode)){
    		return true; 
    	}
    	
    	
    	while(responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_SEE_OTHER ){ 
    		numberOfRedirects++;
    		String Header = connection.getHeaderField("location");
    		System.out.println(Header);
    		
    		if(Header.equals("/")){
    			Header = url.getURLHost();
    		}
    		if(Header.equals("http://localhost")){
    			return true; //The site's redirecting to local host after a redirect. It's totally dead
    		}
    		if(Header.charAt(0) == '/'){ //The header does not contain the host, so just add the host to the header so we can make a proper connection.
    			Header = url.getURLHost() + Header;
    		}
    		
    		try { 
				testSite = new URL(Header); 
				connection = (HttpURLConnection) testSite.openConnection();
				responseCode = connection.getResponseCode();
			} catch (MalformedURLException e) {
				System.out.println("The url that was tried in the redirect loop is malformed somehow check " +testSite.toString() + " in the debug code");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("The openConnection in the testSite redirect loop is causing an error check " +testSite.toString() +" in the debug code");
				e.printStackTrace();
			} 
    	
    		
        	if(isBadConnection(responseCode)){
        	    return true; 
        	}
    		
    		if(numberOfRedirects >= MAXREDIRECTS){//In case of infinite redirect loops, only allow this checker to do 5 redirects at most. 
    			return true;			//That should be generous enough for most cases. If we get this deep then just return true, it's obviously a loop or an bad link.
    		}
    	}
    	
    	String finalURL = testSite.toString(); //Get the final url reached by the program. Placing it here means it will get the final url even if there is no use of the redirect loop.
    	String randomAddress = url.getURLParent()+RandomStringUtils.randomAlphabetic(MAXRANDOMSTRINGLENGTH).toLowerCase(); //Create a random address that's not likely to exist on the parent directory of your test server.
    	
    	try{
    		 testSite = new URL(randomAddress);
        	 connection = (HttpURLConnection) testSite.openConnection();//Open a connection with the site so we can start testing.
        	 connection.setConnectTimeout(MAXWAITTIME*1000);
        	 responseCode = connection.getResponseCode(); //Way too many things to check, just assign it to an int and check that so we don't have to slow things down with constant gets.
    	}catch(java.net.SocketTimeoutException e){
    		System.out.println("The website you tried to connect to is temporarily down and not returning any checkable response codes, flag" +randomAddress +" for a later checkup.");
    		return true;
    	}catch( IOException e){
    		System.out.println("The openConnection in the randomAddress testSite is causing an error check " +randomAddress +" in the debug code");
    	}
    	int rSNumberofRedirects = 0;
    	

    	
    	System.out.println(randomAddress);
    	System.out.println(responseCode);
    	
    	if(isBadConnection(responseCode)){	
    	    		return false; 
    	}
    	
    	while(responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_SEE_OTHER ){ //In this check we return true instead of false, if our randomly generated address produces a hard 404 then we can safely assume that the page does not create soft 404 pages and the original url is indeed alive.
    		rSNumberofRedirects++;
    		String Header = connection.getHeaderField("location");
    		if(Header.equals("/")){
    			Header = url.getURLHost();
    		}else if(Header.equals("http://localhost")){
    			return true; //The site's redirecting to local host after a redirect. It's totally dead
    		}else if(Header.charAt(0) == '/'){ //The header does not contain the host, so just add the host to the header so we can make a propper connection.
    			Header = url.getURLHost() + Header;
    		}
    		try {
				testSite= new URL(Header);
	    		connection = (HttpURLConnection) testSite.openConnection();
	    		responseCode = connection.getResponseCode();
			} catch (MalformedURLException e) {
				System.out.println("The random url that was tried in the redirect loop is malformed somehow check " +testSite.toString() + " in the debug code");
				e.printStackTrace();
			}catch( IOException e){
				 System.out.println("The openConnection for the randomAddress in the redirect loop is causing an error check " +testSite.toString() +" in the debug code");
			}


    		
        	if(isBadConnection(responseCode)){	
        		return false; 
        	}
    		
    		if(rSNumberofRedirects >= MAXREDIRECTS){//In case of infinite redirect loops, only allow this checker to do 20 redirects at most. 
    			return false;		
    		}
    	}
    	randomAddress = testSite.toString(); //This is the final url of the random url that the redirects reach. 

    	
    	if((randomAddress == finalURL) && (rSNumberofRedirects == numberOfRedirects)){
    		if(finalURL == url.getURLHost()){
    			return false; //We're assuming that no host site can be a dead page. It's the one real weakness of this  algorithm. 
    		}
    		return true; // the tested url is clearly a soft 404.
    	}
    	
    	if(rSNumberofRedirects != numberOfRedirects){
    		return false;
    	}
    	
    	if((randomAddress != finalURL) && (rSNumberofRedirects == numberOfRedirects) && (pageParser.computeSimilarity(randomAddress,finalURL)>.90)){
    		return true;
    	}
    	
    	return false;
    }
}


	

