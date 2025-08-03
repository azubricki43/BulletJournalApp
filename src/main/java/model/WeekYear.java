package model;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

public class WeekYear {
    private int week;
    private int year;
    private static WeekYear currentWeekYear;

    public WeekYear(){
        this.week = LocalDate.now().minusDays(-1).get(WeekFields.ISO.weekOfWeekBasedYear());
        this.year = LocalDate.now().getYear();
    }

    public WeekYear(int week, int year){
        this.week = week;
        this.year = year;
    }

    public int getWeek(){
        return this.week;
    }

    public LocalDate getFirstDayOfWeek(){
        WeekFields weekFields = WeekFields.of(Locale.US);
        LocalDate date = LocalDate.of(year,1,1)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1);
        return date;
    }

    public static WeekYear getCurrentWeekYear(){
        if(currentWeekYear == null){
            currentWeekYear = new WeekYear();
        }
        return currentWeekYear;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WeekYear weekYear)) return false;
        return week == weekYear.week && year == weekYear.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(week, year);
    }

    public String toString(){
        String weekString;
        if (week <= 9){
            weekString = String.valueOf(week);
            weekString.concat("0");
        }
        else{
            weekString = String.valueOf(week);
        }
        return weekString + '-' + year;
    }

    public static WeekYear parse(String weekYear){
        String[] array = weekYear.split("-");
        int week = Integer.parseInt(array[0]);
        int year = Integer.parseInt(array[1]);
        return new WeekYear(week, year);
    }
}
