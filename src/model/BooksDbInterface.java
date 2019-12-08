package model;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public boolean connect() throws IOException, SQLException, ClassNotFoundException;
    
    public void disconnect() throws IOException, SQLException;
    
    public List<Book> searchBooksByTitle(String title) throws IOException, SQLException;
    
    public List<Book> searchBooksByAuthor(String author) throws IOException, SQLException;
    
    public List<Book> searchBooksByIsbn(String isbn) throws IOException, SQLException;
                   
    public List<Book> searchBooksByRating(Rating rating) throws IOException, SQLException;
    
//    public List<Book> searchBooksByGenre(Genre genre) throws IOException, SQLException;
    
    
    public List<Book> getAllBooks() throws SQLException, IOException;
    
    public void addBook(String isbn, String title, String genre, String publisher, String pDate) throws IOException, SQLException;
    
    // TODO: Add abstract methods for all inserts, deletes and queries 
    // mentioned in the instructions for the assignement.
}
