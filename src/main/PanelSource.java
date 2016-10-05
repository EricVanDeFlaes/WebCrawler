package main;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jsoup.nodes.Document;

public class PanelSource extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JEditorPane editorSource;
	
	public PanelSource() {
		setLayout(new BorderLayout(0, 0));
		editorSource = new JEditorPane();
		editorSource.setEditable(false);
		editorSource.setContentType( "text/plain" );    
		editorSource.setText("");
		add(new JScrollPane(editorSource));
	}
	
	public void setDocument(Document document) {
		if (document == null) editorSource.setText("");
		else editorSource.setText(document.outerHtml());
		editorSource.setCaretPosition(0);
	}
}
