package application.main;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import java.awt.BorderLayout;

public class PanelConsole extends JPanel {
	private static final long serialVersionUID = 1L;
	public final JTextArea txtInfo = new JTextArea();
	public final JScrollPane scrollView = new JScrollPane(txtInfo);
	
	public PanelConsole() {
		setLayout(new BorderLayout(0, 0));
		txtInfo.setEditable(false);
		txtInfo.setTabSize(3);
		scrollView.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollView);		
	}
}
