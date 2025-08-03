/**
 * Controller class for Weekly View Screen
 ***
 * @author Angel Zubricki
 */
package journal.bulletjournaljavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import model.Event;
import model.Log;
import model.Task;
import model.WeekYear;

import java.io.IOException;
import java.net.URL;
import java.time.temporal.WeekFields;
import java.util.ArrayList;

import java.time.LocalDate;
import java.util.ResourceBundle;

public class WeeklyView implements Initializable {
    @FXML
    private VBox sundayVBox, mondayVBox, tuesdayVBox, wednesdayVBox, thursdayVBox, fridayVBox, saturdayVBox, habitVBox, weeklyToDoVBox;

    @FXML
    private Label weekLabel, promptLabel,
            sundayNumber, mondayNumber, tuesdayNumber, wednesdayNumber, thursdayNumber, fridayNumber, saturdayNumber;
    @FXML
    private TextArea weeklyNotes, eventDescriptionTextArea;

    @FXML
    private Button addTaskButton, createEventButton;

    @FXML
    private TextField taskTextField, eventNameTextField;

    @FXML
    private DatePicker eventDatePicker;

    ArrayList<Label> daysOfWeekLabels = new ArrayList<>();
    ArrayList<VBox> daysOfWeekVBox = new ArrayList<>();
    ShareResource resource = ShareResource.getInstance();
    private WeekYear currentWeek = WeekYear.getCurrentWeekYear();


    /**
     * Function called whenever scene is loaded to load all data
     */
    public void setWeek(){
        LocalDate instance = LocalDate.now();
        int week = currentWeek.getWeek();
        weekLabel.setText("Week " + week);
        LocalDate sunday = instance.minusDays(instance.getDayOfWeek().getValue() % 7);
        int dayCounter = sunday.getDayOfMonth();
        int monthMaxDate = instance.getMonth().maxLength();
        if(instance.getYear() % 4 != 0 && monthMaxDate == 29){
            monthMaxDate = 28;
        }
        for(Label dayOfWeek : daysOfWeekLabels){
            dayOfWeek.setText(String.valueOf(dayCounter));
            dayCounter++;
            if(dayCounter > monthMaxDate){
                dayCounter = 1;
            }
            if(Integer.parseInt(dayOfWeek.getText()) == instance.getDayOfMonth()){
                dayOfWeek.getStyleClass().add("number-label");
            }
        }
        Log weeklyLog = resource.getJournal().getWeeklyLog(currentWeek);
        generateHabits();
        displayToDos(weeklyLog);
        String weeklyEntry = resource.getJournal().getWeeklyLog(currentWeek).getEntry();
        weeklyNotes.setText(weeklyEntry);
        weeklyNotes.getStyleClass().add("custom-text-area");
        weeklyNotes.textProperty().addListener((obs, oldVal, newVal) -> {
            weeklyLog.createEntry(weeklyNotes.getText());
        });
        setEvents();
    }

    /**
     * Initialize function to set up week and add variables to lists when program starts
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDayOfWeekList();
        setWeek();
    }

    /**
     * Function called when create Event button is pressed, creates new Event
     */
    public void createEvent(){
        LocalDate eventDate = eventDatePicker.getValue();
        if(eventDate == null){
            eventDate = LocalDate.now();
        }
        Log dailyLog = resource.getJournal().getDailyLog(eventDate);
        dailyLog.createEvent(new Event(eventNameTextField.getText(), eventDescriptionTextArea.getText()));
        setEvents();
        eventDatePicker.getEditor().clear();
        eventNameTextField.clear();
        eventDescriptionTextArea.clear();
    }

