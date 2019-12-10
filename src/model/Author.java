/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Date;

/**
 * This class represents an author to a book
 * @author Johan Challita, challita@kth.se, Jesper Larsson, jespelar@kth.se
 */
public class Author {
    private final int authorId;
    private final String name;
    private final Date dob;
    
    /**
     *  Constructor with arguments to an author
     * @param authorId
     * @param name
     * @param date
     */
    public Author(int authorId, String name, Date date){
        this.authorId = authorId;
        this.name = name;
        this.dob = date; 
    }
    
    /**
     *  returns AuthorID
     * @return
     */
    public int getAuthorId(){
        return authorId;
    }
    
    /**
     *  returns author's name
     * @return
     */
    public String getName(){
        return name;
    }
    
    /**
     *  returns author's birth day
     * @return
     */
    public Date getDob(){
        return dob;
    }
    
    
    /**
     * String representation of Author.
     * @return 
     */
    @Override
    public String toString() {
        return name;
    }

    
}