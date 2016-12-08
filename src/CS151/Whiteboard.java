package CS151;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Point2D;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;

public class Whiteboard extends Application {

    private WhiteboardPresenter presenter;
    private Canvas canvas;
    private Button rect;
    private Button oval;
    private Button line;
    private Button text;
    private Button colorPicker;
    private Button toFront;
    private Button toBack;
    private Button remove;
    private TextField textInput;
    private Button fontButton;
    private ComboBox<String> fonts;
    private TableView<DShapeModel> tv;
    private MenuItem save;
    private MenuItem open;
    private MenuItem close;
    private Color color;
    
    @Override
    public void start(Stage stage) throws Exception {
	presenter = new WhiteboardPresenter();
	presenter.attachView(this);
	
	
	VBox main = new VBox();
	main.setPrefSize(950, 400);
	VBox menu = getMenu();
	GridPane gp = new GridPane();
	canvas = new Canvas(main);
	VBox leftColumn = getLeftColumn(main);
	setFontBox();
	// gather the location in the canvas where a user clicked
	// use this information to select the correct shape in the
	// view 
	canvas.setOnMouseClicked(e -> {
		Point2D location = new Point2D(e.getX(), e.getY());
		// output for testing
		System.out.print("x click: " + location.getX());
		System.out.print(" y click : " + location.getY() + '\n');
		canvas.makeSelection(location);
		if(canvas.getSelected() instanceof DText) {
		    disableTextControls(false);
		    DShape text = canvas.getSelected();
		    setTextInput( ((DText) text).getText());
		    setFontText( ((DText) text).getFont());
		} else {
		    disableTextControls(true);
		    setFontText("Font Type");
		}
	    });
	
	rect.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
		    presenter.addDShape(new DRectModel());
		    disableTextControls(true);
		}
	    });

	oval.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
		    presenter.addDShape(new DOvalModel());
		    disableTextControls(true);
		}
	    });

	line.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
		    presenter.addDShape(new DLineModel());
		    disableTextControls(true);		    
		}
	    });

	text.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
		    presenter.addDShape(new DTextModel());
		    disableTextControls(false);
		    DShape text = canvas.getSelected();
		    setTextInput( ((DText) text).getText());
		    setFontText( ((DText) text).getFont());
		}
	    });

	colorPicker.setOnAction(new EventHandler() {
		ColorPickerWindow colorPick;
		DShape selected;
		public void handle(Event t) {
			selected = canvas.getSelected();
			if (selected != null)
				colorPick = new ColorPickerWindow(selected.getModel().getColor());
			else
				colorPick = new ColorPickerWindow(Color.GRAY);
		    color = colorPick.display();
		    canvas.updateColor(color);
		    canvas.paintComponent();
		}
	    });

       
	fontButton.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {		    
		    displayFonts(stage);		    
		}
	    });

	toFront.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {

		}
	    });

	toBack.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {

		}
	    });

	remove.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {
		    DShape deleted = canvas.deleteSelected();
		    if(deleted != null) {
			tv.setItems(canvas.getShapeModels());
                        canvas.deleteSelected();
		    }
		}
	    });

	save.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {

		}
	    });

	open.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {

		}
	    });

	close.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent event) {

		}
	    });

	textInput.setOnKeyReleased(new EventHandler<KeyEvent>() {
		public void handle(KeyEvent event) {
		    DShape selected = canvas.getSelected();
		    if(selected != null) {
			if(selected instanceof DText) {
			    ((DText)selected).setText(textInput.getText());
			    canvas.paintComponent();
			}
		    }
		}
	    });
	
	gp.addColumn(0, leftColumn);
	gp.addColumn(1, canvas);
	
	
	main.getChildren().addAll(menu, gp);
		
	stage.setOnCloseRequest(e -> {
		e.consume();
		if (ConfirmBox.display("ConfirmBox", "Are you sure that you want to close this window?")) 
		    stage.close();
		else	
		    return;
	    });
	
	stage.setScene(new Scene(main, 900, 450));
	stage.setTitle("Whiteboard - Collaborative");
	stage.show();
	
    }


    /**
     * Add a new DShape to the canvas and update the GUI.
     * Consider changing the function call to setItems
     * because this will update the entire table when 
     * we just need to update one row
     * @param DShape shape
     */
    public void updateView(DShapeModel shape) {
	canvas.addShape(shape);
	tv.setItems(canvas.getShapeModels());
    }

    
    /**
     * Return the current color set in the GUI
     * @return Color
     */
    public Color getColor() {
	return color;
    }

    
    public VBox getTopLeft() {
	final int BOX_SIZE = 10;
	
	VBox topLeft = new VBox();
	topLeft.setPrefSize(500, 200);
	topLeft.setStyle("-fx-background-color: #bbfff8;");

	Label add = new Label("Add:");

	rect = new Button("Rect");
	oval = new Button("Oval");
	line = new Button("Line");
	text = new Button("Text");

	HBox line1 = new HBox(BOX_SIZE);
	line1.getChildren().addAll(add, rect, oval, line, text);

	colorPicker = new Button("Select Color");

	HBox line2 = new HBox(BOX_SIZE);
	line2.getChildren().add(colorPicker);

	textInput = new TextField("");
	fontButton = new Button("Font Type");
	fontButton.setMinWidth(100);
	fontButton.setMaxWidth(100);	
	disableTextControls(true);

	HBox line3 = new HBox(BOX_SIZE);
	line3.getChildren().addAll(textInput, fontButton);

	toFront = new Button("Move to Front");
	toBack = new Button("Move to Back");
	remove = new Button("Remove Shape");

	HBox line4 = new HBox(BOX_SIZE);
	line4.getChildren().addAll(toFront, toBack, remove);

	topLeft.setAlignment(Pos.TOP_CENTER);
	topLeft.setSpacing(15);
	topLeft.setPadding(new Insets(20, 10, 10, 20));

	topLeft.getChildren().addAll(line1, line2, line3, line4);
	return topLeft;	
    }

    public VBox getBottomLeft(VBox main) {
	tv = new TableView();
	

	TableColumn<DShapeModel, Integer> xColumn = new TableColumn("x");
	xColumn.setMinWidth(100);
	xColumn.setCellValueFactory(
		        new PropertyValueFactory<DShapeModel, Integer>("x"));

	TableColumn<DShapeModel, Integer> yColumn = new TableColumn("y");
	yColumn.setMinWidth(100);
	yColumn.setCellValueFactory(
			     new PropertyValueFactory<DShapeModel, Integer>("y"));

	TableColumn<DShapeModel, Integer> widthColumn = new TableColumn("width");
	widthColumn.setMinWidth(100);
	widthColumn.setCellValueFactory(
			    new PropertyValueFactory<DShapeModel, Integer>("width"));

	TableColumn<DShapeModel, Integer> heightColumn = new TableColumn("height");
	heightColumn.setMinWidth(100);
	heightColumn.setCellValueFactory(
			     new PropertyValueFactory<DShapeModel, Integer>("height"));

	tv.getColumns().addAll(xColumn, yColumn, widthColumn, heightColumn);

	tv.setItems(canvas.getShapeModels());
	tv.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
	tv.prefHeightProperty().bind(main.heightProperty());
	VBox table = new VBox();
	table.setMinWidth(400);
	//table.prefHeightProperty().bind(main.heightProperty());
	table.getChildren().add(tv);
	return table;	
    }

    public VBox getLeftColumn(VBox main) {
	VBox left = new VBox();
	left.getChildren().addAll(getTopLeft(), getBottomLeft(main));
	return left;
    }

    public VBox getMenu() {
	VBox menu = new VBox();
	MenuBar menuBar = new MenuBar();
	Menu menuFile = new Menu("File");

	save = new MenuItem("Save");
	open = new MenuItem("Open");
	close = new MenuItem("Close");

	menuFile.getItems().addAll(save, open, close);
	menuBar.getMenus().add(menuFile);
	menu.getChildren().add(menuBar);
	return menu;
    }

    
    /**
     * Enable or disable the text controls
     */
    public void disableTextControls(boolean set) {
	fontButton.setDisable(set);
	textInput.setDisable(set);
	setTextInput("");
    }


    /**
     * Set the text of the textfield
     * @param String text
     */
    public void setTextInput(String text) {
	textInput.setText(text);
    }


    /**
     * Set the text of the font button
     * @param String font
     */
    public void setFontText(String font) {
	fontButton.setText(font);
    }


    /**
     * Initialize the combo box used to display fonts
     */
    public void setFontBox() {
	fonts = new ComboBox<String>();
	List<String> systemFonts = Font.getFamilies();
	ObservableList<String> fontModel = FXCollections.observableArrayList();
	for(int i = 0; i < systemFonts.size(); i++) {
	    fontModel.add(systemFonts.get(i));
	}
	fonts.setItems(fontModel);
	fonts.setPrefHeight(20);
	fonts.setPrefWidth(200);
	fonts.setMaxWidth(Control.USE_PREF_SIZE);	
    }


    /**
     * Set the font of the selected shape if it's
     * a DText shape. 
     * @param String font
     */
    public void setFont(String font) {
	DShape selected = canvas.getSelected();
	if(selected != null) {
	    if(selected instanceof DText) {
		( (DText) selected).setFont(font);
		canvas.paintComponent();
	    }
	    
	}
    }
    
    public TableView<DShapeModel> getTv() {
    	return tv;
    }


    /**
     * Create a popup window that displays the available
     * font types to the user
     * @param Stage primaryStage
     */
    public void displayFonts(Stage primaryStage) {
	DShape selected = canvas.getSelected();	
	fonts.setValue( ((DText)selected).getFont());
	Stage fontSelection = new Stage();
	fontSelection.initModality(Modality.APPLICATION_MODAL);
	fontSelection.initOwner(primaryStage);
	VBox box = new VBox();
	box.setAlignment(Pos.CENTER);
	box.setSpacing(10);
	Button confirm = new Button("Set Font");
	confirm.setOnMouseClicked(e -> {
		String selectedFont = fonts.getValue();
		if(selectedFont != null) {
		    setFontText(selectedFont);
		    setFont(selectedFont);
		    fontSelection.close();
		}
	    });
	box.getChildren().addAll(fonts, confirm);
	Scene selection = new Scene(box, 300, 100);
	fontSelection.setScene(selection);
	fontSelection.setTitle("Fonts");
	fontSelection.show();
    }

    
    public static void main(String[] args) {
	launch(args);
    }

}
