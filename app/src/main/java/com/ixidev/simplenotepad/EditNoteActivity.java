package com.ixidev.simplenotepad;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.api.Backend;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.ixidev.simplenotepad.db.NotesDB;
import com.ixidev.simplenotepad.db.NotesDao;
import com.ixidev.simplenotepad.model.Note;

import java.io.IOException;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {
    private EditText inputNote;
    private NotesDao dao;
    private Note temp;
    public static final String NOTE_EXTRA_Key = "note_id";
    //public String res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // set theme
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt(MainActivity.THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_note);
        Toolbar toolbar = findViewById(R.id.edit_note_activity_toolbar);
        setSupportActionBar(toolbar);

        inputNote = findViewById(R.id.input_note);
        dao = NotesDB.getInstance(this).notesDao();
        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_Key, 0);
            temp = dao.getNoteById(id);
            inputNote.setText(temp.getNoteText());
        } else inputNote.setFocusable(true);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edite_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_note)
            onSaveNote();
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onSaveNote() {
        // TODO: 20/06/2018 Save Note


        String text = inputNote.getText().toString();

        if (!text.isEmpty()) {
            long date = new Date().getTime(); // get  system time
            // if  exist update els crete new
            if (temp == null) {
                temp = new Note(text, date);
                dao.insertNote(temp); // create new note and inserted to database
            } else {
                temp.setNoteText(text);
                temp.setNoteDate(date);
                dao.updateNote(temp); // change text and date and update note on database
            }

            try (LanguageServiceClient language = LanguageServiceClient.create()) {

                // The text to analyze
                Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();

                // Detects the sentiment of the text
                Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();

                //this.res =  ""+sentiment.getScore();

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage("This is an alert with no consequence");
                dlgAlert.setTitle("App Title");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
//            System.out.printf("Text: %s%n", text);
//            System.out.printf("Sentiment Score: %s", sentiment.getScore());//, sentiment.getMagnitude());
            } catch (IOException e) {
                e.printStackTrace();
                //this.res = "No res found error";
            }

            finish(); // return to the MainActivity
        }

    }
}
