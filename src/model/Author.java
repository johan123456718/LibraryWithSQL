/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Johan C
 */
public class Author {
    private int authorId;
    private String name;
    
    public Author(int authorId, String name){
        this.authorId = authorId;
        this.name = name;
    }
    
    public int getAuthorId(){
        return authorId;
    }
    
    public String getName(){
        return name;
    }
    
    
    /**
     * String representation of Author.
     * @return 
     */
    @Override
    public String toString() {
        String info = "";
        info += "Name: " + name + ",ID: " + authorId;
        return info;
    }

    
}
