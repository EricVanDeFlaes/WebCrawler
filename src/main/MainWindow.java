package main;

import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FontMetrics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import crawler.Crawler;
import crawler.ICrawlerLog;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.jsoup.nodes.Document;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JSplitPane;

public class MainWindow extends JFrame implements ICrawlerLog, MouseListener {
	private static final ResourceBundle forms = ResourceBundle.getBundle("resources.forms"); //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	private final Crawler crawler = new Crawler();
	
	private final JTextField txtProxy = new JTextField();
	private final JSpinner spinPort = new JSpinner();
	private final JTextField txtURL = new JTextField();
	private final JTextField txtRoot = new JTextField();
	private final JTree treeSite = new JTree();
	
	private final JTabbedPane tabViewMode = new JTabbedPane(JTabbedPane.TOP);
	private final PanelConsole panelConsole = new PanelConsole();
	private final PanelPreview panelPreview = new PanelPreview();
	private final PanelSource panelSource = new PanelSource();
	private final JSplitPane splitPane;
	private final JLabel lblLimite = new JLabel(forms.getString("MainWindow.lblLimite.text")); //$NON-NLS-1$
	private final JSpinner spinLimite = new JSpinner();

	// Optimisation pour auto-scroll
	private final FontMetrics fm;

	public MainWindow() {
		txtRoot.setText("");
		txtRoot.setColumns(10);
		this.setTitle(forms.getString("MainWindow.title")); //$NON-NLS-1$
		this.setSize(600, 450);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             
		
		JPanel panelSaisie = new JPanel();
		getContentPane().add(panelSaisie, BorderLayout.CENTER);
		GridBagLayout gbl_panelSaisie = new GridBagLayout();
		gbl_panelSaisie.columnWidths = new int[]{5, 40, 100, 30, 0, 0, 0};
		gbl_panelSaisie.rowHeights = new int[]{5, 0, 30, 0, 200, 0, 0};
		gbl_panelSaisie.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panelSaisie.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panelSaisie.setLayout(gbl_panelSaisie);
		
		JLabel lblProxy = new JLabel(forms.getString("MainWindow.lblProxy.text")); //$NON-NLS-1$
		GridBagConstraints gbc_lblProxy = new GridBagConstraints();
		gbc_lblProxy.anchor = GridBagConstraints.EAST;
		gbc_lblProxy.insets = new Insets(0, 0, 5, 5);
		gbc_lblProxy.gridx = 1;
		gbc_lblProxy.gridy = 1;
		panelSaisie.add(lblProxy, gbc_lblProxy);
		
		txtProxy.setText(forms.getString("MainWindow.txtProxy.text")); //$NON-NLS-1$
		GridBagConstraints gbc_txtProxy = new GridBagConstraints();
		gbc_txtProxy.anchor = GridBagConstraints.NORTH;
		gbc_txtProxy.insets = new Insets(0, 0, 5, 5);
		gbc_txtProxy.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtProxy.gridx = 2;
		gbc_txtProxy.gridy = 1;
		panelSaisie.add(txtProxy, gbc_txtProxy);
		txtProxy.setColumns(10);
		
		JLabel lblProxyPort = new JLabel(forms.getString("MainWindow.lblPort.text")); //$NON-NLS-1$
		GridBagConstraints gbc_lblProxyPort = new GridBagConstraints();
		gbc_lblProxyPort.anchor = GridBagConstraints.EAST;
		gbc_lblProxyPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblProxyPort.gridx = 3;
		gbc_lblProxyPort.gridy = 1;
		panelSaisie.add(lblProxyPort, gbc_lblProxyPort);
		
		spinPort.setModel(new SpinnerNumberModel(new Integer(80), null, null, new Integer(10)));
		GridBagConstraints gbc_spinPort = new GridBagConstraints();
		gbc_spinPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinPort.insets = new Insets(0, 0, 5, 5);
		gbc_spinPort.gridx = 4;
		gbc_spinPort.gridy = 1;
		panelSaisie.add(spinPort, gbc_spinPort);
		
		JLabel lblUrl = new JLabel(forms.getString("MainWindow.lblUrl.text")); //$NON-NLS-1$
		GridBagConstraints gbc_lblUrl = new GridBagConstraints();
		gbc_lblUrl.anchor = GridBagConstraints.EAST;
		gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblUrl.gridx = 1;
		gbc_lblUrl.gridy = 2;
		panelSaisie.add(lblUrl, gbc_lblUrl);

		txtURL.setText(forms.getString("MainWindow.txtUrl.text")); //$NON-NLS-1$
		GridBagConstraints gbc_textURL = new GridBagConstraints();
		gbc_textURL.insets = new Insets(0, 0, 5, 5);
		gbc_textURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_textURL.gridx = 2;
		gbc_textURL.gridy = 2;
		panelSaisie.add(txtURL, gbc_textURL);
		txtURL.getDocument().addDocumentListener(new DocumentListener() {
			  public void changedUpdate(DocumentEvent e) {
				    computeRoot();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  computeRoot();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  computeRoot();
				  }

				  public void computeRoot() {
					  txtRoot.setText(Crawler.getRootUrl(txtURL.getText()));
				  }
				});
		
		JButton btnLaunch = new JButton(forms.getString("MainWindow.btnLaunch.text")); //$NON-NLS-1$
		btnLaunch.addMouseListener(this);
		GridBagConstraints gbc_btnLaunch = new GridBagConstraints();
		gbc_btnLaunch.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLaunch.insets = new Insets(0, 0, 5, 5);
		gbc_btnLaunch.gridx = 4;
		gbc_btnLaunch.gridy = 2;
		panelSaisie.add(btnLaunch, gbc_btnLaunch);
		
		JLabel lblRoot = new JLabel(forms.getString("MainWindow.lblRoot.text")); //$NON-NLS-1$
		GridBagConstraints gbc_lblRoot = new GridBagConstraints();
		gbc_lblRoot.anchor = GridBagConstraints.EAST;
		gbc_lblRoot.insets = new Insets(0, 0, 5, 5);
		gbc_lblRoot.gridx = 1;
		gbc_lblRoot.gridy = 3;
		panelSaisie.add(lblRoot, gbc_lblRoot);
		
		GridBagConstraints gbc_txtRoot = new GridBagConstraints();
		gbc_txtRoot.anchor = GridBagConstraints.NORTH;
		gbc_txtRoot.insets = new Insets(0, 0, 5, 5);
		gbc_txtRoot.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtRoot.gridx = 2;
		gbc_txtRoot.gridy = 3;
		panelSaisie.add(txtRoot, gbc_txtRoot);
		
		GridBagConstraints gbc_lblLimite = new GridBagConstraints();
		gbc_lblLimite.insets = new Insets(0, 0, 5, 5);
		gbc_lblLimite.gridx = 3;
		gbc_lblLimite.gridy = 3;
		panelSaisie.add(lblLimite, gbc_lblLimite);
		
		GridBagConstraints gbc_spinLimite = new GridBagConstraints();
		gbc_spinLimite.fill = GridBagConstraints.BOTH;
		gbc_spinLimite.insets = new Insets(0, 0, 5, 5);
		gbc_spinLimite.gridx = 4;
		gbc_spinLimite.gridy = 3;
		spinLimite.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		panelSaisie.add(spinLimite, gbc_spinLimite);
		
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.gridwidth = 4;
		gbc_splitPane.insets = new Insets(0, 0, 5, 5);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 1;
		gbc_splitPane.gridy = 4;
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(treeSite),tabViewMode);
		panelSaisie.add(splitPane, gbc_splitPane);
		
