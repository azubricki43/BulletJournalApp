/**
 * Controller class for monthly spread
 *****
 * @author Angel Zubricki
 */
package journal.bulletjournaljavafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import model.Event;
import model.Log;
import model.MonthYear;
import model.Task;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MonthlyView implements Initializable {

    @FXML
    private GridPane calendar;

    @FXML
    private Label monthYearLabel;

    @FXML
    private Button addTask;

    @FXML
    private TextArea monthlyNotesArea;

    @FXML
    private VBox monthlyToDo;

    @FXML
    private TextField taskField;

    @FXML
    private Button createEventButton;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextArea eventDescription;

    @FXML
    private TextField eventName;

    ShareResource resource = ShareResource.getInstance();
    LocalDate currentDay = LocalDate.now();
    private MonthYear currentMonthYear = MonthYear.getCurrentMonthYear();
    private ArrayList<VBox> calendarDays;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String month = currentDay.getMonth().toString().toLowerCase();
        String firstLetter = month.substring(0, 1).toUpperCase();
        String restOfString = month.substring(1);
        month = firstLetter + restOfString;
        monthYearLabel.setText(month + " " + currentDay.getYear());
        setCalendar();
        drawEvents();
        Log monthlyLog = resource.getJournal().getMonthlyLog(currentMonthYear);
        monthlyNotesArea.setText(monthlyLog.getEntry());
        monthlyNotesArea.getStyleClass().add("custom-text-area");
        monthlyNotesArea.textProperty().addListener((obs, oldVal, newVal) -> {
            monthlyLog.createEntry(monthlyNotesArea.getText());
        });
        displayToDos(monthlyLog);
    }

    public void setCalendar() {
        calendarDays = new ArrayList<>();
        int monthMaxDate = currentDay.getMonth().maxLength();
        if (currentDay.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }
        LocalDate firstDayOfMonth = LocalDate.of(currentDay.getYear(), currentDay.getMonth(), 1);
        int dateOffset = firstDayOfMonth.getDayOfWeek().getValue(); //2 for tuesday
        if (dateOffset == 7) {
            dateOffset = 0;
        }
        int calculatedDate = 1;
        int colIndex = 0;
        while (calculatedDate <= monthMaxDate) {
            int rowIndex = dateOffset - 1 + calculatedDate;
            if (rowIndex >= 7) {
                rowIndex = rowIndex % 7;
                if (rowIndex == 0) {
                    colIndex++;
                }
            }
            Label date = new Label(String.valueOf(calculatedDate));
            VBox scrollVBox = new VBox();
            scrollVBox.setAlignment(Pos.CENTER);
            scrollVBox.setSpacing(5);
            ScrollPane scrollPane = new ScrollPane(scrollVBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            VBox mainVBox = new VBox(date, scrollPane);
            calendarDays.add(scrollVBox);
            calendar.add(mainVBox, rowIndex, colIndex);
            calculatedDate++;
        }
    }

    public void drawEvents() {
        int monthMaxDate = currentDay.getMonth().maxLength();
        if (currentDay.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }
        for (int i = 1; i <= monthMaxDate; i++) {
            LocalDate dayOfMonth = LocalDate.of(currentDay.getYear(), currentDay.getMonth(), i);
            Log dailyLog = resource.getJournal().getDailyLog(dayOfMonth);
            if (!dailyLog.getEvents().isEmpty()) {
                VBox day = calendarDays.get(dayOfMonth.getDayOfMonth() - 1);
                day.getChildren().clear();
                day.setFillWidth(true);
                for (Event event : dailyLog.getEvents()) {
                    Label eventLabel = new Label(event.getEventName());
                    eventLabel.setOnMouseClicked(e -> {
                        try {
                            createPopUp(eventLabel, event, dailyLog);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    eventLabel.setWrapText(true);
                    eventLabel.getStyleClass().add("monthly-event");
                    day.getChildren().add(eventLabel);
                }
            }
        }
    }

    public void createEvent() {
        LocalDate eventDate = eventDatePicker.getValue();
        if (eventDate == null) {
            eventDate = LocalDate.now();
        }
        Log dailyLog = resource.getJournal().getDailyLog(eventDate);
        dailyLog.createEvent(new Event(eventName.getText(), eventDescription.getText()));
        drawEvents();
        eventDatePicker.getEditor().clear();
        eventName.clear();
        eventDescription.clear();
    }

    public void addTask() {
        if (taskField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Empty field!");
            alert.setContentText("Please enter a task");
            alert.show();
            return;
        }
        String newToDo = taskField.getText();
        Task newTask = new Task(newToDo);
        resource.getJournal().getMonthlyLog(currentMonthYear).addTask(newTask);
        displayToDos(resource.getJournal().getMonthlyLog(currentMonthYear));
        taskField.clear();
    }

    public void displayToDos(Log monthlyLog) {
        monthlyToDo.getChildren().clear();
        for (Task task : monthlyLog.getList().getTasks()) {
            CheckBox newTask = new CheckBox(task.getName());
            newTask.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    task.setCompleted(true);
                } else {
                    task.setCompleted(false);
                }
            });
            if (task.isCompleted()) {
                newTask.setSelected(true);
            }
            newTask.getStyleClass().add("custom-checkbox");
            newTask.setWrapText(true);
            GridPane taskLayout = createNewCheckBox(newTask);
            Button deleteButton = (Button) taskLayout.getChildren().get(1);
            deleteButton.setOnAction(actionEvent -> {
                monthlyLog.getList().deleteTask(task);
                monthlyToDo.getChildren().remove(taskLayout);
            });
            monthlyToDo.getChildren().add(taskLayout);
        }
    }

    public GridPane createNewCheckBox(CheckBox newTask) {
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

    public void createPopUp(Label label, Event event, Log monthlyLog) throws IOException {
        Popup eventPopUp = new Popup();
        Bounds labelBounds = label.localToScreen(label.getBoundsInLocal());
        String eventDescription = event.getEventDescription();
        if (eventDescription.equals("")) {
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
            monthlyLog.getEvents().remove(event);
            drawEvents();
            eventPopUp.hide();
        });
        popUpLayout.getChildren().add(deleteButton);
        eventPopUp.setAutoHide(true);
        if (!eventPopUp.isShowing()) {
            eventPopUp.show(resource.getStage(), labelBounds.getMaxX() + 10, labelBounds.getMinY());
        } else {
            eventPopUp.hide();
        }
    }

    /**
     * Switch to Daily View when button is pressed
     */
    public void switchDailyView() {
        resource.switchScene("/journal/bulletjournaljavafx/dailyview.fxml");
    }

    /**
     * Switch to Weekly View when button is pressed
     */
    public void switchWeeklyView() {
        resource.switchScene("/journal/bulletjournaljavafx/weeklyview.fxml");
    }
}