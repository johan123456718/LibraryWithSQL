package model;

import java.io.IOException;
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
     * @return true on successful connection.
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public boolean connect() throws IOException, SQLException, ClassNotFoundException;
    
    /**
     *
     * @throws IOException
     * @throws SQLException
     */
    public void disconnect() throws IOException, SQLException;
    
    /**
     *
     * @param title
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByTitle(String title) throws IOException, SQLException;
    
    /**
     *
     * @param author
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByAuthor(String author) throws IOException, SQLException;
    
    /**
     *
     * @param isbn
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByIsbn(String isbn) throws IOException, SQLException;
                   
    /**
     *
     * @param genre
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByGenre(String genre) throws IOException, SQLException;
    
    /**
     *
     * @param rating
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByRating(String rating) throws IOException, SQLException;
    
    /**
     *
     * @param isbn
     * @param rating
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public boolean updateRating(String isbn, String rating) throws IOException, SQLException;
   
    /**
     *
     * @param isbn
     * @param authorName
     * @param authorId
     * @param bDate
     * @throws IOException
     * @throws SQLException
     */
    public void addAuthorToBook(String isbn, String authorName, String authorId, String bDate) throws IOException, SQLException;
    
    /**
     *
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<Book> getAllBooks() throws SQLException, IOException;
    
    /**
     *
     * @param isbn
     * @param title
     * @param genre
     * @param publisher
     * @param pDate
     * @param authorName
     * @param authorId
     * @param bDate
     * @throws IOException
     * @throws SQLException
     */
    public void addBook(String isbn, String title, String genre, String publisher, String pDate, String authorName, String authorId, String bDate) throws IOException, SQLException;
    
    /**
     *
     * @return
     * @throws SQLException
     */
    public List<Author> getAllAuthors() throws SQLException;
}