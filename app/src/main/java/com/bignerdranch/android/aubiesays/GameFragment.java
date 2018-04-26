package com.bignerdranch.android.aubiesays;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Lawrence-S on 4/21/2018.
 */

public class GameFragment extends Fragment {

    private static final int[] BUTTON_IDS = {R.id.blue, R.id.red, R.id.green, R.id.yellow};
    private static final double ACTIVATE_PERCENT = 0.8;
    private static final String TAG = "GameFragment";

    private Sequence mSequence;
    private int mCurrentIndex;
    private Handler mHandler;
    private ScoreListener mCallback;
    private final MediaPlayer mBlueSound = new MediaPlayer();
    private final MediaPlayer mRedSound = new MediaPlayer();
    private final MediaPlayer mGreenSound = new MediaPlayer();
    private final MediaPlayer mYellowSound = new MediaPlayer();

    public interface ScoreListener {
        public void onShowScore(int score);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // restore variables from Bundle
        }
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mSequence == null) {
            mSequence = new Sequence(BUTTON_IDS);
            mCurrentIndex = 0;
        }
        return inflater.inflate(R.layout.game, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (ScoreListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ScoreListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        for (int buttonId : BUTTON_IDS) {
            try {
                setSoundPlayer(buttonId);
            }
            catch (IOException e) {
                Log.d(TAG, "Error reading sound file");
            }
        }
        playSequence();
    }

    @Override
    public void onStop() {
        super.onStop();
        for (int i : BUTTON_IDS) {
            Button b = getView().findViewById((int) i);
            b.getHandler().removeCallbacksAndMessages(null);
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void setSoundPlayer(int buttonId) throws IOException {
        AssetFileDescriptor soundFile;

        switch (buttonId) {
            case R.id.blue:
                soundFile = getContext().getAssets().openFd("E4.wav");
                mBlueSound.setDataSource(soundFile.getFileDescriptor(), soundFile.getStartOffset(), soundFile.getDeclaredLength());
                break;
            case R.id.red:
                soundFile = getContext().getAssets().openFd("A4.wav");
                mRedSound.setDataSource(soundFile.getFileDescriptor(), soundFile.getStartOffset(), soundFile.getDeclaredLength());
                break;
            case R.id.green:
                soundFile = getContext().getAssets().openFd("E3.wav");
                mGreenSound.setDataSource(soundFile.getFileDescriptor(), soundFile.getStartOffset(), soundFile.getDeclaredLength());
                break;
            case R.id.yellow:
                soundFile = getContext().getAssets().openFd("C4#.wav");
                mYellowSound.setDataSource(soundFile.getFileDescriptor(), soundFile.getStartOffset(), soundFile.getDeclaredLength());
                break;
            default:
                soundFile = getContext().getAssets().openFd("C4#.wav");
                break;
        }
    }

    public void activateButton(int buttonId) throws IOException {
        /* TODO implement this
         * We need to flash the button/show aubie on the button and
         * play the sound for a period shorter than the difficulty delay
         */

        /* TODO change later
         * for now just making activated button black
         */

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int speed = Integer.parseInt(sharedPrefs.getString("prefDifficulty", "NULL"));
        final Button b;
        final Drawable d;

        switch (buttonId) {
            case R.id.blue:
                mBlueSound.prepare();
                mBlueSound.setLooping(true);
                mBlueSound.start();

                b = (Button) getView().findViewById(buttonId);
                d = b.getBackground();
                b.setBackground(getResources().getDrawable(R.drawable.upper_right_button_active, getContext().getTheme()));
                b.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setBackground(d);
                        mBlueSound.stop();
                    }
                }, (long) (speed * ACTIVATE_PERCENT));
                break;
            case R.id.red:
                mRedSound.prepare();
                mRedSound.setLooping(true);
                mRedSound.start();

                b = (Button) getView().findViewById(buttonId);
                d = b.getBackground();
                b.setBackground(getResources().getDrawable(R.drawable.upper_left_button_active, getContext().getTheme()));
                b.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setBackground(d);
                        mRedSound.stop();
                    }
                }, (long) (speed * ACTIVATE_PERCENT));
                break;
            case R.id.green:
                mGreenSound.prepare();
                mGreenSound.setLooping(true);
                mGreenSound.start();

                b = (Button) getView().findViewById(buttonId);
                d = b.getBackground();
                b.setBackground(getResources().getDrawable(R.drawable.lower_left_button_active, getContext().getTheme()));
                b.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setBackground(d);
                        mGreenSound.stop();
                    }
                }, (long) (speed * ACTIVATE_PERCENT));
                break;
            case R.id.yellow:
                mYellowSound.prepare();
                mYellowSound.setLooping(true);
                mYellowSound.start();

                b = (Button) getView().findViewById(buttonId);
                d = b.getBackground();
                b.setBackground(getResources().getDrawable(R.drawable.lower_right_button_active, getContext().getTheme()));
                b.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        b.setBackground(d);
                        mYellowSound.stop();
                    }
                }, (long) (speed * ACTIVATE_PERCENT));
                break;
            default:
                break;
        }
    }

    public void disableButtons() {
        /*
         * We need to set the OnClickListener for each button
         * to be null.
         */
        for (int i : BUTTON_IDS) {
            ((Button) getView().findViewById(i)).setEnabled(false);
        }
    }

    public void enableButtons() {
        /* TODO implement this
         * Each OnClickListener needs to verify if the button is correct and
         * then increment the current index. Finally, if the pressed button
         * is the last one in the sequence, we need to show the user that
         * they entered a sequence correctly, extend the sequence, then play
         * the sequence.
         */

        Button.OnClickListener buttonListener = new Button.OnClickListener(){
            public void onClick(View v) {
                //Log.d(TAG, "View ID: " + v.getId());
                //Log.d(TAG, "Current Index: " + mCurrentIndex);
                //Log.d(TAG, "Verify: " + mSequence.verify(v.getId(), mCurrentIndex));

                if (mSequence.verify(v.getId(), mCurrentIndex)) {
                    mCurrentIndex++;
                    if (mCurrentIndex == mSequence.getSize()) {

                         /* try {
                            activateButton(v.getId());
                        } catch (IOException e) {
                            Log.d(TAG, "Id could not be read from file");
                        } */

                        mCurrentIndex = 0;
                        mSequence.extend();
                        playSequence();
                    }
                }
                else {
                    mCallback.onShowScore(mSequence.getSize() - 1);
                }

            }
        };

        for (int i : BUTTON_IDS) {
            //Log.d(TAG, "Button ID: " + i);
            ((Button) getView().findViewById(i)).setOnClickListener(buttonListener);
            ((Button) getView().findViewById(i)).setEnabled(true);
        }
    }

    public void playSequence() {
        disableButtons();
        ArrayList<Integer> buttons = mSequence.getSequence();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int speed = Integer.parseInt(sharedPrefs.getString("prefDifficulty", "800"));


        int index = 0;
        for (Integer i : buttons) {
            Button b = getView().findViewById((int) i);
            final int id = i;
            final int sequenceIndex = index;
            b.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Showing button " + sequenceIndex + " in sequence.");
                    try {
                        activateButton(id);
                    }
                    catch (IOException e) {
                        Log.d(TAG, "Error reading sound file");
                    }
                }
            }, index * speed);

            index++;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Enabling buttons now");
                enableButtons();
            }
        }, index * speed);

    }
}
