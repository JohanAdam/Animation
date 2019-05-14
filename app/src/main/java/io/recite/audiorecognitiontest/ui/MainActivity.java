package io.recite.audiorecognitiontest.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liulishuo.magicprogresswidget.MagicProgressCircle;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.recite.audiorecognitiontest.Constants;
import io.recite.audiorecognitiontest.MyApiEndpointInterface;
import io.recite.audiorecognitiontest.R;
import io.recite.audiorecognitiontest.customwidget.CustomToggleButton;
import io.recite.audiorecognitiontest.model.QuranSearchResult;
import io.recite.audiorecognitiontest.util.FabTransform;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 1234;
    Animation anim = null;
    Animation anim_fade_in = null;
    private TextView mText;
    private boolean isListening = false;
    private SpeechRecognizer sr;
    private static final String TAG = "MainActivity";
    private MagicProgressCircle pb;
    ConstraintLayout root_layout;
    private static final int RC_LOGIN_LIKE = 0;
    CustomToggleButton speakButton;
    LottieDrawable drawable;
    static float speed_animation_value = 2;
    public String animation_state;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root_layout = (ConstraintLayout) findViewById(R.id.main_layout);
        speakButton = (CustomToggleButton) findViewById(R.id.btn_mic);
        mText = (TextView) findViewById(R.id.status_text);
        mText.setText("Ready");
        mText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cardview_dark_background));

        pb = (MagicProgressCircle) findViewById(R.id.demo_mpc);

        drawable = new LottieDrawable();
        LottieComposition.Factory.fromAssetFileName(this, "mic_to_load.json", new
                OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(@Nullable LottieComposition composition) {
                        Log.v("v","loaded");
                        drawable.setComposition(composition);
                        drawable.setScale(0.3f);
                    }
                });
        speakButton.setImageDrawable(drawable);
        animation_state = Constants.MIC_READY;

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"onClick");
                if (!isListening){
                    Log.v(TAG,"!isListening");
                    //isListening is false, so start listen
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar-JO");
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"io.recite.audiorecognitiontest");
                    intent.putExtra(RecognizerIntent
                            .EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 100000);
                    intent.putExtra(RecognizerIntent
                            .EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 100000);
                    // value to wait
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);  // 1 is the maximum number of results to be returned.
                    sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    sr.setRecognitionListener(new listener());
                    sr.startListening(intent);
                    startButton();
