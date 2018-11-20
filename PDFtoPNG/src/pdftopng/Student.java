/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdftopng;
public class Student {
    
    public String lessonname;
    public String name;
    public String date;
    public String id;
    public String type;
    public int examnumber;
    public int[] grades;
    public int[] percentages;
    public int finalgrade;
    public int totalgrade =0;
    
    Student(String lessonname,String date,String type,int examnumber,String name,String id,int gradeslength){
        this.lessonname = lessonname;
        this.date = date;
        this.type = type;
        this.examnumber = examnumber;
        this.name = name;
        this.id = id;
        grades = new int[gradeslength+1]; 
    }
    
    Student(String name,int totalgrade){
        this.name = name;
        this.totalgrade = totalgrade;
        grades = new int[1];
        grades[0] = 0;
    }
    
    @Override
    public String toString(){
    StringBuilder a = new StringBuilder();
    a.append("Lesson Name is ");
    a.append(lessonname);
    a.append(" name is: ");
    a.append(name);
    String b = a.toString();
    return b;
}    
    
    public int getGrades(){
        int max =0;
        if(grades.length!=0){
        for(int i=0;i<grades.length;i++){
            max = max + grades[i];
        }
        
        max = max+totalgrade;
        }
        
        else{
            max = max + totalgrade;
        }
        
        return max;
    }
    
    public void setTotalGrade(int a){
        totalgrade = totalgrade + a;
    }
}
