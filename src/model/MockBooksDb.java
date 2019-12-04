/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 *
 * Your implementation should access a real database.
 *
 * @author anderslm@kth.se
 */
public class MockBooksDb implements BooksDbInterface {

    private final List<Book> books;
    private Connection con;
    
    public MockBooksDb() {
        books = Arrays.asList(DATA);
    }

    @Override
    public boolean connect() throws IOException, SQLException, ClassNotFoundException {
        String database = "laboration1";
        String user = "lab1"; // user name
        String pwd = "lab123"; // password 
        System.out.println(user + ", *********");
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8" + "?useTimezone=true&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(server, user, pwd);
        System.out.println("Connected!");
        
        if(con == null){
            return false;
        }
                
        executeQuery(con, "SELECT * FROM T_Book");
        return true;
    }
    
    @Override
    public void disconnect() throws IOException, SQLException {
        try {
            if (con != null) {
                con.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle)
            throws IOException, SQLException {
        // mock implementation
        // NB! Your implementation should select the books matching
        // the search string via a query with to a database.
        List<Book> result = new ArrayList<>();
        searchTitle = searchTitle.toLowerCase();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTitle)) {
                result.add(book);
            }
        }
        return result;
    }

    @Override
    public List<Book> searchBooksByGenre(Genre genre) throws IOException, SQLException {
        return null;
    }

    @Override
    public List<Book> searchBooksByRating(Rating rating) throws IOException, SQLException {
        return null;
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) throws IOException, SQLException {
        return null;
    }

    @Override
    public void addBook(String isbn, String title, Genre genre, String author, Date date) {

    }
    
    
    public static void executeQuery(Connection con, String query) throws SQLException {

        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);

            // Get the attribute names
            ResultSetMetaData metaData = rs.getMetaData();
            int ccount = metaData.getColumnCount();
            for (int c = 1; c <= ccount; c++) {
                System.out.print(metaData.getColumnName(c) + "\t");
            }
            System.out.println();

            // Get the attribute values
            while (rs.next()) {
                // NB! This is an example, -not- the preferred way to retrieve data.
                // You should use methods that return a specific data type, like
                // rs.getInt(), rs.getString() or such.
                // It's also advisable to store each tuple (row) in an object of
                // custom type (e.g. Employee).
                for (int c = 1; c <= ccount; c++) {
                    System.out.print(rs.getObject(c) + "\t");
                }
                System.out.println();
            }

        }
    }

    private static final Book[] DATA = {
        new Book(1, "123456789", "Databases Illuminated", new Date(1990, 1, 1)),
        new Book(2, "456789012", "The buried giant", new Date(2000, 1, 1)),
        new Book(2, "567890123", "Never let me go", new Date(2000, 1, 1)),
        new Book(2, "678901234", "The remains of the day", new Date(2000, 1, 1)),
        new Book(2, "234567890", "Alias Grace", new Date(2000, 1, 1)),
        new Book(3, "345678901", "The handmaids tale", new Date(2010, 1, 1))
    };
}
