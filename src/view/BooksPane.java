package view;

import model.Author;
import model.SearchMode;
import model.Book;
import model.Genre;
import model.BookMySQLDb;
import model.Rating;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
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
    private ChoiceBox addBookGenreChoiceBox;
    private DatePicker publishDate;    
    private DatePicker addBookBirthDate;
    private TableView<Author> addBookAuthorTable;
    private ObservableList<Author> addBookAuthorsInTable;
    
    private Dialog addRatingDialog;
    
    private TextField isbnTextField;
    private ChoiceBox addRatingChoiceBox;
    private Dialog addAuthorDialog;
    private ArrayList<TextField> addAuthorTextField;
    private DatePicker addAuthorBirthDate;
    private TableView<Author> addAuthorAuthorTable;
    private ObservableList<Author> addAuthorAuthorsInTable;
    
    private Dialog searchRatingDialog, searchGenreDialog;
    private ChoiceBox searchRatingChoiceBox;
    private ChoiceBox genreChoiceBox;
                

    public BooksPane(BookMySQLDb booksDb){
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

    private void init(Controller controller){

        booksInTable = FXCollections.observableArrayList();
        addBookAuthorsInTable = FXCollections.observableArrayList();
        addAuthorAuthorsInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus(controller);
        addBookInit();
        addRatingInit();
        addAuthorInit();
        searchRatingInit();
        searchGenreInit();

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
        addAuthorAuthorTable = new TableView<>();
        addAuthorAuthorTable.setEditable(false); // don't allow user updates (yet)
        // define columns
        TableColumn<Author, String> idCol = new TableColumn<>("Author ID");
        TableColumn<Author, String> nameCol = new TableColumn<>("Name");
        TableColumn<Author, Date> dateCol = new TableColumn<>("Date of Birth");;
        addAuthorAuthorTable.getColumns().addAll(idCol, nameCol, dateCol);
        idCol.setCellValueFactory(new PropertyValueFactory<>(String.valueOf("authorId")));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        // associate the table view with the data
        addAuthorAuthorTable.setItems(addAuthorAuthorsInTable);
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
        MenuItem searchGenreItem = new MenuItem("Search Genre");
        MenuItem searchRatingItem = new MenuItem("Search Rating");
        MenuItem allItems = new MenuItem("Get all books");
        searchMenu.getItems().addAll(searchGenreItem, searchRatingItem, allItems);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        
        MenuItem addRatingItem = new MenuItem("Add rating");
        MenuItem addAuthorItem = new MenuItem("Add author");
        manageMenu.getItems().addAll(addItem, addRatingItem, addAuthorItem);

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
                    controller.addBookSelected();
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
        searchRatingItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                controller.searchRatingSelected();
            }
            
        });
        addAuthorItem.setOnAction(new EventHandler<ActionEvent> (){
            @Override
            public void handle(ActionEvent event) {
                controller.addAuthorSelected();
            }
        });
        searchGenreItem.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                controller.searchGenreSelected();
            } 
        });
        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }
    
    
            
        private void addBookInit(){
                addBookDialog = new Dialog();
                ButtonType button = new ButtonType("Create", ButtonData.OK_DONE);

		addBookDialog.getDialogPane().getButtonTypes().add(button);
		addBookDialog.setTitle("Add book");
                GridPane addBookPane = new GridPane();
                addBookTextField = new ArrayList<>();

                addBookGenreChoiceBox = new ChoiceBox(FXCollections.observableArrayList("fantasy", "sci-fi", "crime", "drama", "romance", "science"));
                addBookGenreChoiceBox.setTooltip(new Tooltip("Select genre"));
                publishDate = new DatePicker();
                
                addBookBirthDate = new DatePicker();
                addBookAuthorTable = new TableView<>();
        addBookAuthorTable.setEditable(false); // don't allow user updates (yet)
        // define columns
        TableColumn<Author, String> idCol = new TableColumn<>("Author ID");
        TableColumn<Author, String> nameCol = new TableColumn<>("Name");
        TableColumn<Author, Date> dateCol = new TableColumn<>("Date of Birth");;
        addBookAuthorTable.getColumns().addAll(idCol, nameCol, dateCol);
        idCol.setCellValueFactory(new PropertyValueFactory<>(String.valueOf("authorId")));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        // associate the table view with the data
        addBookAuthorTable.setItems(addBookAuthorsInTable);
                
                
                
                addBookTable(addBookPane);
                addBookDialog.getDialogPane().setContent(addBookPane);
        }
        
        private void addAuthorInit(){
            addAuthorDialog = new Dialog();
            ButtonType button = new ButtonType("Add", ButtonData.YES);
            addAuthorDialog.getDialogPane().getButtonTypes().add(button);
            addAuthorDialog.setTitle("Add Author");
            GridPane addAuthorPane = new GridPane();
            addAuthorTextField = new ArrayList();
            addAuthorBirthDate = new DatePicker();
            initAuthorTable();
            addAuthorTable(addAuthorPane);
            addAuthorDialog.getDialogPane().setContent(addAuthorPane);
        }
        
        public ArrayList<String> showAddBookDialog(List<Author> allAuthors){
                addBookAuthorsInTable.clear();
                addBookAuthorsInTable.addAll(allAuthors);
                addBookDialog.showAndWait();
		addBookDialog.setResizable(true);
                    String isbn = null;
                    String title = null;
                    String authorName = null;
                    String authorId = null;
                    String publisher = null;
                    String genre = null;
                    String pDate = null;
                    String bDate = null;
                
                if(addBookDialog.getDialogPane().getButtonTypes().get(0).getButtonData() == ButtonData.OK_DONE){

                    if (!addBookTextField.get(0).getText().equals("")){
                        isbn = addBookTextField.get(0).getText();
                    }
                    if (!addBookTextField.get(1).getText().equals("")){
                        title = addBookTextField.get(1).getText();
                    }
                    if (!addBookTextField.get(2).getText().equals("")){
                        authorName = addBookTextField.get(2).getText();
                    }
                    if (!addBookTextField.get(3).getText().equals("")){
                        authorId = addBookTextField.get(3).getText();
                    }
                    if (!addBookTextField.get(4).getText().equals("")){
                        publisher = addBookTextField.get(4).getText();
                    }
                    if(addBookGenreChoiceBox.getValue() != null){
                        genre = addBookGenreChoiceBox.getValue().toString();
                    }
                    if(publishDate.getValue() != null){
                        pDate = publishDate.getValue().toString();
                    }                    
                    if(addBookBirthDate.getValue() != null){
                        bDate = addBookBirthDate.getValue().toString();
                    }
                }
                ArrayList<String> result = new ArrayList();
                result.add(isbn);
                result.add(title);
                result.add(genre);
                result.add(publisher);
                result.add(pDate);
                result.add(authorName);
                result.add(authorId);
                result.add(bDate);
                return result;     
        }
        
        public ArrayList<String> showAddRatingDialog(){
            addRatingDialog.showAndWait();
            addRatingDialog.setResizable(true);
            String isbn = null;
            String rating = null;
            if(addRatingDialog.getDialogPane().getButtonTypes().get(0).getButtonData()== ButtonData.OK_DONE){
                if(!isbnTextField.getText().equals("")){
                    isbn = isbnTextField.getText();
                }
                if(addRatingChoiceBox.getValue() != null){
                    rating = addRatingChoiceBox.getValue().toString();
                }
            }
            ArrayList<String> result = new ArrayList();
            result.add(isbn);
            result.add(rating);
            return result;
        }
        
        protected void searchRatingInit() {
        
        searchRatingDialog = new Dialog();
        ButtonType button = new ButtonType("Send", ButtonData.OK_DONE);
        searchRatingDialog.getDialogPane().getButtonTypes().add(button);
        searchRatingDialog.setTitle("Search Rating");
        GridPane searchRatingPane = new GridPane();

        searchRatingChoiceBox = new ChoiceBox(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
        searchRatingChoiceBox.setTooltip(new Tooltip("Select ranking"));

        searchRatingDialog.getDialogPane().setContent(searchRatingPane);
        searchRatingTable(searchRatingPane);
    }

    private void searchRatingTable(GridPane searchRatingPane) {
        ArrayList<Label> context = new ArrayList<>();
        context.add(new Label("Rating"));
        searchRatingPane.add(context.get(0), 0, 0);   
        searchRatingPane.add(searchRatingChoiceBox, 2, 0);
    }
    
    protected void searchGenreInit() {
        
        searchGenreDialog = new Dialog();
        ButtonType button = new ButtonType("Send", ButtonData.OK_DONE);
        searchGenreDialog.getDialogPane().getButtonTypes().add(button);
        searchGenreDialog.setTitle("Search Genre");
        GridPane searchGenrePane = new GridPane();

        genreChoiceBox = new ChoiceBox(FXCollections.observableArrayList("fantasy", "sci-fi", "crime", "drama", "romance", "science")
        );
        genreChoiceBox.setTooltip(new Tooltip("Select genre"));

        searchGenreDialog.getDialogPane().setContent(searchGenrePane);
        searchGenreTable(searchGenrePane);
        
    }

    private void searchGenreTable(GridPane searchRatingPane) {
        ArrayList<Label> context = new ArrayList<>();
        context.add(new Label("Genre"));
        searchRatingPane.add(context.get(0), 0, 0);   
        searchRatingPane.add(genreChoiceBox, 2, 0);
    }

        
    public ArrayList<String> showAddAuthorDialog(List<Author> allAuthors){
            addAuthorAuthorsInTable.clear();
            addAuthorAuthorsInTable.addAll(allAuthors);
            addAuthorDialog.showAndWait();
            addAuthorDialog.setResizable(true);
            String isbn = null;
            String authorName = null;
            String authorId = null;
            String bDate = null;
            if(addAuthorDialog.getDialogPane().getButtonTypes().get(0).getButtonData()== ButtonData.YES){
                if(!addAuthorTextField.get(0).getText().equals("")){
                    isbn = addAuthorTextField.get(0).getText();
                }
                if(!addAuthorTextField.get(1).getText().equals("")){
                    authorName = addAuthorTextField.get(1).getText();
                }                               
                if(!addAuthorTextField.get(2).getText().equals("")){
                    authorId = addAuthorTextField.get(2).getText();
                }
                if(addAuthorBirthDate.getValue() != null){
                    bDate = addAuthorBirthDate.getValue().toString();
                }
            }
            ArrayList<String> result = new ArrayList();
            result.add(isbn);
            result.add(authorName);
            result.add(authorId);
            result.add(bDate);
            return result;
        }
    
    public String showSearchGenreDialog(){
        searchGenreDialog.showAndWait();
        searchGenreDialog.setResizable(true);  
        String genre = null;
        if (addBookDialog.getDialogPane().getButtonTypes().get(0).getButtonData() == ButtonData.OK_DONE) {
            if(genreChoiceBox.getValue() != null){
                genre = genreChoiceBox.getValue().toString();
            }
        }
        return genre;
    }
    
    public String showSearchRatingDialog(){
            searchRatingDialog.showAndWait();
            searchRatingDialog.setResizable(true);
            String rating = null;
            if (searchRatingDialog.getDialogPane().getButtonTypes().get(0).getButtonData() == ButtonData.OK_DONE) {
                if(searchRatingChoiceBox.getValue() != null){
                    rating = searchRatingChoiceBox.getValue().toString();
                }
            }
            return rating;
        }
        
    private void addAuthorTable(GridPane addAuthorPane){
            ArrayList<Label> context = new ArrayList<>();

            context.add(new Label("ISBN"));
            context.add(new Label("Name of Author"));
            context.add(new Label("AuthorId (if author already exists)"));
            context.add(new Label("Author Birth Date"));
            context.add(new Label("Existing Authors"));
            
            for(int i = 0; i < context.size(); i++){
                    addAuthorPane.add(context.get(i), 0, i);
            }
            for(int i = 0; i < 3; i++){
                addAuthorTextField.add(new TextField());
                addAuthorPane.add(addAuthorTextField.get(i), 1, i);
            }
                addAuthorPane.add(addAuthorBirthDate,1,3);
                addAuthorPane.add(addAuthorAuthorTable, 1, 4);
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
                
                addBookPane.add(addBookGenreChoiceBox,1,5);
                
                addBookPane.add(addBookBirthDate,1,6);
                addBookPane.add(publishDate, 1, 7);
                addBookPane.add(addBookAuthorTable, 1, 8);       
        }
    private void addRatingInit(){
        addRatingDialog = new Dialog();
        ButtonType button = new ButtonType("Send", ButtonData.OK_DONE);
        addRatingDialog.getDialogPane().getButtonTypes().add(button);
        addRatingDialog.setTitle("Add Rating");
        GridPane addRatingPane = new GridPane();
        isbnTextField = new TextField();
           
        addRatingChoiceBox = new ChoiceBox(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
        addRatingChoiceBox.setTooltip(new Tooltip("Select ranking"));
           
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
            addRatingPane.add(addRatingChoiceBox,1,1);
        }
}