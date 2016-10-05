package crawler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
    private static Proxy proxy = null;
    private int maxPages = 0;
    public SortedMap<String, Document> visited = new TreeMap<String, Document>();
    public Queue<String> links;
    
    public static class Proxy {
    	protected String host;
    	protected int port;
    	
    	public Proxy(String host, int port) {
    		this.host = host;
    		this.port = port;
    	}
    }
    
    public static void setProxy (String host, int port) {
    	proxy = (host.isEmpty())? null : new Proxy(host, port);
    }

    /**
     * Renvoie l'url de base d'un lien (la partie précédent # et ?)
     * @param url l'url dont on extrait l'url de base
     * @return l'url de base extraite
     */
    public static String getBaseUrl(String url) {
    	String tokens[] = url.split("[#?]");
    	return tokens[0];
    	
    }
    
    /**
     * Renvoie l'url racine d'un lien (la partie précédent # et ? avec un / final)
     * @param url l'url dont on extrait la racine
     * @return la racine extraite
     */
    public static String getRootUrl(String url) {
    	String baseUrl = getBaseUrl(url);
    	return baseUrl + ((baseUrl.endsWith("/")?"":"/"));
    }
    
    public void crawl(ICrawlerLog log, String startUrl, String rootUrl) {
    	visited.clear();
     	links = new LinkedList<String>();
     	startUrl = getBaseUrl(startUrl);
    	links.add(startUrl);
    	if (!rootUrl.endsWith("/")) rootUrl += "/";
    	while (links.size() > 0 && (maxPages==0 || visited.size() < maxPages)) {
    		String url = links.remove();
    		crawlPage(log, rootUrl, url);
    	}
    }
    
    private void crawlPage(ICrawlerLog log, String rootUrl, String url) {
    	try {
            log.log("*** Visiting page at " + url);
	        visited.put(url, null);
	        int linksCount = links.size();
	        Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
	        connection.followRedirects(false);
	        if (proxy != null) connection.proxy(proxy.host, proxy.port);
	        Document htmlDocument = connection.get();
	        if(connection.response().statusCode() != 200) { 
	        	// 200 is the HTTP OK status code indicating that everything is great.
	        	log.log("--- Connection failure");
	            return;
	        }
	        if(!connection.response().contentType().contains("text/html")) {
	        	log.log("--- Content type failure : " + connection.response().contentType());
	            return;
	        }
	        visited.put(url,  htmlDocument);
	        Elements linksOnPage = htmlDocument.select("a[href]");
	        log.log("\tFound (" + linksOnPage.size() + ") links");
	        String linkUrl;
	        for(Element link : linksOnPage) {
	        	linkUrl = getBaseUrl(link.absUrl("href"));
	        	if (!linkUrl.startsWith(rootUrl)) continue; // On ne sort pas du root
	        	// On ne revisite pas les liens déja visités
	        	if (links.contains(linkUrl)) continue;
	        	if (visited.containsKey(linkUrl)) continue; 
	        	if (linkUrl.endsWith("/")) {
		        	if (links.contains(linkUrl.substring(0, linkUrl.length()-1))) continue;
		        	if (visited.containsKey(linkUrl.substring(0, linkUrl.length()-1))) continue;
	        	} else {
		        	if (links.contains(linkUrl+"/")) continue;
		        	if (visited.containsKey(linkUrl+"/")) continue;
	        	}
	        	links.add(linkUrl);
	        }
	        log.log("\tAdded (" + (links.size() - linksCount) + ") links");
    	} catch (Exception e) {
    		log.log("--- Failure reading the URL");
    	}
    }

    public boolean searchForWord(Document htmlDocument, String searchWord) {
        if (htmlDocument == null) return false;
        return htmlDocument.body().text().toLowerCase().contains(searchWord.toLowerCase());
    }
    
    public int getMaxPages() {
    	return maxPages;
    }
    
    public void setMaxPages (int maxPages) {
    	this.maxPages = maxPages;
    }
}