package io.recite.audiorecognitiontest.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.recite.audiorecognitiontest.Constants;
import io.recite.audiorecognitiontest.R;
import io.recite.audiorecognitiontest.model.QuranSearchResult;
import io.recite.audiorecognitiontest.util.FabTransform;
import io.recite.audiorecognitiontest.util.MorphTransform;

public class DribbbleLogin extends AppCompatActivity {

    boolean isDismissing = false;
    ViewGroup container;
    QuranSearchResult theData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dribbble_login);
        Log.v("v","onCreate DribbleLogin");

        container = (ViewGroup) findViewById(R.id.container);

        if (!FabTransform.setup(this, container)) {
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dialog_corners));
        }

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
        //        Log.v("v","set that");
//
//        final AnimatedVectorDrawableCompat mic_to_load_anim = AnimatedVectorDrawableCompat.create(this,
//                R.drawable.loading);
//        mIcDownloadAnimator.setImageDrawable(mic_to_load_anim);
////        final Handler mainHandler = new Handler(Looper.getMainLooper());
//        if (mic_to_load_anim != null) {
//            mic_to_load_anim.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
//                @Override
//                public void onAnimationEnd(final Drawable drawable) {
//                    Log.v("v","animation End");
//
//                    mic_to_load_anim.start();
//                }
//            });
//        }
//        mic_to_load_anim.start();

    }

    public void dismiss(View view) {
        isDismissing = true;
        setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }

}
