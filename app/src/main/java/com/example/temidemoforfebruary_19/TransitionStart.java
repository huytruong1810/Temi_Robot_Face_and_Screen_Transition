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

    enum DRAW {
        NA, WAITING, UNICORN, UNICORN_AND_FACE, UNICORN_FLOWER, UNICORN_LOLLIPOPS, UNICORN_SANTA
    }
    static class TemiOutput {
        public long waitTime;
        public String speech;
        public boolean listening;
        public DRAW drawMode;
        public TemiOutput(long waitTime, String speech, boolean listening, DRAW drawMode) {
            this.waitTime = waitTime;
            this.speech = speech;
            this.listening = listening;
            this.drawMode = drawMode;
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
//        queue.add(new TemiOutput(0L,
//                "Hi, my name is TEMI.",
//                false, DRAW.NA
//        ));
//        queue.add(new TemiOutput(300L,
//                "I will be your art friend today.",
//                false, DRAW.NA
//        ));
//        queue.add(new TemiOutput(300L,
//                "I love your outfit by the way.",
//                false, DRAW.NA
//        ));
//        queue.add(new TemiOutput(300L,
//                "What's your name?",
//                true, DRAW.NA
//        ));
//        // Johnny: *Tells the name* Johnny.
//        queue.add(new TemiOutput(2000L,
//                "Johnny.",
//                false, DRAW.NA
//        ));
//        queue.add(new TemiOutput(300L,
//                "Did I get that right?",
//                true, DRAW.NA
//        ));
//        // Johnny: yes!
//        queue.add(new TemiOutput(2000L,
//                "Johnny is such a lovely name!",
//                false, DRAW.NA
//        ));
//        queue.add(new TemiOutput(300L,
//                "Let’s make some art!",
//                false, DRAW.NA
//        ));
        queue.add(new TemiOutput(300L,
                "What do you want to draw?",
                true, DRAW.NA
        ));
        // Johnny: I want to draw a unicorn!
        queue.add(new TemiOutput(2000L,
                "Okay, give me a minute.",
                false, DRAW.WAITING
        ));
        queue.add(new TemiOutput(3000L,
                "Here you go!",
                false, DRAW.UNICORN
        ));
        queue.add(new TemiOutput(3000L,
                "What do you think?",
                false, DRAW.UNICORN_AND_FACE
        ));
        // Johnny: It looks good. I want the unicorn to stand in the field of flowers.
        queue.add(new TemiOutput(3000L,
                "Oh thats a wonderful idea!",
                false, DRAW.WAITING
        ));
        queue.add(new TemiOutput(3000L,
                "How does this look?",
                false, DRAW.UNICORN_FLOWER
        ));
        // Johnny: Awesome!
        queue.add(new TemiOutput(2000L,
                "Do you want to see something even cooler??",
                false, DRAW.UNICORN_LOLLIPOPS
        ));
        // Johnny: *giggles* thats pretty cool!!
        queue.add(new TemiOutput(2000L,
                "Do you want me to change it back to a field of flowers?",
                false, DRAW.UNICORN_LOLLIPOPS
        ));
        // Johnny: No, lollipops are better.
        queue.add(new TemiOutput(2000L,
                "What if we have a unicorn Santa?",
                false, DRAW.WAITING
        ));
        queue.add(new TemiOutput(3000L,
                "Wouldn’t that be fun!",
                false, DRAW.UNICORN_SANTA
        ));


    }

    private void insertTellJoke(final Queue<TemiOutput> queue) {
        queue.add(new TemiOutput(0L,
                "Johnny, do you like jokes?",
                true, DRAW.NA
        ));
        // Johnny: yes!
        queue.add(new TemiOutput(2000L,
                "Why can’t you tell an egg a joke?",
                false, DRAW.NA
        ));
        queue.add(new TemiOutput(300L,
                "It’ll crack up!",
                false, DRAW.NA
        ));
    }

    private void insertAskRandomQuestion(final Queue<TemiOutput> queue) {
        // Johnny: Hey Temi, whats your favourite color?
        queue.add(new TemiOutput(0L,
                "I don’t have one favourite color, I love all the colors.",
                false, DRAW.NA
        ));
        queue.add(new TemiOutput(300L,
                "What’s your favourite color, Johnny?",
                true, DRAW.NA
        ));
        // Johnny: Pink and purple!
        queue.add(new TemiOutput(2000L,
                "Lovely!",
                false, DRAW.NA
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
                insertGreeting(queue);
//                insertTellJoke(queue);
//                insertAskRandomQuestion(queue);
//                insertArtRevision(queue);

                robot.addTtsListener(ttsRequest -> {
                    if (ttsRequest.getStatus() == TtsRequest.Status.COMPLETED) {
                        if (!queue.isEmpty()) {
                            TemiOutput temiOutput = queue.remove();
                            if (temiOutput.listening) face.setFace(Mood.LISTENING); // face making
                            else face.setFace(Mood.HAPPY);
                            switch (temiOutput.drawMode) {
                                case WAITING:
                                    face.setFace(Mood.DRAWING);
                                    break;
                                case UNICORN:
                                    startActivity(new Intent(TransitionStart.this, TransitionUnicorn.class));
                                    break;
//                                case UNICORN_AND_FACE:
//                                    startActivity(new Intent(TransitionStart.this, TransitionToUpperLeftCorner.class));
//                                    break;
                                case NA:
                                    break;
                            }
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
