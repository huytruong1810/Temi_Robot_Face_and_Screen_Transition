package com.example.temidemoforfebruary_19;

import static com.example.temidemoforfebruary_19.Mood.HAPPY;

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
    
    static class TemiOutput {
        public long waitTime;
        public String speech;
        public boolean listening;
        public boolean showImage;
        public TemiOutput(long waitTime, String speech, boolean listening, boolean showImage) {
            this.waitTime = waitTime;
            this.speech = speech;
            this.listening = listening;
            this.showImage = showImage;
        }
    }
    
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

    private void insertGreeting(final Queue<TemiOutput> queue) {
        // *Child enters the room*
        // *Temi reacts by acknowledging the child by looking up and down and greeting”
        queue.add(new TemiOutput(0L,
                "Hi, my name is TEMI.",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "I will be your art friend today.",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "I love your outfit by the way.",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "What's your name?",
                true, false
        ));
        // Johnny: *Tells the name* Johnny.
        queue.add(new TemiOutput(2000L,
                "Johnny.",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "Did I get that right?",
                true, false
        ));
        // Johnny: yes!
        queue.add(new TemiOutput(2000L,
                "Johnny is such a lovely name!",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "Let’s make some art.",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "What do you want to draw today?",
                true, false
        ));
        queue.add(new TemiOutput(2000L,
                "Yup!",
                false, false
        ));
    }

    private void insertTellJoke(final Queue<TemiOutput> queue) {
        queue.add(new TemiOutput(0L,
                "Johnny, do you like jokes?",
                false, false
        ));
        // Johnny: yes!
        queue.add(new TemiOutput(2000L,
                "Why can’t you tell an egg a joke?",
                true, false
        ));
        queue.add(new TemiOutput(300L,
                "It’ll crack up!",
                false, false
        ));
    }

    private void insertAskRandomQuestion(final Queue<TemiOutput> queue) {
        // Johnny: Hey Temi, whats your favourite color?
        queue.add(new TemiOutput(0L,
                "I don’t have one favourite color, I love all the colors.",
                false, false
        ));
        queue.add(new TemiOutput(300L,
                "What’s your favourite color, Johnny?",
                false, false
        ));
        // Johnny: Pink and purple!
        queue.add(new TemiOutput(2000L,
                "Lovely!",
                true, false
        ));
    }

    private void insertArtRevision(final Queue<TemiOutput> queue) {
        // *Temi draws an image of a butterfly*
        // Johnny: No, I want to draw flowers!
        queue.add(new TemiOutput(0L,
                "Okay! I will help you draw flowers.",
                false, true
        ));
        // *Temi draws an image of flowers”
        queue.add(new TemiOutput(2000L,
                "What do you think?",
                true, false
        ));
    }

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

                face.lookUpAndDown();
                // interaction queue that contains wait-time, speech, and index tuples
                final Queue<TemiOutput> queue = new LinkedList<>();

                // interaction model goes below
//                insertGreeting(queue);
//                insertTellJoke(queue);
//                insertAskRandomQuestion(queue);
                insertArtRevision(queue);

                robot.addTtsListener(ttsRequest -> {
                    if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
                        if (!queue.isEmpty()) {
                            TemiOutput temiOutput = queue.remove();
                            if (temiOutput.listening) face.listening(); // face making
                            else face.setFace(HAPPY);
                            if (temiOutput.showImage) face.setDrawnImage();
                            try { // waiting
                                Thread.sleep(temiOutput.waitTime);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            robot.speak(TtsRequest.create(temiOutput.speech, false)); // speaking
                        }
                    }
                });

                TemiOutput first = queue.remove();
                robot.speak(TtsRequest.create(first.speech, false));

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
