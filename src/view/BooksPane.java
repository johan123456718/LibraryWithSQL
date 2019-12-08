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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
    private Dialog addBookDialog;
    private ArrayList<TextField> addBookTextField;
    private ChoiceBox genreChoiceBox;
    private ChoiceBox authorChoiceBox;
    private DatePicker publishDate;    
    private DatePicker birthDate;
    private TableView<Author> authorTable;
    private ObservableList<Author> authorsInTable;
    
    private Dialog addRatingDialog;
    private ChoiceBox ratingChoiceBox;
    private TextField isbnTextField;
                

    public BooksPane(MockBooksDb booksDb) throws IOException, SQLException {
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

    private void init(Controller controller) throws IOException, SQLException {

        booksInTable = FXCollections.observableArrayList();
        authorsInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus(controller);
        addBookInit(controller);
        addRatingInit(controller);

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
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.2));
        isbnCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.2));
        authorCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.4));



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
    
    private void initAuthorTable() {
        authorTable = new TableView<>();
        authorTable.setEditable(false); // don't allow user updates (yet)

        // define columns
        TableColumn<Author, String> idCol = new TableColumn<>("Author ID");
        TableColumn<Author, String> nameCol = new TableColumn<>("Name");
        TableColumn<Author, Date> dateCol = new TableColumn<>("Date of Birth");;
        authorTable.getColumns().addAll(idCol, nameCol, dateCol);
        // give title column some extra space
        //nameCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        idCol.setCellValueFactory(new PropertyValueFactory<>(String.valueOf("authorId")));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        // associate the table view with the data
        authorTable.setItems(authorsInTable);
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
        MenuItem addRatingItem = new MenuItem("Add rating");
        MenuItem addAuthorItem = new MenuItem("Add author");
        manageMenu.getItems().addAll(addItem, removeItem, addRatingItem, addAuthorItem);

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
                    controller.addBookSelected();
                } catch (SQLException ex) {
                    Logger.getLogger(BooksPane.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
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
        
        addRatingItem.setOnAction(new EventHandler<ActionEvent> (){
            @Override
            public void handle(ActionEvent event) {
                controller.addRatingSelected();
            }
            
        });

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }
    
    
            
        private void addBookInit(Controller controller) throws IOException, SQLException{
                addBookDialog = new Dialog();
                ButtonType button = new ButtonType("Create", ButtonData.OK_DONE);

		addBookDialog.getDialogPane().getButtonTypes().add(button);
		addBookDialog.setTitle("Add book");
                GridPane addBookPane = new GridPane();
                addBookTextField = new ArrayList<>();

                genreChoiceBox = new ChoiceBox(FXCollections.observableArrayList("fantasy", "sci-fi", "crime", "drama", "romance", "science")
                );
                genreChoiceBox.setTooltip(new Tooltip("Select genre"));
                publishDate = new DatePicker();
                
                birthDate = new DatePicker();
                
                initAuthorTable();
                
                addBookTable(addBookPane);
                addBookDialog.getDialogPane().setContent(addBookPane);
        }
        
        public void showAddBookDialog(Controller controller, List<Author> allAuthors) throws IOException, SQLException{
                authorsInTable.clear();
                authorsInTable.addAll(allAuthors);
                addBookDialog.showAndWait();
		addBookDialog.setResizable(true);
                
                if(addBookDialog.getDialogPane().getButtonTypes().get(0).getButtonData()== ButtonData.OK_DONE){
                    String isbn = addBookTextField.get(0).getText();
                    String title = addBookTextField.get(1).getText();
                    String authorName = addBookTextField.get(2).getText();
                    String authorId = addBookTextField.get(3).getText();
                    String publisher = addBookTextField.get(4).getText();
                    String genre = null;
                    if(genreChoiceBox.getValue() != null){
                        genre = genreChoiceBox.getValue().toString();
                    }
                    String pDate = null;
                    if(publishDate.getValue() != null){
                        pDate = publishDate.getValue().toString();
                    }
                    String bDate = null;
                    if(birthDate.getValue() != null){
                        bDate = birthDate.getValue().toString();
                    }  
                    controller.addBook(isbn, title, genre, publisher, pDate);
                }
        }
        
        public void showAddRatingDialog(Controller controller){
            addRatingDialog.showAndWait();
                addRatingDialog.setResizable(true);
                if(addRatingDialog.getDialogPane().getButtonTypes().get(0).getButtonData()== ButtonData.OK_DONE){
                    String isbn = isbnTextField.getText();
                    
                    String rating = null;
                    if(ratingChoiceBox.getValue() != null){
                        rating = ratingChoiceBox.getValue().toString();
                    }
                    controller.updateRating(isbn, rating);
                }
        }
        
        private void addBookTable(GridPane addBookPane){
                ArrayList<Label> context = new ArrayList<>();
                context.add(new Label("ISBN"));
                context.add(new Label("Title"));
                context.add(new Label("Name of Author"));
                context.add(new Label("AuthorId (if author already exists)"));
                context.add(new Label("Publisher"));
                context.add(new Label("Genre"));
                context.add(new Label("Author Birth Date"));
                context.add(new Label("Publication Date"));
                context.add(new Label("Existing Authors"));
                             
                for(int i = 0; i < context.size(); i++){
                    addBookPane.add(context.get(i), 0, i);
                }           
                
                for(int i = 0; i < 5; i++){
                    addBookTextField.add(new TextField());
                    addBookPane.add(addBookTextField.get(i), 1, i);
                }
                
                addBookPane.add(genreChoiceBox,1,5);
                
                addBookPane.add(birthDate,1,6);
                addBookPane.add(publishDate, 1, 7);
                addBookPane.add(authorTable, 1, 8);       
        }
         private void addRatingInit(Controller controller) throws IOException, SQLException{
           addRatingDialog = new Dialog();
           ButtonType button = new ButtonType("Send", ButtonData.OK_DONE);
           addRatingDialog.getDialogPane().getButtonTypes().add(button);
           addRatingDialog.setTitle("Add Rating");
           GridPane addRatingPane = new GridPane();
           isbnTextField = new TextField();
           
           ratingChoiceBox = new ChoiceBox(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
           ratingChoiceBox.setTooltip(new Tooltip("Select ranking"));
           
           addRatingDialog.getDialogPane().setContent(addRatingPane);  
           addRatingTable(addRatingPane);
        }
        private void addRatingTable(GridPane addRatingPane){
            ArrayList<Label> context = new ArrayList<>();
            context.add(new Label("ISBN"));
            context.add(new Label("Rating"));
            for(int i = 0; i < context.size(); i++){
                addRatingPane.add(context.get(i), 0, i);
            }    
            addRatingPane.add(isbnTextField, 1, 0);
            addRatingPane.add(ratingChoiceBox,1,1);
        }
}