		treeSite.setVisibleRowCount(8);
		treeSite.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode(new UrlNode("root", null)) {
				private static final long serialVersionUID = 1L;
			}
		));
		treeSite.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				TreePath path = event.getPath();
				UrlNode node = (UrlNode) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
				panelPreview.setDocument(node.document);
				panelSource.setDocument(node.document);
			}
		});
						
		tabViewMode.add("Console", panelConsole);
		tabViewMode.add("Preview", panelPreview);
		tabViewMode.add("Source", panelSource);
		
		// Optimisation pour autoscroll
		fm = panelConsole.txtInfo.getFontMetrics(panelConsole.txtInfo.getFont());

		this.setVisible(true);
	}
	
	public void log(String text) {
		panelConsole.txtInfo.append(text + "\n");
		// Force le scroll bottom sur la scrollbar en recalculant maximum
		int docHeight = panelConsole.txtInfo.getLineCount() * fm.getHeight();
		int visibleHeight = panelConsole.scrollView.getVerticalScrollBar().getVisibleAmount();
		panelConsole.scrollView.getVerticalScrollBar().setMaximum(docHeight);
		panelConsole.scrollView.getVerticalScrollBar().setValue(docHeight - visibleHeight);
		panelConsole.scrollView.paint(panelConsole.scrollView.getGraphics());
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		panelConsole.txtInfo.setText("");
		tabViewMode.setSelectedComponent(panelConsole);
		Crawler.setProxy(txtProxy.getText(), (int) spinPort.getValue());
		crawler.setMaxPages((int)spinLimite.getValue());
        crawler.crawl(MainWindow.this, txtURL.getText(), txtRoot.getText());
        
        // constitution de l'arborescence du site
		int delta = txtRoot.getText().length();
        treeSite.setModel(null);
        for (Entry<String, Document> entry: crawler.visited.entrySet()) {
        	if (treeSite.getModel() == null) {
        		// on crée le root élément
        		treeSite.setModel(new DefaultTreeModel(
    				new DefaultMutableTreeNode(new UrlNode(entry.getKey(), entry.getValue())) {
    					private static final long serialVersionUID = 1L;
    				}
        		));
        	} else {
        		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeSite.getModel().getRoot();
        		DefaultMutableTreeNode node;
        		String path = entry.getKey().substring(delta);
        		String tokens[] = path.split("/");
        		boolean doInsert = false;
        		for (int i=0; i < tokens.length; i++) {
        			if (!doInsert) {
        				doInsert = true;
	        			for (int n=0; n < rootNode.getChildCount(); n++) {
	        				node = (DefaultMutableTreeNode) rootNode.getChildAt(n);
	        				if (((UrlNode)node.getUserObject()).name.equals(tokens[i])) {
	        					rootNode = node;
	        					doInsert = false;
	        					break;
	        				}
	        			}
        			}
        			if (doInsert) {
        				node = new DefaultMutableTreeNode(new UrlNode(tokens[i], (i < tokens.length-1) ? null : entry.getValue())) {
        					private static final long serialVersionUID = 1L;
        				};
        				rootNode.add(node);
        				rootNode = node;
        			}
        		}
        	}
        }
       
        // On initialise les vues
        Document doc = crawler.visited.get(txtURL.getText());
        panelPreview.setDocument(doc);
        panelSource.setDocument(doc);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
