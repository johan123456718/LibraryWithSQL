package model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se, Johan Challita, challita@kth.se, Jesper Larssoon, jespelar@kth.se
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
     * Constructor with arguments to create a book
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
     * Returns book ISBN
     * @return
     */
    public long getIsbn() { 
        return isbn; 
    }

    /**
     * Returns book title
     * @return
     */
    public String getTitle() { 
        return title; 
    }

    /**
     * returns the date when it was published
     * @return
     */
    public Date getPublished() { 
        return published; 
    }

    /**
     * return the genre of the book
     * @return
     */
    public Genre getGenre(){
        return this.genre;
    }

    /**
     * returns the book's rating
     * @return
     */
    public Rating getRating(){
        return this.rating;
    }
    
    /**
     * returns the list of authors
     * @return
     */
    public List<Author> getAuthors(){
        return this.authors;
    }
    
    /**
     *  Adding an author to a book
     * @param author
     */
    public void addAuthor(Author author){
        this.authors.add(author);
    }
    
    /**
     * returns a string of the object's initialization
     * @return
     */
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString();
    }
}
