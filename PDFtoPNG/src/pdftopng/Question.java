/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdftopng;

/**
 *
 * @author Melih
 */
public class Question {
    public int qnumber;
    public String comment;
    public int maxgrade;
    public int cgrade;
            
    Question(int qnumber,int maxgrade){
        this.qnumber = qnumber;
        this.maxgrade = maxgrade;
    }
    
    @Override
    public String toString(){
        StringBuilder a = new StringBuilder();
        a.append("Question Number is");
        a.append(qnumber);
        a.append("Max Grade is");
        a.append(maxgrade);
        String b =a.toString();
        return b;
        
        
        
    }
}
