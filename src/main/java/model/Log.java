/**
 * Class that defines a journal entry, used for daily, weekly, and monthly to-do's and reflections
 * @author Angel Zubricki
 */
package model;

import java.util.ArrayList;

public class Log {
    private ToDoList list;
    private String entry;
    private ArrayList<Event> events;

    /**
     * Default constructor
     */
    public Log(){
        this.list = new ToDoList();
        this.entry = null;
        this.events = new ArrayList<>();
    }

    /**
     * Add task to the log's to-do list
     * @param task to add
     */
    public void addTask(Task task){
        list.addTask(task);
    }

    /**
     * Create journal entry
     * @param entry to add to log
     */
    public void createEntry(String entry){
        this.entry = entry;
    }

    public ToDoList getList(){
        return this.list;
    }

    public String getEntry(){
        return this.entry;
    }

    public ArrayList<Event> getEvents(){
        return this.events;
    }

    public void createEvent(Event event){
        this.events.add(event);
    }
}
