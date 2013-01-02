package edu.harvard.wcfia.yoshikonverter.ui.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.roydesign.ui.FolderDialog;
import edu.harvard.wcfia.yoshikonverter.Environment;
import edu.harvard.wcfia.yoshikonverter.ui.YKConverter;

public class PreferencesAction extends AbstractYKAction {

    ActionListener listener = new ActionListener(){
        public void actionPerformed(ActionEvent e) {
            try {
                setPreferences();
            } catch (BackingStoreException bse){
                bse.printStackTrace();
            }
        }
    };
    
    private String tooltip = "Set application preferences";
    
    private JDialog dialog;
    private JRadioButton saveNearby;
    private JRadioButton folder;
    private JTextField saveFolder;
    private JButton findFile;
    private JFileChooser chooser;
    private FolderDialog osxChooser;
    
    public PreferencesAction(YKConverter conv) {
        super(conv, "Preferences");
        if (!Environment.isMac())
            putValue(Action.SHORT_DESCRIPTION, tooltip);
    }

    protected void getPreferences(){
        Preferences preferences = 
            Preferences.userNodeForPackage(YKConverter.class);
        
        boolean sn = preferences.getBoolean("saveNearby", false);
        saveNearby.setSelected(sn);
        folder.setSelected(!sn);
        
        String filePath = preferences.get("saveFolder", 
                (new File(System.getProperty("user.home"))).getAbsolutePath());
        saveFolder.setText(filePath);        
    }
    
    protected void setPreferences() throws BackingStoreException {
        System.err.println("Setting preferences:");
        System.err.println("saveNearby set to " + saveNearby.isSelected());
        System.err.println("saveFolder set to " + saveFolder.getText());
        
        Preferences preferences = 
            Preferences.userNodeForPackage(YKConverter.class);
        
        preferences.putBoolean("saveNearby", saveNearby.isSelected());
        preferences.put("saveFolder", saveFolder.getText());
        preferences.flush();
    }
    
    public void showPreferencesDialog(){
    	if (dialog == null){
    		// construct a filechooser
    		if (Environment.isMac())
    			osxChooser = new FolderDialog(converter);
    		else {
    			chooser = new JFileChooser();
    			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    		}
    			
    		saveNearby = new JRadioButton();
    		saveNearby.addActionListener(listener);
    		folder = new JRadioButton();
    		folder.addActionListener(listener);

    		ButtonGroup group = new ButtonGroup();
    		group.add(saveNearby);
    		group.add(folder);

    		saveFolder = new JTextField(20);
    		saveFolder.setEditable(false);

    		getPreferences(); // fill this lot in appropriately

    		findFile = new JButton("Select");
    		findFile.addActionListener(new ActionListener(){
    			public void actionPerformed(ActionEvent e) {
    				folder.setSelected(true);
    				if (Environment.isMac()){
    					osxChooser.setDirectory(saveFolder.getText());
    					osxChooser.show();
    					String fle = osxChooser.getFile();
    					if (fle == null) // they cancelled
    						return;
    					saveFolder.setText(osxChooser.getDirectory());
    				} else {
    					chooser.setSelectedFile(new File(saveFolder.getText()));
    					int resp = chooser.showSaveDialog(converter);
    					if (resp != JFileChooser.APPROVE_OPTION)
    						return;
    					File f = chooser.getSelectedFile();
    					saveFolder.setText(f.getAbsolutePath());
    				}
    				
    				try {
    					setPreferences();
    				} catch (BackingStoreException bse){
    					bse.printStackTrace();
    				}
    			}
    		});

    		
    		JPanel folderChooser = new JPanel();

    		folderChooser.setLayout(new BoxLayout(folderChooser, BoxLayout.X_AXIS));
    		folderChooser.add(folder);
    		folderChooser.add(Box.createRigidArea(new Dimension(10,0)));
    		folderChooser.add(new JLabel("Save in:"));
    		folderChooser.add(Box.createRigidArea(new Dimension(1,0)));    		
    		folderChooser.add(saveFolder);
    		//folderChooser.add(Box.createRigidArea(new Dimension(2,0)));
    		if (Environment.isMac())
    			findFile.putClientProperty("Quaqua.Button.style", "square");
    		folderChooser.add(findFile);
    		folderChooser.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    		
    		JPanel nextChooser = new JPanel();
    		nextChooser.setLayout(new BoxLayout(nextChooser, BoxLayout.X_AXIS));    		
    		nextChooser.add(saveNearby);
    		nextChooser.add(Box.createRigidArea(new Dimension(10,0)));
    		nextChooser.add(new JLabel("Save next to the originals"));
    		nextChooser.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
    		
    		
    		JPanel midPanel = new JPanel(new GridLayout(2,1));
    		//midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
    		midPanel.add(folderChooser);
    		//folderChooser.add(Box.createRigidArea(new Dimension(5,0)));
    		midPanel.add(nextChooser);
    		
    		midPanel.setBorder(new TitledBorder("Extracted Text"));
    		
    		dialog = new JDialog(converter, "Preferences", false);
    		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    		JPanel p = new JPanel(new BorderLayout());
    		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    		p.add(midPanel, BorderLayout.CENTER);
            dialog.getContentPane().add(p);
            dialog.pack();
            dialog.setLocationRelativeTo(converter);
        }
        
        dialog.setVisible(true);
    }
   
    public void actionPerformed(ActionEvent e) {
    	showPreferencesDialog();
    }

}
