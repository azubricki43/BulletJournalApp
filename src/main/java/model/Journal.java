/**
 * Main class that keeps track of a user's entries and associated data
 */
package model;

import journal.bulletjournaljavafx.ShareResource;
import storage.JournalStorage;

import java.time.LocalDate;
import java.util.*;

public class Journal {
    private String journalName;
    private HashMap<MonthYear, Log> monthlyLog;
    private HashMap<WeekYear, Log> weeklyLog;
    private HashMap<LocalDate, Log> dailyLog;
    private HashMap<String, Set<LocalDate>> habitTracker;


    public Journal(){
        this.journalName = "New Journal";
        this.monthlyLog = new HashMap<>();
        this.weeklyLog = new HashMap<>();
        this.dailyLog = new HashMap<>();
        this.habitTracker = new HashMap<>();
    }

    public void setJournalName(String name){
        this.journalName = name + "'s Journal";
    }

    public String getJournalName(){
        return journalName;
    }

    public Log getDailyLog(LocalDate date){
        if(dailyLog.get(date) == null){
            dailyLog.put(date, new Log());
        }
        return dailyLog.get(date);
    }

    public Log getWeeklyLog(WeekYear currentWeek){
        if(weeklyLog.get(currentWeek) == null){
            weeklyLog.put(currentWeek, new Log());
        }
        return weeklyLog.get(currentWeek);
    }

    public Log getMonthlyLog(MonthYear currentMonth){
        if(monthlyLog.get(currentMonth) == null){
            monthlyLog.put(currentMonth, new Log());
        }
        return monthlyLog.get(currentMonth);
    }

    public ArrayList<String> getHabits(){
        ArrayList<String> habits = new ArrayList<>();
        for(String key : habitTracker.keySet()){
            habits.add(key);
        }
        return habits;
    }

    public void addHabit(String name){
        this.habitTracker.put(name, new HashSet<>());
    }

    public boolean checkHabit(String name, LocalDate date){
        if (this.habitTracker.get(name).contains(date)){
            return true;
        }
        return false;
    }

    public void completedHabit(String name, LocalDate date){
        Set<LocalDate> dates = habitTracker.get(name);
        dates.add(date);
    }

    public void unCompleteHabit(String name, LocalDate date){
        Set<LocalDate> dates = habitTracker.get(name);
        dates.remove(date);
    }

    public void deleteHabit(String name){
        habitTracker.remove(name);
    }
}
