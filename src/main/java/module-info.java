module journal.bulletjournaljavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires json.simple;


    opens journal.bulletjournaljavafx to javafx.fxml;
    exports journal.bulletjournaljavafx;
}