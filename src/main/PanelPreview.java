package main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jsoup.nodes.Document;

import javax.swing.JEditorPane;
import java.awt.BorderLayout;

public class PanelPreview extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JEditorPane editorPreview;
	
	public PanelPreview() {
		setLayout(new BorderLayout(0, 0));
		editorPreview = new JEditorPane();
		editorPreview.setEditable(false);
		editorPreview.setContentType( "text/html" );    
		editorPreview.setText( "<html><body>Preview page</body></html>" );
		add(new JScrollPane(editorPreview));
	}
	
	public void setDocument(Document document) {
		if (document == null) editorPreview.setText("");
		else editorPreview.setText(document.outerHtml());
		editorPreview.setCaretPosition(0);
	}
}
