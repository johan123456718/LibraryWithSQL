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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements BooksDbInterface to connect to a MySQL database
 *
 * @author jlars
 */
public class BookMySQLDb implements BooksDbInterface {

    private Connection con;

    public BookMySQLDb() {
        con = null;
    }

    @Override
    public boolean connect() throws IOException, SQLException, ClassNotFoundException {
        String database = "laboration1";
        String user = "lab1"; // user name
        String pwd = "lab123"; // password 
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8" + "?useTimezone=true&serverTimezone=UTC";
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(server, user, pwd);
        return true;
    }

    @Override
    public void disconnect() throws IOException, SQLException{
        if (con != null) {
            con.close();
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws IOException, SQLException {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT T_book.isbn, T_Book.title, T_Book.genre, T_Book.rating, T_Book.publisher, T_Book.datePublished, T_Author.authorID, T_Author.name, T_Author.dob "
                + "FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN"
                + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Book.title LIKE ? ORDER BY T_Book.isbn ASC";
        PreparedStatement selectTitle = null;
        ResultSet rs = null;
        try{
            selectTitle = con.prepareStatement(sql);
            selectTitle.setString(1, "%" + searchTitle + "%");
            
            rs = selectTitle.executeQuery();
            result = ConvertToBook(rs);
            return result;
        }
        finally{
            if (selectTitle != null) {
                selectTitle.close();
                
            }
            if(rs != null){
                rs.close();
            }
        }
    }
    
    @Override
    public List<Book> searchBooksByGenre(String genre) throws IOException, SQLException{
        List<Book> result = new ArrayList<>();
        String sql = "SELECT T_book.isbn, T_Book.title, T_Book.genre, T_Book.rating, T_Book.publisher, T_Book.datePublished, T_Author.authorID, T_Author.name, T_Author.dob "
                + "FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN"
                + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Book.genre LIKE ? ORDER BY T_Book.isbn ASC";
        PreparedStatement selectGenre = null;
        ResultSet rs = null;
        try{
            selectGenre = con.prepareStatement(sql);
            selectGenre.setString(1, "%" + genre.toString() + "%");
            rs = selectGenre.executeQuery();
            result = ConvertToBook(rs);
            return result;
        }
        finally{
            if (selectGenre != null) {
            selectGenre.close();
            }
            if(rs != null){
                rs.close();
            }
        }
    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn) throws IOException, SQLException {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT T_book.isbn, T_Book.title, T_Book.genre, T_Book.rating, T_Book.publisher, T_Book.datePublished, T_Author.authorID, T_Author.name, T_Author.dob "
                + "FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN"
                + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Book.isbn LIKE ? ORDER BY T_Book.isbn ASC";
        PreparedStatement selectIsbn = null;
        ResultSet rs = null;
        try{
            selectIsbn = con.prepareStatement(sql);
            selectIsbn.setString(1, "%" + isbn + "%");
            rs = selectIsbn.executeQuery();
            result = ConvertToBook(rs);
            return result;
        }
        finally{
            if (selectIsbn != null) {
            selectIsbn.close();
            }
            if(rs != null){
                rs.close();
            }
        }
    }

    @Override
    public List<Book> searchBooksByAuthor(String author) throws IOException, SQLException {
        List<Book> result = new ArrayList<>();
        String sql = "SELECT T_book.isbn, T_Book.title, T_Book.genre, T_Book.rating, T_Book.publisher, T_Book.datePublished, T_Author.authorID, T_Author.name, T_Author.dob"
                + " FROM T_Book JOIN WrittenBy ON T_Book.isbn = WrittenBy.isbn JOIN"
                + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Author.name LIKE ? ORDER BY T_Book.isbn ASC";
        PreparedStatement selectAuthor = null;
        ResultSet rs = null;
        try{
            selectAuthor = con.prepareStatement(sql);
            selectAuthor.setString(1, "%" + author + "%");
            rs = selectAuthor.executeQuery();
            result = ConvertToBook(rs);
            return result;
        }
        finally{
            if (selectAuthor != null) {
                selectAuthor.close();
            }
            if(rs != null){
                rs.close();
            }
        }
    }
    
    @Override
    public List<Book> searchBooksByRating(String rating) throws IOException, SQLException{
        
        List<Book> result = new ArrayList<>();
       
        String sql = "SELECT T_book.isbn, T_Book.title, T_Book.genre, T_Book.rating, T_Book.publisher, T_Book.datePublished, T_Author.authorID, T_Author.name, T_Author.dob "
                + "FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN"
                + " T_Author ON T_Author.authorID = Writtenby.authorID WHERE T_Book.rating LIKE ? ORDER BY T_Book.isbn ASC";
        PreparedStatement selectRating = null;
        ResultSet rs = null;
        try{
            selectRating = con.prepareStatement(sql);
            selectRating.setString(1, "%" + rating + "%");
            rs = selectRating.executeQuery();
            result = ConvertToBook(rs);
            return result;
        }
        finally{
            if (selectRating != null) {
            selectRating.close();
            }
            if(rs != null){
                rs.close();
            }
        }
    }
    
    @Override
    public boolean updateRating(String isbn, String rating) throws IOException, SQLException {
        List<Book> result = new ArrayList<>();
        String sql = "UPDATE T_Book "
                + "SET T_Book.rating = ? WHERE T_Book.isbn = ?";
        PreparedStatement ratingUpdate = null;
        try{
            ratingUpdate = con.prepareStatement(sql);
            ratingUpdate.setString(1, rating );
            ratingUpdate.setString(2, isbn);
            return (ratingUpdate.execute());
        }
        finally{
            if (ratingUpdate != null) {
                ratingUpdate.close();
            }
        }
    }
    @Override
    public void addAuthorToBook(String isbn, String authorName, String authorId, String bDate) throws IOException, SQLException{
        PreparedStatement getAllAuthors = null;
        PreparedStatement insertWrittenBy = null;
        PreparedStatement insertAuthor = null;
        
        con.setAutoCommit(false);
        ResultSet rs = null;
        try{
            String sql = "SELECT * FROM T_Author ORDER BY AuthorId ASC;";
            getAllAuthors = con.prepareStatement(sql);
            rs = getAllAuthors.executeQuery();
            boolean authorExists = false;
            int idIndex = 0;
            while (rs.next()){
                idIndex = rs.getInt("authorId");
                if(authorId!= null){
                    if(authorId.equals(String.valueOf(idIndex))){
                        //check for existing Id and verify all attributes for consistency
                        if(bDate.equals(rs.getString("dob")) && authorName.equals(rs.getString("name"))){
                            authorExists = true;
                            break;
                        }
                        else{
                            throw new IllegalArgumentException();
                        }
                    }
                }
            }
            sql = "INSERT INTO WrittenBy(isbn, authorID) VALUES(?, ?)";
            insertWrittenBy = con.prepareStatement(sql);
            insertWrittenBy.setString(1, isbn);
            if(!authorExists){
                insertWrittenBy.setInt(2, idIndex+1);
                sql = "INSERT INTO T_Author(name, dob, authorId) VALUES(?, ?, ?)";
                insertAuthor = con.prepareStatement(sql);
                insertAuthor.setString(1, authorName);
                insertAuthor.setString(2, bDate);
                insertAuthor.setInt(3, idIndex+1);
                insertAuthor.execute();
            }
            else{
                insertWrittenBy.setString(2, authorId);
            }
            insertWrittenBy.execute();
            con.commit();
        }
        catch(Exception e){
            con.rollback();
            throw e;
        }
        finally{
            if(getAllAuthors != null){
              getAllAuthors.close();
            }
            if(insertWrittenBy != null){
                insertWrittenBy.close();
            }
            if(insertAuthor != null){
                insertAuthor.close();
            }
            if(rs != null){
                rs.close();
            }
            con.setAutoCommit(true);
        }
    }
    @Override
    public List<Book> getAllBooks() throws IOException, SQLException{
        List<Book> result = new ArrayList<>();
        String sql = "SELECT * "
                + "FROM T_Book JOIN Writtenby ON T_Book.isbn = Writtenby.isbn JOIN"
                + " T_Author ON T_Author.authorID = Writtenby.authorID ORDER BY T_Book.isbn ASC";
        PreparedStatement selectAll = null;
        ResultSet rs = null;
        try{
            selectAll = con.prepareStatement(sql);
            rs = selectAll.executeQuery();
            result = ConvertToBook(rs);
            return result;
        }
        finally{
            if (selectAll != null) {
                selectAll.close();
            }
            if(rs != null){
                rs.close();
            }
        }
    }

    @Override
    public void addBook(String isbn, String title, String genre, String publisher, String pDate, String authorName, String authorId, String bDate) throws IOException, SQLException {
        PreparedStatement getAllAuthors = null;
        PreparedStatement insertBook = null;
        PreparedStatement insertAuthor = null;
        PreparedStatement insertWrittenBy = null; 
        ResultSet rs = null;
        try{
            con.setAutoCommit(false);
            String sql = "SELECT * FROM T_Author ORDER BY AuthorId ASC;";
            getAllAuthors = con.prepareStatement(sql);
            rs = getAllAuthors.executeQuery();
            boolean authorExists = false;
            int idIndex = 0;
            while (rs.next()){
                idIndex = rs.getInt("authorId");
                if(authorId!= null){
                    if(authorId.equals(String.valueOf(idIndex))){
                        //check for existing Id and verify all attributes for consistency
                        if(bDate.equals(rs.getString("dob")) && authorName.equals(rs.getString("name"))){
                            authorExists = true;
                            break;
                        }
                        else{
                            throw new IllegalArgumentException();
                        }
                    }
                }
            }
            sql = "INSERT INTO T_Book(isbn, title, genre, publisher, datePublished) VALUES(?, ?, ?, ?, ?)";
            insertBook = con.prepareStatement(sql);
            insertBook.setString(1, isbn);
            insertBook.setString(2, title);
            insertBook.setString(3, genre);
            insertBook.setString(4, publisher);
            insertBook.setString(5, pDate);
            insertBook.execute();
            
            sql = "INSERT INTO WrittenBy(isbn, authorID) VALUES(?, ?)";
            insertWrittenBy = con.prepareStatement(sql);
            insertWrittenBy.setString(1, isbn);
            if(!authorExists){
                insertWrittenBy.setInt(2, idIndex+1);
                sql = "INSERT INTO T_Author(name, dob, authorId) VALUES(?, ?, ?)";
                insertAuthor = con.prepareStatement(sql);
                insertAuthor.setString(1, authorName);
                insertAuthor.setString(2, bDate);
                insertAuthor.setInt(3, idIndex+1);
                insertAuthor.execute();
            }
            else{
                insertWrittenBy.setString(2, authorId);
            }
            insertWrittenBy.execute();
            con.commit();
        }
        catch(Exception e){
            con.rollback();
            throw e;
        }
        finally{
            if(getAllAuthors != null){
                getAllAuthors.close();
            }
            if(insertAuthor != null){
               insertAuthor.close();
            }
            if(insertBook != null){
                insertBook.close();
            }
            if(insertWrittenBy != null){
               insertWrittenBy.close();
            }
            if(rs != null){
                rs.close();
            }
            con.setAutoCommit(true);
        }
    }

    private Genre convertToGenre(String genre) {
        switch (genre) {
            case "fantasy":
                return Genre.FANTASY;
            case "sci-fi":
                return Genre.SCI_FI;
            case "crime":
                return Genre.CRIME;
            case "drama":
                return Genre.DRAMA;
            case "romance":
                return Genre.ROMANCE;
            case "science":
                return Genre.SCIENCE;
            default:
                throw new IllegalArgumentException();

        }
    }

    private Rating convertToRating(int ratingInt) {
        switch (ratingInt) {
            case 0:
                return Rating.NONE;
            case 1:
                return Rating.ONE;
            case 2:
                return Rating.TWO;
            case 3:
                return Rating.THREE;
            case 4:
                return Rating.FOUR;
            case 5:
                return Rating.FIVE;
            default:
                throw new IllegalArgumentException();
        }
    }

    private List<Book> ConvertToBook(ResultSet rs) throws SQLException {
        List<Book> result = new ArrayList<>();
        while (rs.next()) {
            long isbn = rs.getLong("isbn");
            String title = rs.getString("title");
            int ratingInt = rs.getInt("Rating");
            Rating rating = convertToRating(ratingInt);
            String genreString = rs.getString("genre");
            Genre genre = convertToGenre(genreString);
            Date date = rs.getDate("datePublished");
            Author author = ConvertToAuthor(rs);
            boolean bookExists = false;
            if (result.size() > 0) {
                if (result.get(result.size() - 1).getIsbn() == isbn) {
                    result.get(result.size() - 1).addAuthor(author);
                    bookExists = true;
                }
            }
            if(!bookExists){
                result.add(new Book(isbn, title, date, author, genre, rating));
            }
        }
        return result;
    }
    
    @Override
    public List<Author> getAllAuthors() throws SQLException {
        List<Author> allAuthors = new ArrayList();
        ResultSet rs = null;
        PreparedStatement getAllAuthors = null;
        String sql = "SELECT * FROM T_Author ORDER BY AuthorId ASC;";
        try{
            getAllAuthors = con.prepareStatement(sql);
            rs = getAllAuthors.executeQuery();  
            while(rs.next()){
                allAuthors.add(ConvertToAuthor(rs));
            }
            return allAuthors;
        }
        finally {
            if(getAllAuthors != null){
                getAllAuthors.close();
            }
            if(rs != null){
                rs.close();
            }         
        }
    }
    
    private Author ConvertToAuthor(ResultSet rs) throws SQLException {
            int authorId = rs.getInt("authorId");
            String name = rs.getString("name");
            Date dob = rs.getDate("dob");
            Author author = new Author(authorId, name, dob);
            return author;    
    }
}