package journal.bulletjournaljavafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenScreen implements Initializable {
    @FXML
    private Button journalButton;

    @FXML
    private TextField nameTextArea;

    ShareResource resource = ShareResource.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        journalButton.setOnAction(e -> startProgram());
    }

    public void startProgram(){
        if(nameTextArea.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Name not entered");
            alert.setContentText("Please enter your name!");
            alert.show();
            return;
        }
        resource.createJournal();
        resource.getJournal().setJournalName(nameTextArea.getText());
        resource.sampleJournal();
        dailyView();
    }

    public void dailyView(){
        resource.switchScene("/journal/bulletjournaljavafx/dailyview.fxml");
    }
}
