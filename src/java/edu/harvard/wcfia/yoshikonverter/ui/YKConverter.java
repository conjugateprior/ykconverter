package edu.harvard.wcfia.yoshikonverter.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import junit.awtui.ProgressBar;

import net.roydesign.ui.StandardMacAboutFrame;

import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import edu.harvard.wcfia.yoshikonverter.ApplicationProperties;
import edu.harvard.wcfia.yoshikonverter.Converter;
import edu.harvard.wcfia.yoshikonverter.Environment;
import edu.harvard.wcfia.yoshikonverter.exception.ConversionException;
import edu.harvard.wcfia.yoshikonverter.ui.action.AboutAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.DeleteAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.HelpAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.OpenAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.PreferencesAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.QuitAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.SaveAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.SaveAllAction;
import edu.harvard.wcfia.yoshikonverter.ui.action.TucAction;
import edu.harvard.wcfia.yoshikonverter.util.FileUtil;


/**
 * @author will
 */
public class YKConverter extends JFrame {

	private static Logger log = Logger.getLogger(YKConverter.class.getName());

	private StandardMacAboutFrame macAbout;
	
    TransferHandler th = new TransferHandler(){
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            for (int i = 0; i < transferFlavors.length; i++) {
                if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
                    return true;
                }
            }
            return false;
        }
        public boolean importData(JComponent comp, Transferable t) {
            if (canImport(comp, t.getTransferDataFlavors())) {
                try {
                    List fs = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                    openFile((File[])fs.toArray(new File[fs.size()]));
                    return true;
                } catch (Exception e){
                	System.err.println("Could not convert files" + e);
                }
            }
            return false;
        }  
    };
        
    private JCheckBox assertEncoding = new JCheckBox("I know the document encoding is", false);  

    private JTabbedPane tabs;
    private JComboBox encodingBox;
    
    private QuitAction quitAction = new QuitAction(this);
    private PreferencesAction preferencesAction = new PreferencesAction(this);
    private OpenAction openAction = new OpenAction(this);
    private SaveAction saveAction = new SaveAction(this);
    private SaveAllAction saveAllAction = new SaveAllAction(this);
    private HelpAction helpAction = new HelpAction(this);
    private AboutAction aboutAction = new AboutAction(this);
    private DeleteAction deleteAction = new DeleteAction(this);
    private TucAction tucAction = new TucAction(this);
    
    public YKConverter() throws HeadlessException {
        super("YK Converter");
        
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(openAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAllAction);
        fileMenu.add(deleteAction);
        if (!Environment.isMac()){
            fileMenu.add(preferencesAction);
            fileMenu.addSeparator();
            fileMenu.add(quitAction);
        }
        bar.add(fileMenu);
        
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(tucAction);
        bar.add(editMenu);
        
        if (!Environment.isMac()){
        	JMenu helpMenu = new JMenu("Help");
        	helpMenu.add(helpAction);
        	helpMenu.add(aboutAction);
        	bar.add(helpMenu);
        }
        
        // now wedge these into the Mac OS.
        if (Environment.isMac()){
        	ApplicationProperties ap = Environment.getApplicationProperties();     
            Icon icon = Environment.getIcon(ap.iconPath);
            macAbout = 	new StandardMacAboutFrame(ap.appName, ap.appVersion);
            macAbout.setApplicationIcon(icon);
            macAbout.setCopyright(ap.appCopy);
            macAbout.setApplicationVersion(ap.appVersion);
            macAbout.setBuildVersion(""+ap.buildNumber);
            
        	Application app = Application.getApplication();
        	app.setAboutHandler(new AboutHandler(){
        		@Override
        		public void handleAbout(AboutEvent arg0) {
        			macAbout.setVisible(true);
        		}
        	});
        	app.setPreferencesHandler(new PreferencesHandler(){
				@Override
				public void handlePreferences(PreferencesEvent arg0) {
					preferencesAction.showPreferencesDialog();
				}
			});
        	app.setQuitHandler(new QuitHandler() {
				@Override
				public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
					handleQuit();
				}
			});
        	
        	JMenu help = new JMenu("Help");
            JMenuItem helpItem = new JMenuItem(new HelpAction(this));
            help.add(helpItem);
            bar.add(help);
        	
        }
        setJMenuBar(bar);

        tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        //tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setTransferHandler(th);
        
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        SortedMap sm = Charset.availableCharsets();
        Charset dcs = Charset.defaultCharset();
        for (Iterator iterator = sm.values().iterator(); iterator.hasNext();) {
			Charset cs = (Charset) iterator.next();
			dcm.addElement(new CharsetWrapper(cs));
        }
        encodingBox = new JComboBox(dcm);
        encodingBox.setSelectedItem(new CharsetWrapper(dcs));
        
        JPanel panel = makePanel();
        getContentPane().add(panel);
        
        
        readAndAssertPreferences();
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                handleQuit();
            }
        });
        
        pack();
        
        setSize(500,400);       
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width/2 - 250, d.height/2 - 200);
    }
    
    private JPanel makePanel(){    
    	JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));        

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton button = new JButton(openAction);
        button.setIcon(Environment.getIcon("openicon.png"));
        button.setText(null);
        toolbar.add(button);
        button = new JButton(saveAction);
        button.setIcon(Environment.getIcon("saveicon.png"));
        button.setText(null);
        toolbar.add(button);
        button = new JButton(deleteAction);
        button.setIcon(Environment.getIcon("discardicon.png"));
        button.setText(null);
        toolbar.add(button);
        JButton tbutton = new JButton(tucAction);
        tbutton.setIcon(Environment.getIcon("tuc.png"));
        tbutton.setText(null);
        toolbar.add(tbutton);
        
        centrePanel.add(tabs, BorderLayout.CENTER);
    
        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.X_AXIS));
        assertEncoding.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        hintPanel.add(Box.createGlue());
        hintPanel.add(assertEncoding);
        hintPanel.add(encodingBox);
        hintPanel.add(Box.createGlue());
        hintPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 2));
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(toolbar, BorderLayout.NORTH);
        p.add(centrePanel, BorderLayout.CENTER);
        p.add(hintPanel, BorderLayout.SOUTH);
        return p;
    }
    
    public void handleTuc(){
    	DocumentView dv = (DocumentView)tabs.getSelectedComponent();
    	if (dv == null)
            return;
    	dv.tuc();
    }
    
    protected void readAndAssertPreferences(){
        Preferences preferences = 
            Preferences.userNodeForPackage(YKConverter.class);
        
        Charset defCS = Charset.defaultCharset();
        String encName = preferences.get("encoding", defCS.name());
        CharsetWrapper wrapper = null;
        try {
        	Charset cs = Charset.forName(encName);
        	wrapper = new CharsetWrapper(cs);
        } catch (Exception ex){
        	wrapper = new CharsetWrapper(defCS);
        }
    	encodingBox.setSelectedItem(wrapper);
        
        boolean isAsserted = preferences.getBoolean("assertEncoding", false);
        assertEncoding.setSelected(isAsserted);
    }
    
    protected void savePreferences() throws BackingStoreException {
        Preferences preferences = 
            Preferences.userNodeForPackage(YKConverter.class);
        
        preferences.put("encoding", 
        		((CharsetWrapper)encodingBox.getSelectedItem()).canonicalName);
        preferences.putBoolean("assertEncoding", assertEncoding.isSelected());

        preferences.flush();
    }
    
    public void handleQuit(){
        // ask any questions
    	try {
    		savePreferences();
    	} catch (BackingStoreException bex){
    		bex.printStackTrace();
    	}    	
        dispose();
        System.out.println("Quitting");
        System.exit(0);
    }
    
    public String openFile(File f, String encoding) throws Exception{

    	String txt = null;
    	String name = f.getName().toLowerCase();
    	if (name.endsWith("pdf")){
    		// ignore encoding
    		System.err.println("Converting PDF");
    		txt = Converter.inhalePdf(f);
    		System.err.println("done");
    	}
    	else if (name.endsWith("html") || name.endsWith("htm")){
    		System.err.println("Converting HTML");
    		txt = Converter.inhaleHtml(f, encoding);
    		System.err.println("done");
    	} else {
    		// test for microsoft-ness
    		try {
    			System.err.println("Checking if we this is an MS format POI can deal with");
    			POITextExtractor factory = ExtractorFactory.createExtractor(f);
    			System.err.println("Apparently it is. Converting...");
    			txt = factory.getText();
    			System.err.println("done");
    		} catch (Exception ex){
    			System.err.println("It isn't, so we'll treat it as if it is plain text");
    			txt = Converter.inhale(f, encoding);
    		}
    	}
    	return txt;
    }
    
    private CharsetDetector charsetDetector;
    
    protected String tryToIdentifyEncoding(File f) throws IOException {
    	if (charsetDetector == null)
    		charsetDetector = new CharsetDetector();
    	
    	charsetDetector.setText(FileUtil.getBytes(f));
    	CharsetMatch match = charsetDetector.detect();
    	String matchName = match.getName();
    	System.err.println("Identified encoding of " + f.getName() + " as " + matchName);
    	
    	charsetDetector.setText(new byte[]{});
    	
    	return matchName;
    }
        
    public void openFile(final File[] files) {

        final boolean guessEncoding = !assertEncoding.isSelected();
        final String assertedEncoding = ((CharsetWrapper)encodingBox.getSelectedItem()).getCanonicalName();
        final Charset defaultCharset = Charset.defaultCharset();
        
        SwingWorker<List<File>, Object> worker;

        //getGlassPane().setVisible(true);
        //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        worker = new SwingWorker<List<File>, Object>(){
        	
        	@Override
        	protected List<File> doInBackground() throws Exception {

        		String encoding = null;
        	
        		List<File> fails = new ArrayList<File>();
        		for (int ii = 0; ii < files.length; ii++) {
        			final File f = files[ii];
        			
        			String fname = f.getName().toLowerCase();
        			if (fname.endsWith("html") || fname.endsWith("htm") || fname.endsWith("txt")){
        				// assert, guess, or default an encoding
        				if (!guessEncoding)
        					encoding = assertedEncoding;
        				else {
        					try {
        						encoding = tryToIdentifyEncoding(f);
        					} catch (IOException ex){
        						encoding = defaultCharset.name();
        					}
        				}
        			}
        			System.err.println("Processing " + f.getName());
            		if (encoding != null)
            			System.err.println("which we decided was in " + encoding);

        			try {
        				// extract
        				final String txt = openFile(f, encoding);

        				// update the GUI on the right thread
        				SwingUtilities.invokeLater(new Runnable() {
        					@Override
        					public void run() {
        						DocumentView dv = new DocumentView(f, txt, th);
        						dv.setTransferHandler(th);
        						tabs.add(f.getName(), dv);
        						tabs.setSelectedComponent(dv);  							
        					}
        				});		
        			} catch (Exception ex){
        				fails.add(f);
        			}
        		}
        		return fails;
        	}
        	@Override
        	protected void done() {
        		try {
        			List<File> res = get();;
        			if (res.size()>0){
        				JTextArea area = new JTextArea(30,10);
        				StringBuffer sb = new StringBuffer();
        				sb.append("The following files could not be converted:\n");
        				for (int ii = 0; ii < files.length; ii++) 
        					sb.append("\t" + files[ii].getName() + "\n");
        				area.setText(sb.toString());
					
        				JOptionPane.showMessageDialog(YKConverter.this, area);
        			}
        		} catch (Exception ex){
        			ex.printStackTrace();
        		}
        	}
        };
        worker.execute();   
    }

    /**
     * Replaces the suffix on a string, if there is one, with a replacement.
     * If there is no suffix on the original, adds the replacement.
     * 
     * @param suffixedString
     * @param replacementSuffix
     * @return adjusted string with replacementSuffix
     */
    protected String replaceSuffix(String suffixedString, String replacementSuffix){
        int index = -1; // find the last period
        for (int ii = suffixedString.length()-1; ii >= 0; ii--) {
            if (suffixedString.charAt(ii) == '.'){
                index = ii;
                break;
            }
        }       
        if (index == -1)
            return suffixedString + "." + replacementSuffix;
       
        return suffixedString.substring(0,index) + 
        	"." + replacementSuffix;
    }
    
    protected void saveDocumentView(DocumentView dv) throws IOException {
        Preferences preferences = 
            Preferences.userNodeForPackage(YKConverter.class);
        boolean saveNearby = preferences.getBoolean("saveNearby", false);
        String filePath = preferences.get("saveFolder", 
                (new File(System.getProperty("user.home"))).getAbsolutePath());
            
        File saveFile = null;
        File f = dv.getFile();
        if (saveNearby)
            saveFile = new File(f.getParentFile(), replaceSuffix(f.getName(), "txt"));
        else  // save in place
            saveFile = new File(new File(filePath), replaceSuffix(f.getName(), "txt"));
        
        dv.save(saveFile);
        tabs.remove(dv);        
    }
    
    public void save(){
        DocumentView dv = (DocumentView)tabs.getSelectedComponent();
        if (dv == null)
            return;
            
        try {
            saveDocumentView(dv);
        } catch (IOException ioe) {
            String fname = dv.getFile().getName();
            JOptionPane.showMessageDialog(this, "Could not save " + fname + ": " +
                    ioe.toString(), "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void saveAll() {
        List errors = new ArrayList();
        for (int ii = 0; ii < tabs.getTabCount(); ii++) {
            DocumentView dv = (DocumentView)tabs.getComponent(ii);
            try {
                saveDocumentView(dv);
            } catch (IOException ioe) {
                errors.add(dv.getFile());
            }
        }
        if (errors.size() > 0){
            JTextArea ar = new JTextArea(errors.size() + 2, 40);
            StringBuffer sb = new StringBuffer();
            sb.append("Could not save:\n");
            for (Iterator iter = errors.iterator(); iter.hasNext();) {
                File offendingFile = (File)iter.next();
                sb.append("\t" + offendingFile.getName() + "\n");
            }
            ar.setText(sb.toString());
            ar.setBackground(this.getBackground());
            JOptionPane.showMessageDialog(this, ar, "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void deleteView(){
        tabs.remove(tabs.getSelectedComponent());
    }
    
    public static void main(String[] args) {    	
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	try {
            		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            	} catch (Exception e){ /* */ }
            	
                YKConverter c = new YKConverter();
                c.setVisible(true);
            }
        });
    	
    }
    
}
