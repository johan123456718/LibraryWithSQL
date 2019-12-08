package model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class Book {
    
    private long isbn; // should check format
    private String title;
    private Date published;
    private String storyLine = "";
    private Genre genre;
    private Rating rating;
    private List<Author> authors;
    // TODO: 
    // Add authors, and corresponding methods, to your implementation 
    // as well, i.e. "private ArrayList<Author> authors;"
    
    public Book(long isbn, String title, Date published, Author author, Genre genre, Rating rating) {
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.authors = new ArrayList();
        this.authors.add(author);
        this.genre = genre;
        this.rating = rating;
    }
    
    public long getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
    public String getStoryLine() { return storyLine; }
    public Genre getGenre(){
        return this.genre;
    }
    public Rating getRating(){
        return this.rating;
    }
    
    public List<Author> getAuthors(){
        //needs deep copy?
        return this.authors;
    }
    
    public void addAuthor(Author author){
        this.authors.add(author);
    }
    
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }
    
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
