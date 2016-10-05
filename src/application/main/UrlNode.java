package application.main;

import org.jsoup.nodes.Document;

public class UrlNode {
	public String name;
	public Document document;
	
	public UrlNode(String name, Document document) {
		this.name = name;
		this.document = document;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