//                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

                } else {
                    Log.v(TAG,"isListening");
                    //isListening is true, cancel listen
                    sr.stopListening();
                    stopListen();
//                    startButton();
                }

            }
        });
    }

    private void openResult(QuranSearchResult body){
        final Intent login = new Intent(MainActivity.this, DribbbleLogin.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            FabTransform.addExtras(login, ContextCompat.getColor(MainActivity.this, R
                    .color.dribbble), R.drawable.ic_microphone_black_shape);
            ActivityOptions options = null;
            options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                    speakButton, getString(R.string.transition_dribbble_login));
            Bundle b = new Bundle();
            b.putParcelable(Constants.DATA_SEND, body);
            b.putAll(options.toBundle());
            startActivityForResult(login, RC_LOGIN_LIKE, b);

        } else {
            startActivityForResult(login, RC_LOGIN_LIKE);
        }
    }

    private void stopListen(){
        isListening = false;
        stop_animation();
    }


    private class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params){
            Log.v(TAG,"onReadyForSpeech");
            isListening = true;
            mText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cardview_dark_background));
            mText.setText("Start Listening");
            start_animation();
        }
        public void onBeginningOfSpeech(){
            Log.v(TAG,"onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB){}
        public void onBufferReceived(byte[] buffer) {
            Log.e(TAG,"onBufferReceived " + buffer);
        }
        public void onEndOfSpeech(){
            Log.v(TAG,"OnEndOfSpeech");
            mText.setText("Stop Listening");
            stopListen();
//            errorButton();
        }
        public void onError(int error)
        {
            Log.e(TAG,"error " + error);
            stopListen();
            if (drawable.isAnimating() && drawable.isLooping() && animation_state.equals(Constants
                    .LOADING)){
                Log.e(TAG,"drawable is animaton && drawable is looping && animation state " +
                        animation_state);
                errorButton();
            } else if (drawable.isAnimating() && animation_state.equals(Constants.MIC_READY)) {
                Log.e(TAG,"drawable is animating && animate state " + animation_state);
                errorButton();
            }
            animation_state = Constants.STOPPED;
            mText.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_dark));
            if (error == SpeechRecognizer.ERROR_CLIENT) {
                mText.setText("Done");
                mText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.cardview_dark_background));
            } else if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                mText.setText("No Match");
            } else if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                mText.setText("Speech Timeout");
            } else if (error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) {
                mText.setText("Network Error");
            } else if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                mText.setText("Please enable permission first!");
            } else if (error == SpeechRecognizer.ERROR_SERVER) {
                mText.setText("Server Error :(");
            } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                Log.e("e","busy, restart");
                mText.setText("Restarting..");
                if(sr!=null){
                    sr.stopListening();
                    sr.cancel();
                    sr.destroy();
                }
                sr = null;
                mText.setText("Done");
            } else {
                mText.setText("An error has occurred " + error);
            }
        }
        public void onResults(Bundle results)
        {
            stopListen();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            //  mText.setText("results: "+str+" "+String.valueOf(data.size()));
            mText.setText("Analyzing...");

            sendData(data.get(0).toString());

        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    private void loadingButton(){
        animation_state = Constants.LOADING;

        drawable.setScale(0.3f);
        drawable.setMinProgress(0.5f);
        drawable.setMaxProgress(1f);
        drawable.setSpeed(speed_animation_value);
        drawable.loop(true);
        drawable.playAnimation();
        drawable.removeAnimatorListener(new anim_listener());
    }

    private void errorButton() {
        Log.v("v","errorButton");
        animation_state = Constants.STOPPED;
        drawable.setMinAndMaxProgress(0, 0.5f);
        drawable.loop(false);
        drawable.setSpeed(-speed_animation_value);
        drawable.playAnimation();
        drawable.removeAnimatorListener(new anim_listener());
        //back to mic

    }

    private void startButton() {
        Log.v("v","startButton");
        animation_state = Constants.MIC_READY;

        drawable.setMinProgress(0f);
        drawable.setMaxProgress(0.5f);
        drawable.loop(false);
        drawable.playAnimation();
        drawable.setSpeed(speed_animation_value);
        drawable.addAnimatorListener(new anim_listener());

    }

    private void stop_animation(){
        Log.v("v","stop animation");
        pb.clearAnimation();
        if  (anim != null) {
            Log.v("v","anim not null");
            anim.reset();
            anim.cancel();
        }

        if (anim_fade_in != null) {
            Log.v("v","anim_fade_in not null");
            anim_fade_in.reset();
            anim_fade_in.cancel();
        }
    }

    private void start_animation() {

        if (isListening){
            //play
            anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
            anim.setDuration(800);
            anim.setFillAfter(true);

            pb.startAnimation(anim);

            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    if (isListening){
                        //play
                        anim_fade_in = AnimationUtils.loadAnimation(MainActivity.this, R.anim
                                .fade_in);
                        anim_fade_in.setDuration(800);
                        anim_fade_in.setFillAfter(true);

                        pb.startAnimation(anim_fade_in);

                        anim_fade_in.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                pb.startAnimation(anim);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        Log.e("e","isListening false");
                        //stop
                        pb.clearAnimation();
                        pb.setAlpha(1f);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            Log.e("e","isListening false");
            //stop
            pb.clearAnimation();
            pb.setAlpha(1f);
        }

    }

    public static String stripAccents(String s)
    {
        Log.e(TAG,"get " + s);
        s = Normalizer.normalize(s, Normalizer.Form.NFKD);
//        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
//        s = s.replaceAll("[^\\p{ASCII}]", "");
        s = s.replaceAll("\\p{M}", "");
        Log.e(TAG,"return " + s);
        return s;
    }

    //reciteweb
    public static final String BASE_URL = "https://reciteweb.azurewebsites.net/";
    private void sendData(String input){
        Log.v(TAG,"send Data " + input);

        //Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create(getGsonconverter()))
                .build();

        MyApiEndpointInterface service = retrofit.create(MyApiEndpointInterface.class);

        Call<QuranSearchResult> searchCall = service.searchayat("", "2.0.0", input);

        searchCall.enqueue(new Callback<QuranSearchResult>() {
            @Override
            public void onResponse(Call<QuranSearchResult> call, Response<QuranSearchResult> response) {
                Log.v(TAG,"onResponse");
                if (response.isSuccessful()){
                    Log.v(TAG,"onResponse isSuccessfull");

//                    errorButton();

                    Log.v(TAG," response.body " + response.body());
                    mText.setText("Done");
                    errorButton();

                    if (response.body().getVerseName() == null) {
                        mText.setText("No Match");
                    } else {
//                        Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
//                        Bundle b = new Bundle();
//                        b.putParcelable(Constants.DATA_SEND, response.body());
//                        intent.putExtras(b);
//                        startActivity(intent);
                        openResult(response.body());
                    }

                } else {
                    Log.v(TAG,"onResponse unSuccessfull");
                    mText.setText("Unsuccessful response");
                }
            }

            @Override
            public void onFailure(Call<QuranSearchResult> call, Throwable t) {
                Log.v(TAG,"onFailure");
                errorButton();
                t.printStackTrace();
                mText.setText("Failed to get response. Check log.");
            }
        });

    }

    //initialized gson
    private Gson getGsonconverter() {
        //gson initialized
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return gson;
    }

    //getHttpClient timeout setup
    private OkHttpClient getHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        return client;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        if (sr != null){
            sr.stopListening();
            stopListen();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"destroy sr");
        if (sr != null) {
            sr.destroy();

        }
    }

    private class anim_listener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Log.v(TAG,"onAnimationEnd " + animation_state
                    + "\n isListening " + isListening);
            if (animation_state.equals(Constants.MIC_READY)){
                if (isListening){
                    loadingButton();
                } else {
                    errorButton();
                }
            }

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}