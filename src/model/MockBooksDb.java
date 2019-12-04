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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
    private Connection con;
    
    public MockBooksDb() {
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
        //ska den stängas????            
        return true;
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
        String sql = "SELECT * FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN" 
        + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Book.title LIKE ?";
        List<Book> result = new ArrayList<>();
        
            PreparedStatement selectTitle = con.prepareStatement(sql);
            selectTitle.setString(1, "%" + searchTitle + "%");
            // 1. precompiled statment?
            
            ResultSet rs = selectTitle.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int ccount = metaData.getColumnCount();
            for (int c = 1; c <= ccount; c++) {
                System.out.print(metaData.getColumnName(c) + "\t");
            }
            System.out.println();

            // Get the attribute values
            while (rs.next()) {
                    long isbn=rs.getLong("isbn");
                    String title = rs.getString("title");
                    Date date = rs.getDate("datePublished");
                    System.out.println(isbn + title + date);

                    int authorId = rs.getInt("authorId");
                    String name = rs.getString("name");
                    Date dob = rs.getDate("dob");
                    Author author = new Author(authorId, name, dob);
                    //Author author = new Author(1, "Testmannen", new Date(1994-04-19));
                    if(result.size() > 0){
                        if(result.get(result.size()-1).getIsbn()==isbn){    
                            result.get(result.size()-1).addAuthor(author);
                        }
                        else {
                            result.add(new Book(isbn, title, date, author));
                        }
                    }
                    else {
                        result.add(new Book(isbn, title, date, author));
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
}
