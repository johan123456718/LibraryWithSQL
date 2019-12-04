/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksdbclient.model;

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
        List<Book> result = new ArrayList<>();
        String sql = "SELECT T_book.isbn, T_Book.title, T_Book.genre, T_Book.rating, T_Book.publisher, T_Book.datePublished, T_Author.authorID, T_Author.name, T_Author.dob "
        + "FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN" 
        + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Book.title LIKE ?";
        PreparedStatement selectTitle = con.prepareStatement(sql);
        selectTitle.setString(1, "%" + searchTitle + "%");
            
        ResultSet rs = selectTitle.executeQuery();

        result = ConvertToBook(rs);
                if (selectTitle != null){
            selectTitle.close();
        }
                System.out.println(result.get(0).getAuthors());
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
    
    private Genre convertToGenre(String genre){
        switch(genre){
            case "fantasy": return Genre.FANTASY;
            case "sci-fi": return Genre.SCI_FI;
            case "crime": return Genre.CRIME;
            case "drama": return Genre.DRAMA;
            case "romance": return Genre.ROMANCE;
            case "science": return Genre.SCIENCE;
            default: throw new IllegalArgumentException();
            
        }
    }
    private Rating convertToRating(int ratingInt){
        switch(ratingInt){
            case 0: return Rating.NONE;
            case 1: return Rating.ONE;
            case 2: return Rating.TWO;
            case 3: return Rating.THREE;
            case 4: return Rating.FOUR;
            case 5: return Rating.FIVE;
            default: throw new IllegalArgumentException();
        }
    }
    
    private List<Book> ConvertToBook(ResultSet rs) throws SQLException{
            List<Book> result = new ArrayList<>();
            while (rs.next()) {
            long isbn=rs.getLong("isbn");
            String title = rs.getString("title");
            int ratingInt = rs.getInt("Rating");
            System.out.println(ratingInt);
            Rating rating = convertToRating(ratingInt);
            String genreString = rs.getString("genre");
            Genre genre = convertToGenre(genreString);
            Date date = rs.getDate("datePublished");

            int authorId = rs.getInt("authorId");
            String name = rs.getString("name");
            Date dob = rs.getDate("dob");
            Author author = new Author(authorId, name, dob);
            if(result.size() > 0){
                if(result.get(result.size()-1).getIsbn()==isbn){    
                    result.get(result.size()-1).addAuthor(author);
                }
                else {
                    result.add(new Book(isbn, title, date, author, genre, rating));
                }
            }
            else {
                result.add(new Book(isbn, title, date, author, genre, rating));
            }
        }
        return result;
    }
}
