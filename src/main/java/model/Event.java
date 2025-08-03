package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Event {
    private String eventName;
    private String eventDescription;

    public Event(String eventName, String eventDescription) {
        if(eventName == null || eventName.equals("")){
            this.eventName = "New Event";
        }
        if(eventDescription == null || eventName.equals("")){
            this.eventDescription = "";
        }
        else{
            this.eventName = eventName;
            this.eventDescription = eventDescription;
        }
    }

    public String toString(){
        String str =  eventName + '\n';
        if(eventDescription.equals("")){
            return str;
        }
        return str + "Description: " + eventDescription + '\n';
    }

    public String getEventName(){
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }
}
