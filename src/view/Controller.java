package view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import model.SearchMode;
import model.Book;
import model.BooksDbInterface;
import java.util.List;
import static javafx.scene.control.Alert.AlertType.*;
import model.Author;


/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    /**
     *
     * @param booksDb
     * @param booksView
     */
    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    /**
     *
     * @param searchFor
     * @param mode
     */
    protected void onSearchSelected(String searchFor, SearchMode mode) {
        new Thread(){
            @Override
            public void run(){
                try {
                    if (searchFor != null && searchFor.length() > 1) {
                        final List<Book> result;
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
                            default:
                                result = null;
                        }
                        if (result == null || result.isEmpty()) {
                            javafx.application.Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run(){
                                        booksView.showAlertAndWait("No results found.", INFORMATION);
                                    }
                                });
                        }
                        else {
                            javafx.application.Platform.runLater(
                                new Runnable() {
                                    @Override
                                    public void run(){
                                        booksView.displayBooks(result);
                            }
                        });
                        }
                    } 
                    else {
                        javafx.application.Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run(){
                                    booksView.showAlertAndWait(
                                    "Enter a search string!", WARNING);
                                }
                            });                        
                    }
                }
                catch (Exception e) {
                    javafx.application.Platform.runLater(
                        new Runnable() {
                            @Override
                            public void run(){
                                booksView.showAlertAndWait("Database error. Please connect to database and try again", ERROR);
                            }
                        });       
                }
            }
        }.start();
    }

    /**
     *
     */
    protected void connect(){    
        new Thread(){
            @Override
            public void run(){
                try{
                    booksDb.connect();
                    final List<Book> result = booksDb.getAllBooks();
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        public void run(){
                            booksView.displayBooks(result);
                        }
                    });
                }
                catch (IOException | SQLException | ClassNotFoundException e) {
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.showAlertAndWait("Could not connect to database.", ERROR);
                        }
                    }); 
                } 
            }
        }.start();
    }

    /**
     *
     */
    protected void disconnect(){
        new Thread(){
            @Override
            public void run(){
                try{
                    booksDb.disconnect();
                }catch (IOException | SQLException e) {
            
                }
            }
        }.start();
    }
    
    /**
     *
     */
    protected void getAllBooks(){
        new Thread(){
            public void run(){
                try {
                    final List<Book> result = booksDb.getAllBooks();
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.displayBooks(result);
                        }
                    });           
                }
                catch (IOException | SQLException | NullPointerException e) {
                    javafx.application.Platform.runLater(
                            new Runnable() {
                            @Override
                            public void run(){
                                booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
                            }
                        });                       
                } 
            }
        }.start();
    }
    
    /**
     *
     */
    protected void addBookSelected(){
        new Thread(){
            @Override
            public void run(){
                try {
                    final List<Author> result = booksDb.getAllAuthors();
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            ArrayList<String> addResult;
                            addResult = booksView.showAddBookDialog(result);
                            new Thread(){
                            @Override
                                public void run(){
                                    try{
                                        booksDb.addBook(addResult.get(0), addResult.get(1), addResult.get(2), addResult.get(3), addResult.get(4), addResult.get(5), addResult.get(6), addResult.get(7));
                                    }
                                    catch (IllegalArgumentException e){
                                        javafx.application.Platform.runLater(
                                        new Runnable() {
                                        @Override
                                        public void run(){
                                            booksView.showAlertAndWait("Author data is invalid. Might have been removed or updated. Please try again.", ERROR);
                                        }
                                    });
                                    }
                                    catch (IOException | SQLException | NullPointerException e){
                                    javafx.application.Platform.runLater(
                                        new Runnable() {
                                        @Override
                                        public void run(){
                                            booksView.showAlertAndWait("Invalid data entered. Please try again.", ERROR);
                                        }
                                    });

                                    }
                                }
                            }.start();    
                        }
                    });   
                }
                catch (Exception e){    
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
                        }
                    });
                }
            }
        }.start();
    }
    
    /**
     *
     */
    protected void searchRatingSelected(){
        try{
            String searchResult = booksView.showSearchRatingDialog();
            new Thread(){
                @Override
                public void run(){
                    try{
                        final List<Book> result = booksDb.searchBooksByRating(searchResult);
                        javafx.application.Platform.runLater(
                            new Runnable(){
                            @Override
                            public void run(){
                                booksView.displayBooks(result);
                            }
                        });
                    }
                    catch (Exception e){
                    javafx.application.Platform.runLater(
                            new Runnable(){
                            @Override
                            public void run(){
                            booksView.showAlertAndWait("Not connected to database. Connect and try again", ERROR);
                            }
                        }); 
                    }
                }
            }.start();
        }
        catch (NullPointerException e){
            javafx.application.Platform.runLater(
                new Runnable(){
                @Override
                    public void run(){
                    booksView.showAlertAndWait("Please choose a value and try again.", ERROR);
                    }
                });
        }
    }
    
    /**
     *
     */
    protected void searchGenreSelected(){
        try{
            String searchResult = booksView.showSearchGenreDialog(); 
            new Thread(){
                @Override
                public void run(){
                    try{
                        final List<Book> result = booksDb.searchBooksByGenre(searchResult);
                        javafx.application.Platform.runLater(
                            new Runnable(){
                            @Override
                            public void run(){
                                booksView.displayBooks(result);
                            }
                        });
                    }
                    catch (Exception e){
                    javafx.application.Platform.runLater(
                            new Runnable(){
                            @Override
                            public void run(){
                            booksView.showAlertAndWait("Not connected to database. Connect and try again", ERROR);
                            }
                        }); 
                    }
                }
            }.start();
        }
        catch (NullPointerException e){
            javafx.application.Platform.runLater(
                new Runnable(){
                @Override
                    public void run(){
                    booksView.showAlertAndWait("Please choose a value and try again.", ERROR);
                    }
                });
        }
    }
         
    /**
     *
     */
    protected void addRatingSelected(){
        new Thread(){
        @Override
            public void run(){
                try{
                    final List<Book> result = booksDb.getAllBooks();
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.displayBooks(result);
                            ArrayList<String> result = booksView.showAddRatingDialog();
                            new Thread(){
                                @Override
                                public void run(){
                                    try{
                                        boolean updated = booksDb.updateRating(result.get(0), result.get(1));
                                        if(!updated){
                                            javafx.application.Platform.runLater(
                                            new Runnable() {
                                            @Override
                                            public void run(){
                                                booksView.showAlertAndWait("Could not set rating. No matches found.", INFORMATION);
                                            }
                                        });
                                        }
                                    }
                                    catch (Exception e){
                                        javafx.application.Platform.runLater(
                                            new Runnable() {
                                            @Override
                                            public void run(){
                                                booksView.showAlertAndWait("Invalid data entered.", ERROR);
                                            }
                                        });
                                    }
                                }
                            }.start();    
                        }
                    });
                }
                catch (IOException | SQLException | NullPointerException e) {
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
                        }
                    }); 
                } 
            }
        }.start();
    }
    
    /**
     *
     */
    protected void addAuthorSelected(){
        new Thread(){
        @Override
            public void run(){
                try{
                    final List<Book> result = booksDb.getAllBooks();
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.displayBooks(result);
                            new Thread(){
                                public void run(){
                                try{    
                                    List<Author> allAuthorsResult = booksDb.getAllAuthors();
                                    javafx.application.Platform.runLater(
                                        new Runnable() {
                                            @Override
                                            public void run(){
                                                ArrayList<String> result = booksView.showAddAuthorDialog(allAuthorsResult);
                                                new Thread(){
                                                    @Override
                                                    public void run(){
                                                        try{
                                                            booksDb.addAuthorToBook(result.get(0), result.get(1), result.get(2),result.get(3));
                                                        }
                                                        catch (Exception e){
                                                            javafx.application.Platform.runLater(
                                                                new Runnable() {
                                                                @Override
                                                                public void run(){
                                                                    booksView.showAlertAndWait("Invalid data entered.", ERROR);
                                                                }
                                                            });
                                                        }
                                                    }
                                                }.start();                                }
                                            
                                        });
                                }
                                catch (Exception e){
                                    javafx.application.Platform.runLater(
                                        new Runnable() {
                                            @Override
                                            public void run(){
                                                booksView.showAlertAndWait("Database error.", ERROR);
                                            }
                                        });
                                }
                                    
                                }
                            }.start();
                            
                        }
                        });
                }
                catch (IOException | SQLException | NullPointerException e) {
                    javafx.application.Platform.runLater(
                        new Runnable() {
                        @Override
                        public void run(){
                            booksView.showAlertAndWait("Not connected to database. Connect and try again.", ERROR);
                        }
                    }); 
                } 
            }
        }.start();
    }
}