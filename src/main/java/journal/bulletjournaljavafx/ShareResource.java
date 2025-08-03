/**
 * Class to implement Singleton design pattern to access journal data across all controllers
 * @author Angel Zubricki
 */
package journal.bulletjournaljavafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public final class ShareResource {
    private static ShareResource resource;

    private Journal journal;
    private Stage stage;


    /**
     * Empty private constructor so Java doesn't generate a public constructor
     */
    private ShareResource(){}

    public static synchronized ShareResource getInstance(){
        if(resource == null){
            resource = new ShareResource();
        }
        return resource;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public Stage getStage(){
        return stage;
    }

    public void switchScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/checkbox.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createJournal(){
        this.journal = new Journal();
    }

    public Journal getJournal(){
        return this.journal;
    }

    public void setJournal(Journal journal){
        this.journal = journal;
    }

    public void sampleJournal(){
        journal.addHabit("Exercise");
        journal.addHabit("Read 20 mins");
        journal.addHabit("No spend");;
        Log dailyLog = getJournal().getDailyLog(LocalDate.now());
        dailyLog.addTask(new Task("Do the dishes"));
        dailyLog.addTask(new Task("Grocery Shopping"));
        dailyLog.createEntry("Today was a great day!");
    }

}
