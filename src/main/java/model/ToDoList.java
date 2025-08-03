package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class ToDoList {
    private ArrayList<Task> tasks;

    public ToDoList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ToDoList() {
        this.tasks = new ArrayList<>();
    }

    public void getToDoList(){
        System.out.println("To-Do List");
        for(Task task : tasks){
            System.out.println(task);
        }
        System.out.println("End of To-Do List");
    }

    public void addTask(Task task){
        this.tasks.add(task);
    }

    public ArrayList<Task> getTasks(){
        return tasks;
    }

    public void deleteTask(Task task){
        this.tasks.remove(task);
    }

    public static void main(String[] args){
        Task task2 = new Task("Walk the dog");
        Task task3 = new Task("Grocery Shopping");
        ToDoList list = new ToDoList();
        list.addTask(task2);
        list.addTask(task3);
        list.getToDoList();
    }
}
