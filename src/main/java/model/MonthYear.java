package model;

import java.time.Month;
import java.time.LocalDate;
import java.time.Year;
import java.util.Objects;

public class MonthYear {
    private Month month;
    private int year;
    private static MonthYear currentMonthYear;

    public MonthYear(){
        this.month = LocalDate.now().getMonth();
        this.year = LocalDate.now().getYear();
    }

    public MonthYear(Month month, int year){
        this.month = month;
        this.year = year;
    }

    public static MonthYear getCurrentMonthYear(){
        if(currentMonthYear == null){
            currentMonthYear = new MonthYear();
        }
        return currentMonthYear;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MonthYear monthYear)) return false;
        return year == monthYear.year && month.getValue() == monthYear.month.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, year);
    }

    public String toString(){
        String monthString;
        if(this.month.getValue() <= 9){
            monthString = String.valueOf(month.getValue());
            monthString.concat("0");
        }
        else{
            monthString = String.valueOf(month.getValue());
        }
        return String.valueOf(year) + '-' + monthString;
    }

    public static MonthYear parse (String monthYear){
        String[] array = monthYear.split("-");
        int year = Integer.parseInt(array[0]);
        Month month = Month.of(Integer.parseInt(array[1]));
        return new MonthYear(month, year);
    }
}
