
// Imports the Google Cloud client library
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

import java.io.IOException;

public class Backend {

    String res;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Backend(String text){
        try (LanguageServiceClient language = LanguageServiceClient.create()) {

            // The text to analyze
            Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();

            // Detects the sentiment of the text
            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
            this.res =  ""+sentiment.getScore();
//            System.out.printf("Text: %s%n", text);
//            System.out.printf("Sentiment Score: %s", sentiment.getScore());//, sentiment.getMagnitude());
        } catch (IOException e) {
            e.printStackTrace();
            this.res = "No res found error";
        }
    }

    public String getRes() {
        return res;
    }
}