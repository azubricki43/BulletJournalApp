package journal.bulletjournaljavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Journal;
import storage.JournalStorage;

import java.io.IOException;

public class Main extends Application {
    ShareResource resource = ShareResource.getInstance();

    @Override
    public void start(Stage stage) throws IOException {
        resource.setStage(stage);
        FXMLLoader loader;
        if(JournalStorage.journalExists()){
            Journal loadedJournal = JournalStorage.load();
            resource.setJournal(loadedJournal);
            loader = new FXMLLoader(Main.class.getResource("dailyview.fxml"));
        }
        else{
            loader = new FXMLLoader(Main.class.getResource("openingscreen.fxml"));
        }
        Scene scene = new Scene(loader.load(), 1024, 600);
        scene.getStylesheets().add(getClass().getResource("/css/checkbox.css").toExternalForm());
        stage.setTitle("Digital Bullet Journal");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        JournalStorage.save(ShareResource.getInstance().getJournal());
    }
}