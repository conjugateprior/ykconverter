package edu.harvard.wcfia.yoshikonverter.ui;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import com.sun.javafx.PlatformUtil;

public class TikaConverter extends Application {

	protected File docDirectory;
	
	protected FileChooser fileChooser = new FileChooser(); // what
	protected ListView<File> list = new ListView<File>();
	protected FileChooser dirChooser = new FileChooser(); // where to
	
	protected TextArea area = new TextArea(); // keep them informed
	protected Button bConvert = new Button("CONVERT");
	
    private Parser parser = new AutoDetectParser();
	
    public void processFile(File file) {
    	ContentHandler contenthandler = new BodyContentHandler();
    	Metadata metadata = new Metadata();
    	try (FileInputStream is = new FileInputStream(file)){
    		parser.parse(is, contenthandler, metadata, new ParseContext());
    		area.appendText("Converted " + file.getName());
    	} catch (Exception e) {
    		area.appendText(e.getMessage().toString());
    		e.printStackTrace();
    	}
        System.out.println(contenthandler.toString());
	}
		
	@Override
	public void init() throws Exception {
		// get from application properties
		if (docDirectory == null){
			try {
				String fold = System.getProperty("user.home");
				docDirectory = new File(fold, "document_conversions");
			} catch (Exception e){
				// leave it null after having done our best
			}
		}
	}
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("YKConverter");
		
		area.setEditable(false);
		area.setPromptText("Conversion progress");
		
		bConvert.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				for (File f : list.getItems()) {
					processFile(f);
				}
			}
		});
		
		list.setOnDragOver(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles())
                	event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });
		list.setOnDragDropped(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    for (File file : db.getFiles()) {
						if (file.isDirectory()){
							for (File subfile : file.listFiles()) {
								if (!subfile.isDirectory()){
									if (!list.getItems().contains(subfile))
										list.getItems().add(subfile);
								}
							}
						} else {
							if (!list.getItems().contains(file))
								list.getItems().add(file);
						}
                    }
                    success = true;
                }
                event.setDropCompleted(success);       
                event.consume();
            }
        });
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		list.setOnKeyPressed(new EventHandler <KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.BACK_SPACE) ||
					event.getCode().equals((KeyCode.DELETE))){
					// workaround for bug: http://javafx-jira.kenai.com/browse/RT-24367
					ObservableList<File> sels = 
			            FXCollections.observableArrayList( //copy
			            		list.getSelectionModel().getSelectedItems());
					if (sels != null) {
						//heroes.addAll(sels);
						list.getItems().removeAll(sels);
						list.getSelectionModel().clearSelection();
					}
				}
			}
		});
		
		Button dictBtn = new Button("Choose");
		dictBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				File dd = dirChooser.showSaveDialog(primaryStage);
				if (dd != null){
					docDirectory = dd;
				}
			}
		});
	
		BorderPane listpane = new BorderPane();
        listpane.setCenter(list);
        listpane.setLeft(area);
		listpane.setBottom(bConvert);
        
		MenuBar mbar = new MenuBar();
		final Menu menu1 = new Menu("File");
		MenuItem menuAddFiles = new MenuItem("Add Files...");
		menu1.getItems().add(menuAddFiles);
		
		menuAddFiles.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				List<File> lst = fileChooser.showOpenMultipleDialog(primaryStage);
				if (lst != null) {
					for (File file : lst) {
						if (file.isDirectory()){
							for (File subfile : file.listFiles()) {
								if (!subfile.isDirectory()){
									if (!list.getItems().contains(subfile))
										list.getItems().add(subfile);
								}
							}
						} else {
							if (!list.getItems().contains(file))
								list.getItems().add(file);
						}
                    }
					list.requestFocus();
                }
			}
		});
		
		MenuItem menuExit = new MenuItem("Exit");
		menuExit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Platform.exit();
			}
		});
		if (!PlatformUtil.isMac())
			menu1.getItems().add(menuExit);
		
		final Menu menu3 = new Menu("Help");
		mbar.getMenus().addAll(menu1, menu3);
		
		if (PlatformUtil.isMac()){
			mbar.setUseSystemMenuBar(true);
			//AquaFx.style();
		} 
	
		Scene scene = new Scene(listpane, 600, 500);
		primaryStage.setScene(scene);	
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);	
	}
	
}
