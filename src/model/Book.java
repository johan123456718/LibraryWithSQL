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
    private final String title;
    private final Date published;
    private final Genre genre;
    private final Rating rating;
    private final List<Author> authors;
    // TODO: 
    // Add authors, and corresponding methods, to your implementation 
    // as well, i.e. "private ArrayList<Author> authors;"
    
    /**
     *
     * @param isbn
     * @param title
     * @param published
     * @param author
     * @param genre
     * @param rating
     */
    public Book(long isbn, String title, Date published, Author author, Genre genre, Rating rating) {
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.authors = new ArrayList();
        this.authors.add(author);
        this.genre = genre;
        this.rating = rating;
    }
    
    /**
     *
     * @return
     */
    public long getIsbn() { 
        return isbn; 
    }

    /**
     *
     * @return
     */
    public String getTitle() { 
        return title; 
    }

    /**
     *
     * @return
     */
    public Date getPublished() { 
        return published; 
    }

    /**
     *
     * @return
     */
    public Genre getGenre(){
        return this.genre;
    }

    /**
     *
     * @return
     */
    public Rating getRating(){
        return this.rating;
    }
    
    /**
     *
     * @return
     */
    public List<Author> getAuthors(){
        return this.authors;
    }
    
    /**
     *
     * @param author
     */
    public void addAuthor(Author author){
        this.authors.add(author);
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
