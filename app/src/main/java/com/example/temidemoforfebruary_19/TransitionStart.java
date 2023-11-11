package com.example.temidemoforfebruary_19;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Triple;

public class TransitionStart extends TransitionDemo {
    /**TransitionStart is another class that extends the TransitionDemo.
     * It doesn't use any Transitions, but it will be the start page
     * to the app that when you hold down on the screen, it will
     * display the transition options. If you just click on it, it
     * will perform the blink function. But if you press it too many
     * times, it will advice you to use the transition options! :)
     *                                              -Ese
     */
    //Data Member
    int numClicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shrink);
        verifyStoragePermissions(this);
        robot = Robot.getInstance(); // get an instance of the robot in order to begin using its features.

        getFrames();
        registerForContextMenu(faceFragmentFrame);

        faceFragmentFrame.setOnClickListener(v -> {
            numClicks++;
            if(face != null) {

                face.changeToLookUpAndDown();
                // interaction queue that contains wait-time, speech, and index tuples
                final Queue<Triple<Long, String, Integer>> queue = new LinkedList<>();

                // interaction model goes below
                queue.add(new Triple<>( 0L,
                        "Hi, my name is TEMI. I will be your art friend today. I love your outfit by the way. What's your name?",
                        0
                ));
                queue.add(new Triple<>(2000L,
                        "Johnny. Did I get that right?",
                        1
                ));
                queue.add(new Triple<>(1000L,
                        "Johnny is such a lovely name! Let’s make some art. What do you want to draw today?",
                        2
                ));
                // TODO: keep adding

                robot.addTtsListener(ttsRequest -> {
                    if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
                        if (!queue.isEmpty()) {
                            Triple<Long, String, Integer> cur = queue.remove();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    robot.speak(TtsRequest.create(cur.getSecond(), false));
                                }
                            }, cur.getFirst());
                        }
                    }
                });

                Triple<Long, String, Integer> first = queue.remove();
                robot.speak(TtsRequest.create(first.getSecond(), false));

            }
        });
    }

    //onCreateContextMenu() will initialize the menu options
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.transition_options, menu);
    }

    //onContextItemSelected() will send you to a different transition activity based on what you selected
    @Override
    public boolean onContextItemSelected(MenuItem menuItem){
        Intent intent;
        switch (menuItem.getItemId()){
            case R.id.transition_to_corner:
                intent = new Intent(TransitionStart.this, TransitionToUpperLeftCorner.class);
                startActivity(intent);
                break;
            case R.id.transition_slide_left_and_return:
                intent = new Intent(TransitionStart.this, TransitionSwipe.class);
                startActivity(intent);
                break;
            case R.id.transition_shrink:
                intent = new Intent(TransitionStart.this, TransitionShrink.class);
                startActivity(intent);
                break;
            case R.id.transition_eyes_to_bottom:
                intent = new Intent(TransitionStart.this, TransitionEyesToBottom.class);
                startActivity(intent);
            case R.id.transition_fade:
                intent = new Intent(TransitionStart.this, TransitionFade.class);
                startActivity(intent);
                break;
            case R.id.end_demonstrations:
                finish();
                break;
            default:
                return super.onContextItemSelected(menuItem);
        }
        return true;
    }

}
