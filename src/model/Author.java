/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Date;

/**
 *
 * @author Johan C
 */
public class Author {
    private final int authorId;
    private final String name;
    private final Date dob;
    
    public Author(int authorId, String name, Date date){
        this.authorId = authorId;
        this.name = name;
        this.dob = date; 
    }
    
    public int getAuthorId(){
        return authorId;
    }
    
    public String getName(){
        return name;
    }
    
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