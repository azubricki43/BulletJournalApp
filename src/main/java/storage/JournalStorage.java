package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import journal.bulletjournaljavafx.ShareResource;
import model.*;

import java.io.*;
import java.time.LocalDate;

public class JournalStorage {
    private static final String FILE_PATH = System.getProperty("user.home") + "/BulletJournal/journal.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(MonthYear.class, new MonthYearAdapter())
            .registerTypeAdapter(WeekYear.class, new WeekYearAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting().setPrettyPrinting().create();

    static ShareResource resource = ShareResource.getInstance();

    public static void save(Journal journal) {
        try {
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs(); // Ensure directory exists
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(journal, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Journal load() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try (Reader reader = new FileReader(file)) {
            Journal journal = gson.fromJson(reader, Journal.class);
            return journal;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean journalExists() {
        File file = new File(FILE_PATH);
        return file.exists() && file.length() > 0;
    }

    public static void main(String[] args) throws IOException {
        resource.createJournal();
        resource.sampleJournal();
        Journal newJournal = resource.getJournal();
        newJournal.setJournalName("angel");
        JournalStorage.save(newJournal);
    }
}
