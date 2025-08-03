package model;

import java.time.LocalDate;
import java.util.Date;

public class Task{
    private String name;
    private boolean completed;

    public Task(String name, boolean completed) {
        this.name = name;
        this.completed = false;
    }

    public Task(String name){
        this.name = name;
        this.completed = false;
    }

    public void setCompleted(boolean value){
        this.completed = value;
    }

    public boolean isCompleted(){
        return this.completed;
    }

    public String toString(){
        String str = super.toString();
        return str + "Completed: " + completed;
    }

    public String getName(){
        return name;
    }

    public boolean getCompleted(){
        return completed;
    }

}
