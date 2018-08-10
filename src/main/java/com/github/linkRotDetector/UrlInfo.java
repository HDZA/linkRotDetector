package main.java.com.github.linkRotDetector;
import java.net.URI;
import java.net.URISyntaxException;

public class UrlInfo {
	private String myURL;
	private String myURLName;
	private String myURLHost;
	private String myURLParent;
	
	UrlInfo(){
		myURL = "";
		myURLName = "";
		myURLHost = "";
		myURLParent = "";
	}
		
	public void setURL(String suppliedURL) {
    	if(suppliedURL.charAt(0) == '/'){//Sometimes urls are put in the href as //exampleSite.com instead of http://exampleSite.com which messes up the link rot checker and the initial assignment in the class, check for an '/' character at the start.
    		StringBuilder urlToBe = new StringBuilder(suppliedURL);
    		urlToBe.insert(0,"http:");
    		myURL =urlToBe.toString();
    	}else{
    		myURL = suppliedURL;
    	}
		try {
			setURLHost(suppliedURL);
			setURLParent(suppliedURL);
		} catch (Exception e) {
			System.out.println("The setup of the url has somehow failed. Check " + suppliedURL + " in the debug code.");
			e.printStackTrace();
		}

	}
	private void setURLHost(String suppliedURL){
    	URI aURI = null;
		try {
			aURI = new URI(suppliedURL);
		} catch (URISyntaxException e) {
			System.out.println("The setup of the urlHOST has somehow failed. Check " + suppliedURL + " in the debug code.");
		}
		myURLHost = "http://"+ aURI.getHost(); //Append the http part because getHost just returns the host with no protocol attached.
		
	}
	private void setURLParent(String suppliedURL){ 
		
		StringBuilder parentToBe = new StringBuilder(suppliedURL);
		int parentLength =parentToBe.length()-1;
		String parentURL = this.getURLHost() + "/";
		if(suppliedURL.equals(this.getURLHost())){
			myURLParent = parentToBe.toString() + "/";
			return;
		}
		if(parentURL.equals(parentToBe.toString())){
			myURLParent = parentToBe.toString();
			return;
		}
		if(parentURL.equals(parentToBe.toString())){
			myURLParent = parentToBe.toString();
			return;
		}

    	for(int i = parentLength; i>=0; i-- ){

    		if(i != parentLength && parentToBe.charAt(i) == '/'){
    			break;
    		}
    		parentToBe.deleteCharAt(i);
    	}
    	if(parentToBe.charAt(0) == '/'){//Sometimes urls are put in the href as //exampleSite.com instead of https://exampleSite.com which messes up the link rot checker, check for an '/' character at the start.
    		parentToBe.insert(0,"http:");
    	}
    	myURLParent = parentToBe.toString();
	}
	
	public void setURLTitle(String suppliedURLTitle){
		myURLName = suppliedURLTitle;
	}
	
	public String getURL(){
		return myURL;
	}
	
	public String getURLTitle(){
		return myURLName;    		
	}
	
	public String getURLHost(){
		return myURLHost;
	}
	
	public String getURLParent(){
		return myURLParent;
	}
}
