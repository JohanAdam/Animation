package io.recite.audiorecognitiontest.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import io.recite.audiorecognitiontest.Constants;
import io.recite.audiorecognitiontest.model.QuranSearchResult;
import io.recite.audiorecognitiontest.R;

public class SummaryActivity extends AppCompatActivity {

    QuranSearchResult theData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            theData = b.getParcelable(Constants.DATA_SEND);
            assert theData != null;
            Log.v("v", "surahName " + theData.getVerseName() + "\nayah " + theData.getAyah());

            TextView verse_tv = (TextView) findViewById(R.id.verse_text);
            TextView ayah_tv = (TextView) findViewById(R.id.ayat_text);

            if (theData.getVerseName() == null) {
                verse_tv.setText("No match");
                ayah_tv.setText("No match");
            } else {
                verse_tv.setText(theData.getVerseName());
                ayah_tv.setText(theData.getAyah());

            }
        }
    }
}
