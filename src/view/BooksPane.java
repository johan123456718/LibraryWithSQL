package view;

import java.io.IOException;
import model.Author;
import model.SearchMode;
import model.Book;
import model.Genre;
import model.MockBooksDb;
import model.Rating;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private MenuBar menuBar;

    public BooksPane(MockBooksDb booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus(controller);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)

        // define columns
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        TableColumn<Book, Genre> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Rating> ratingCol = new TableColumn<>("Rating");
        TableColumn<Book, List<Author>> authorCol = new TableColumn<>("Authors");
        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol, genreCol, ratingCol, authorCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        genreCol.setCellValueFactory(new PropertyValueFactory("genre"));
        ratingCol.setCellValueFactory(new PropertyValueFactory("rating"));
        authorCol.setCellValueFactory(new PropertyValueFactory("authors"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void initMenus(Controller controller) {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        MenuItem genreItem = new MenuItem("Genre");
        MenuItem allItems = new MenuItem("Get all books");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem, genreItem, allItems);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");

        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        connectItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.connect();
            }
        });

        exitItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close application?");
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                    controller.disconnect();
                    Platform.exit();
                }
            }
        });

        disconnectItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.disconnect();
            }
        });

        addItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    addBookInit(controller);
                } catch (IOException ex) {
                    Logger.getLogger(BooksPane.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(BooksPane.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        allItems.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.getAllBooks();
            }

        });

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }
    
            
        private void addBookInit(Controller controller) throws IOException, SQLException{
                Dialog dialog = new Dialog();
                ButtonType button = new ButtonType("Create", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(button);
		dialog.setTitle("Add book");
                GridPane addBookPane = new GridPane();
                ArrayList<TextField> contextTextField = new ArrayList<>();
                addBookTable(dialog, addBookPane, contextTextField);
                dialog.getDialogPane().setContent(addBookPane);  
                dialog.showAndWait();
		dialog.setResizable(true);
                if(button.getButtonData()== ButtonData.OK_DONE){
                    String isbn = contextTextField.get(0).getText();
                    String title = contextTextField.get(1).getText();
                    String genre = contextTextField.get(2).getText();
                    String date = contextTextField.get(3).getText();
                    controller.addBook(isbn, title, genre, date);
                }
        }
        
        private void addBookTable(Dialog dialog, GridPane addBookPane, ArrayList<TextField> contextTextField){
                ArrayList<Label> context = new ArrayList<>();
                context.add(new Label("ISBN"));
                context.add(new Label("Title"));
                //context.add(new Label("Author"));
                context.add(new Label("Genre"));
                context.add(new Label("Date"));
                             
                for(int i = 0; i < context.size(); i++){
                    contextTextField.add(new TextField());
                    addBookPane.add(context.get(i), 0, i);
                    addBookPane.add(contextTextField.get(i), 1, i);
                }           
        }
}