    /**
     * Displays to-do's for the week
     * @param weeklyLog with list to display
     */
    public void displayToDos(Log weeklyLog){
        weeklyToDoVBox.getChildren().clear();
        for(Task task : weeklyLog.getList().getTasks()){
            CheckBox newTask = new CheckBox(task.getName());
            newTask.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    task.setCompleted(true);
                }
                else{
                    task.setCompleted(false);
                }
            });
            if(task.isCompleted()){
                newTask.setSelected(true);
            }
            newTask.getStyleClass().add("custom-checkbox");
            newTask.setWrapText(true);
            GridPane taskLayout = createNewCheckBox(newTask);
            Button deleteButton = (Button) taskLayout.getChildren().get(1);
            deleteButton.setOnAction(actionEvent -> {
                weeklyLog.getList().deleteTask(task);
                weeklyToDoVBox.getChildren().remove(taskLayout);
            });
            weeklyToDoVBox.getChildren().add(taskLayout);
        }
    }

    /**
     * Function called when Add Task button is pressed, creates new task and adds it to list
     */
    public void addTask(){
        if(taskTextField.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Empty field!");
            alert.setContentText("Please enter a task");
            alert.show();
            return;
        }
        String newToDo = taskTextField.getText();
        Task newTask = new Task(newToDo);
        resource.getJournal().getWeeklyLog(currentWeek).addTask(newTask);
        displayToDos(resource.getJournal().getWeeklyLog(currentWeek));
        taskTextField.clear();
    }

    /**
     * Function to display Events in respective VBoxes
     */
    public void setEvents(){
        LocalDate day = currentWeek.getFirstDayOfWeek();
        for(VBox vbox : daysOfWeekVBox){
            vbox.getChildren().clear();
            Log dailyLog = resource.getJournal().getDailyLog(day);
            if(!dailyLog.getEvents().isEmpty()){
                for(Event event : dailyLog.getEvents()){
                    Label label = new Label(event.getEventName());
                    label.getStyleClass().add("event-label");
                    label.setWrapText(true);
                    label.setMaxWidth(100);
                    label.setOnMouseClicked(e -> {
                        try {
                            createPopUp(label, event, dailyLog);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    vbox.getChildren().add(label);
                }
            }
            day = day.minusDays(-1);
        }
    }

    /**
     * Function to create popups for when an event is clicked on
     * @param label with event name, triggers the popup when clicked
     * @param event object
     * @param dailyLog of currentday to pass to deleteEvent created in listener
     * @throws IOException in case of exception
     */
    public void createPopUp(Label label, Event event, Log dailyLog) throws IOException {
        Popup eventPopUp = new Popup();
        Bounds labelBounds = label.localToScreen(label.getBoundsInLocal());
        String eventDescription = event.getEventDescription();
        if(eventDescription.equals("")){
            eventDescription = "No description.";
        }
        Label popUpLabel = new Label(eventDescription);
        popUpLabel.getStyleClass().add("event-popup-label");
        VBox popUpLayout = new VBox();
        popUpLayout.setSpacing(10);
        popUpLayout.getStyleClass().add("event-popup-vbox");
        popUpLayout.getChildren().add(popUpLabel);
        eventPopUp.getContent().add(popUpLayout);
        Button deleteButton = new Button("Delete Event");
        deleteButton.getStyleClass().add("event-popup-button");
        deleteButton.setOnAction(e -> {
            dailyLog.getEvents().remove(event);
            setEvents();
            eventPopUp.hide();
        });
        popUpLayout.getChildren().add(deleteButton);
        eventPopUp.setAutoHide(true);
        if(!eventPopUp.isShowing()){
            eventPopUp.show(resource.getStage(), labelBounds.getMaxX() + 10, labelBounds.getMinY());
        }
        else{
            eventPopUp.hide();
        }
    }

    /**
     * Adds week numbers and vboxes to arrays at beginning of program for ease of access
     */
    public void setDayOfWeekList(){
        daysOfWeekLabels.add(sundayNumber);
        daysOfWeekLabels.add(mondayNumber);
        daysOfWeekLabels.add(tuesdayNumber);
        daysOfWeekLabels.add(wednesdayNumber);
        daysOfWeekLabels.add(thursdayNumber);
        daysOfWeekLabels.add(fridayNumber);
        daysOfWeekLabels.add(saturdayNumber);
        daysOfWeekVBox.add(sundayVBox);
        daysOfWeekVBox.add(mondayVBox);
        daysOfWeekVBox.add(tuesdayVBox);
        daysOfWeekVBox.add(wednesdayVBox);
        daysOfWeekVBox.add(thursdayVBox);
        daysOfWeekVBox.add(fridayVBox);
        daysOfWeekVBox.add(saturdayVBox);
    }

    /**
     * Displays habits and creates listeners for when checked/unchecked to update in journal
     */
    public void generateHabits(){
        habitVBox.getChildren().clear();
        for(String habit : resource.getJournal().getHabits()){
            GridPane habitGrid = new GridPane();
            habitGrid.setPrefSize(500, 70);
            LocalDate day = currentWeek.getFirstDayOfWeek();
            Label habitLabel = new Label(habit);
            habitLabel.setPrefSize(80, 70);
            habitLabel.setWrapText(true);
            habitGrid.add(habitLabel, 0, 0, 1, 1);
            for(int i = 1; i < 8; i++){
                CheckBox habitCheck = new CheckBox();
                LocalDate finalDay = day;
                habitCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue){
                        resource.getJournal().completedHabit(habit, finalDay);
                    }
                    else {
                        resource.getJournal().unCompleteHabit(habit, finalDay);
                    }
                });
                if(resource.getJournal().checkHabit(habit, day)){
                    habitCheck.setSelected(true);
                }
                habitCheck.getStyleClass().add("custom-checkbox");
                habitGrid.add(habitCheck, i, 0, 1, 1);
                if(resource.getJournal().checkHabit(habit, day) == true){
                    habitCheck.selectedProperty().set(true);
                }
                day = day.minusDays(-1);
            }
            habitVBox.getChildren().add(habitGrid);
        }
    }

    /**
     * Switch to Daily View when button is pressed
     */
    public void switchDailyView(){
        resource.switchScene("/journal/bulletjournaljavafx/dailyview.fxml");
    }

    /**
     * Switch to Monthly View when button is pressed
     */
    public void switchMonthlyView(){
        resource.switchScene("/journal/bulletjournaljavafx/monthlyview.fxml");
    }

    /**
     * Function to create new gridpane with proper formatting
     * @param newTask with checkbox to be used
     * @return gridpane containing checkbox
     */
    public GridPane createNewCheckBox(CheckBox newTask){
        GridPane taskLayout = new GridPane();
        ColumnConstraints taskColumn = new ColumnConstraints();
        taskColumn.setHalignment(HPos.LEFT);
        taskColumn.setHgrow(Priority.ALWAYS);
        ColumnConstraints deleteColumn = new ColumnConstraints();
        deleteColumn.setHalignment(HPos.RIGHT);
        deleteColumn.setHgrow(Priority.NEVER);
        taskLayout.getColumnConstraints().addAll(taskColumn, deleteColumn);
        taskLayout.add(newTask, 0, 0);
        Button button = new Button();
        Image trashIcon = new Image("file:src/main/resources/images/TrashIcon.png");
        ImageView image = new ImageView(trashIcon);
        image.setPreserveRatio(true);
        image.setFitWidth(30);
        button.setGraphic(image);
        button.getStyleClass().add("icon-button");
        taskLayout.add(button, 1, 0);
        return taskLayout;
    }

}