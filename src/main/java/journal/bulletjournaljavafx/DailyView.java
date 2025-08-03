package journal.bulletjournaljavafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import model.Event;
import model.Log;
import model.Task;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DailyView implements Initializable {

    @FXML
    private TextField taskTextField, habitTextField, eventTextField;

    @FXML
    private TextArea dailyEntryTextArea;

    @FXML
    private VBox habitVBox, todoVBox, eventVBox;

    @FXML
    private Label dateLabel, journalNameLabel, smallCalenderLabel;

    @FXML
    private Button weeklyViewButton, leftButton, rightButton, addEventButton;
    @FXML
    private BorderPane root;
    @FXML
    private GridPane calenderPane;

    ShareResource resource = ShareResource.getInstance();
    private LocalDate currentDay;   //keep track of what daily spread we are viewing

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentDay = LocalDate.now();
        journalNameLabel.setText(resource.getJournal().getJournalName());
        loadDay();
    }

    public void loadDay(){
        Log dailyLog = resource.getJournal().getDailyLog(currentDay);
        dailyEntryTextArea.setText(dailyLog.getEntry());
        dailyEntryTextArea.getStyleClass().add("custom-text-area");
        dailyEntryTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            dailyLog.createEntry(dailyEntryTextArea.getText());
        });
        dateLabel.setText(setDateLabel());
        displayToDos(dailyLog);
        displayHabits(dailyLog);
        displayEvents();
        drawCalender();
    }

    public void setRightButton(){
        currentDay = currentDay.minusDays(-1);
        loadDay();
    }

    public void setLeftButton(){
        currentDay = currentDay.minusDays(1);
        loadDay();
    }

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
        resource.getJournal().getDailyLog(currentDay).addTask(newTask);
        displayToDos(resource.getJournal().getDailyLog(currentDay));
        taskTextField.clear();
    }

    public void addHabit(){
        if(habitTextField.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Empty field!");
            alert.setContentText("Please enter a habit.");
            alert.show();
            return;
        }
        String newHabit = habitTextField.getText();
        resource.getJournal().addHabit(newHabit);
        displayHabits(resource.getJournal().getDailyLog(currentDay));
        habitTextField.clear();
    }

    public void displayToDos(Log dailyLog){
       todoVBox.getChildren().clear();
        for(Task task : dailyLog.getList().getTasks()){
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
                dailyLog.getList().deleteTask(task);
                todoVBox.getChildren().remove(taskLayout);
            });
            todoVBox.getChildren().add(taskLayout);
        }
    }

    public void displayHabits(Log dailyLog){
        habitVBox.getChildren().clear();
        for(String habit : resource.getJournal().getHabits()){
            CheckBox newHabit = new CheckBox(habit);
            newHabit.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    resource.getJournal().completedHabit(habit, currentDay);
                }
                else{
                    resource.getJournal().unCompleteHabit(habit, currentDay);
                }
            });
            if(resource.getJournal().checkHabit(habit, currentDay)){
                newHabit.setSelected(true);
            }
            newHabit.getStyleClass().add("custom-checkbox");
            GridPane taskLayout = createNewCheckBox(newHabit);
            Button deleteButton = (Button) taskLayout.getChildren().get(1);
            deleteButton.setOnAction(actionEvent -> {
                resource.getJournal().deleteHabit(habit);
                habitVBox.getChildren().remove(taskLayout);
            });
            habitVBox.getChildren().add(taskLayout);
        }
    }

    public void displayEvents(){
        eventVBox.getChildren().clear();
        LocalDate dayCounter = currentDay;
        for(int i = 0; i < 7; i++){
            Log dailyLog = resource.getJournal().getDailyLog(dayCounter);
            for(Event event : dailyLog.getEvents()){
                GridPane newEvent = new GridPane();
                VBox day = new VBox();
                day.setAlignment(Pos.CENTER);
                Label dayOfWeek = new Label(dayCounter.getDayOfWeek().name().substring(0,3));
                dayOfWeek.getStyleClass().add("event-week-dailyView");
                Label dayNum = new Label(String.valueOf(currentDay.getDayOfMonth()));
                dayNum.getStyleClass().add("event-num-dailyView");
                day.getChildren().add(dayOfWeek);
                day.getChildren().add(dayNum);
                newEvent.add(day, 0, 0);
                Label label = new Label(event.getEventName());
                label.getStyleClass().add("event-label-dailyView");
                GridPane.setValignment(newEvent, VPos.TOP);
                newEvent.setHgap(15);
                newEvent.add(label, 1, 0);
                eventVBox.getChildren().add(newEvent);
            }
            dayCounter = dayCounter.minusDays(-1);
        }
    }

    public void drawCalender(){
        calenderPane.getChildren().clear();
        int monthMaxDate = currentDay.getMonth().maxLength();
        if(currentDay.getYear() % 4 != 0 && monthMaxDate == 29){
            monthMaxDate = 28;
        }
        LocalDate firstDayOfMonth = LocalDate.of(currentDay.getYear(), currentDay.getMonth(),1);
        int dateOffset = firstDayOfMonth.getDayOfWeek().getValue(); //2 for tuesday
        if (dateOffset == 7){
            dateOffset = 0;
        }
        int calculatedDate = 1;
        int colIndex = 0;
        while(calculatedDate <= monthMaxDate){
            int rowIndex = dateOffset-1+calculatedDate;
            if(rowIndex >= 7){
                rowIndex = rowIndex % 7;
                if(rowIndex == 0){
                    colIndex++;
                }
            }
            Text date = new Text(String.valueOf(calculatedDate));
            calenderPane.add(date, rowIndex, colIndex);
            calculatedDate++;
        }
        String month = currentDay.getMonth().toString().toLowerCase();
        String firstLetter = month.substring(0, 1).toUpperCase();
        String restOfString = month.substring(1);
        month = firstLetter + restOfString;
        smallCalenderLabel.setText(month + " " + currentDay.getYear());
    }

    public String setDateLabel(){
        String month = currentDay.getMonth().toString().toLowerCase();
        String firstLetter = month.substring(0, 1).toUpperCase();
        String restOfString = month.substring(1);
        month = firstLetter + restOfString;
        Integer day = currentDay.getDayOfMonth();
        String dayString = null;
        if(day == 1){
            dayString = day + "rst";
        }
        else if(day == 2){
            dayString = day + "nd";
        }
        else if(day == 3){
            dayString = day + "rd";
        }
        else{
            dayString = day + "th";
        }
        return month + " " + dayString + ", " + currentDay.getYear();
    }

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

    public void switchWeeklyView(){
        resource.switchScene("/journal/bulletjournaljavafx/weeklyview.fxml");
    }

    public void switchMonthlyView(){
        resource.switchScene("/journal/bulletjournaljavafx/monthlyview.fxml");
    }


}
