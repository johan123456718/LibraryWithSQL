package view;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import model.SearchMode;
import model.Book;
import model.BooksDbInterface;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.control.Alert.AlertType.*;
import model.Genre;
import model.Rating;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 1) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        result = booksDb.searchBooksByIsbn(searchFor);
                        break;
      
                    case Author:
                        result = booksDb.searchBooksByAuthor(searchFor);
                        break;
                    case Genre:
                        result = booksDb.searchBooksByGenre(searchFor);
                        break;
                    default:
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (Exception e) {
            booksView.showAlertAndWait("Database error.", ERROR);
        }
    }

    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).
    protected void connect(){    
        try{
            booksDb.connect();
            booksView.displayBooks(booksDb.getAllBooks());
        }catch (IOException | SQLException | ClassNotFoundException e) {
            booksView.showAlertAndWait("Could not connect to database.", ERROR);
        } 
    }

    protected void disconnect(){
        try{
            booksDb.disconnect();
        }catch (IOException | SQLException e) {
                    e.printStackTrace();
        } 
    }
    
    protected void getAllBooks(){
        try {
            booksView.displayBooks(booksDb.getAllBooks());
        }
        catch (IOException | SQLException e) {
                e.printStackTrace();
        } 
    }
    
    protected void addBookSelected() throws SQLException, IOException{
        try {
            booksView.showAddBookDialog(this, booksDb.getAllAuthors());
        }
        catch (Exception e){
            booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
        }
    }
    
        
    protected void addRatingSelected(){
        try {
            booksView.displayBooks(booksDb.getAllBooks());
            booksView.showAddRatingDialog(this);
        }
        catch (Exception e){
            booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
        }
    }
    
    protected void updateRating(String isbn, String rating){
        try{
            booksView.displayBooks(booksDb.getAllBooks());
            booksDb.updateRating(isbn, rating);
            booksView.displayBooks(booksDb.getAllBooks());
        }catch (IOException | SQLException e) {
                e.printStackTrace();
        } 
    }
    
    protected void addBook(String isbn, String title, String genre, String publisher, String pDate) throws IOException, SQLException{
        try{
            System.out.println(booksDb.getAllAuthors());
            booksDb.addBook(isbn, title, genre, publisher, pDate);
            System.out.println(Date.valueOf("2007-11-22"));
        }
        catch (IllegalArgumentException e){
            booksView.showAlertAndWait("Author has been removed or updated. Please try again.", ERROR);
        }
        catch (IOException | SQLException e){
            booksView.showAlertAndWait("Invalid data entered. Please try again.", ERROR);
        }
        catch(NullPointerException e){
            booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
        }
    }  
}