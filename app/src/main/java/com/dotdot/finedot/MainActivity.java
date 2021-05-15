package com.dotdot.finedot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private com.dotdot.finedot.SoundEffects mSoundEffects;
    private DotsGame mGame;
    private com.dotdot.finedot.DotsGrid mDotsGrid;
    private TextView mMovesRemaining;
    private TextView mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovesRemaining = findViewById(R.id.movesRemaining);
        mScore = findViewById(R.id.score);
        mDotsGrid = findViewById(R.id.gameGrid);
        mDotsGrid.setGridListener(mGridListener);
        mGame = DotsGame.getInstance();
        mSoundEffects = com.dotdot.finedot.SoundEffects.getInstance(getApplicationContext());
        startNewGame();
    }

    private com.dotdot.finedot.DotsGrid.DotsGridListener mGridListener = new com.dotdot.finedot.DotsGrid.DotsGridListener() {
        @Override
        public void onDotSelected(com.dotdot.finedot.Dot dot, com.dotdot.finedot.DotsGrid.DotSelectionStatus selectionStatus) {
            //ignore selections when game is over
            if(mGame.isGameOver()) return;

            if(selectionStatus == com.dotdot.finedot.DotsGrid.DotSelectionStatus.First){
                mSoundEffects.resetTones();
            }


            //add/remove dot to/from selected dots
            DotsGame.DotStatus addStatus = mGame.processDot(dot);
            if (addStatus == DotsGame.DotStatus.Added){
                mSoundEffects.playTone(true);
            }
            else if ( addStatus == DotsGame.DotStatus.Added){
                mSoundEffects.playTone(false);
            }

            //if done selecting dots then replace selected dots and siplay new moves and score
            if (selectionStatus == com.dotdot.finedot.DotsGrid.DotSelectionStatus.Last){
                if(mGame.getSelectedDots().size() > 1 ){
                    mDotsGrid.animateDots();
                    //mGame.finishMove();
                    //updateMovesAndScore();
                }
                else {
                    mGame.clearSelectedDots();
                }
            }
            //display changes to the game
            mDotsGrid.invalidate();
        }

        @Override
        public void onAnimationFinished() {

            mGame.finishMove();
            mDotsGrid.invalidate();
            updateMovesAndScore();
            if (mGame.isGameOver()){
                mSoundEffects.playGameOver();
            }
        }
    };

    public void newGameClick(View view) {
        //Animate down off screen
        ObjectAnimator moveBoardOff = ObjectAnimator.ofFloat(mDotsGrid, "translationY", mDotsGrid.getHeight() * 1.5f);
        moveBoardOff.setDuration(700);
        moveBoardOff.start();

        moveBoardOff.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startNewGame();
                ObjectAnimator moveBoardOn = ObjectAnimator.ofFloat(mDotsGrid,
                        "translationY", mDotsGrid.getHeight() * -1.5f, 0);
                moveBoardOn.setDuration(700);
                moveBoardOn.start();
            }
        });
    }

    private void startNewGame(){



        mGame.newGame();
        mDotsGrid.invalidate();
        updateMovesAndScore();
    }

    private void updateMovesAndScore(){
        mMovesRemaining.setText(Integer.toString(mGame.getMovesLeft()));
        mScore.setText(Integer.toString(mGame.getScore()));
    }
}