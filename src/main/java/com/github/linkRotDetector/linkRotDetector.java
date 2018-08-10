package main.java.com.github.linkRotDetector;

import java.io.PrintWriter;
public class linkRotDetector {
    public static void main(String[] args) throws Exception {
        PrintWriter output = new PrintWriter("Report.txt", "UTF-8");
        String url = args[0];
        WikipediaPage targetWikiPage = new WikipediaPage(url);
        int counter = 1;
        if(!targetWikiPage.getCitationLink().isEmpty()){
            output.println("Link in: " + url);
            for(UrlInfo URL:targetWikiPage.getCitationLink()){
                if(targetWikiPage.isLinkDead(URL)){
                    output.println(counter + ": " +URL.getURL() + " : Is a dead link either hard 404 or a soft 404");
                    counter++;
                }else{
                    output.println(counter + ": " + URL.getURL() + " : passes the link rot test");
                    //System.out.println(counter + ": " + URL.getURL() + " : passes the link rot test");
                    counter++;
                }
            }
            output.close();
        }
        System.out.println("The Wikipedia page analysis is complete!");

    }
}
