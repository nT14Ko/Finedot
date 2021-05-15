package com.dotdot.finedot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DotsGame {

    public static final int NUM_COLORS = 5;
    public static final int GRID_SIZE = 6;
    public static final int INIT_MOVES = 10;

    public enum DotStatus {Added, Rejected, Removed}

    ;

    private static DotsGame mDotsGame;

    private int mMovesLeft;
    private int mScore;
    private com.dotdot.finedot.Dot[][] mDots;
    private ArrayList<com.dotdot.finedot.Dot> mSelectedDots;

    private DotsGame() {

        mScore = 0;
        mDots = new com.dotdot.finedot.Dot[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                mDots[row][col] = new com.dotdot.finedot.Dot(row, col);
            }
        }

        mSelectedDots = new ArrayList();
    }

    public static DotsGame getInstance() {
        if (mDotsGame == null) {
            mDotsGame = new DotsGame();
        }
        return mDotsGame;
    }

    public int getMovesLeft() {
        return mMovesLeft;
    }

    public int getScore() {
        return mScore;
    }

    public com.dotdot.finedot.Dot getDot(int row, int col) {
        if (row >= GRID_SIZE || row < 0 || col >= GRID_SIZE || col < 0) {
            return null;
        } else {
            return mDots[row][col];
        }
    }

    public ArrayList<com.dotdot.finedot.Dot> getSelectedDots() {
        return mSelectedDots;
    }

    public com.dotdot.finedot.Dot getLastSelectedDot() {
        if (mSelectedDots.size() > 0) {
            return mSelectedDots.get(mSelectedDots.size() - 1);
        } else {
            return null;
        }
    }

    public ArrayList<com.dotdot.finedot.Dot> getLowestSelectedDots() {

        ArrayList<com.dotdot.finedot.Dot> dots = new ArrayList<>();
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = GRID_SIZE - 1; row >= 0; row--) {
                if (mDots[row][col].selected) {
                    dots.add(mDots[row][col]);
                    break;
                }
            }
        }

        return dots;
    }

    public void clearSelectedDots() {
        for (com.dotdot.finedot.Dot dot : mSelectedDots) {
            dot.selected = false;
        }

        mSelectedDots.clear();
    }

    //Attempt to add the dot to the list of selected dots
    public DotStatus processDot(com.dotdot.finedot.Dot dot) {
        DotStatus status = DotStatus.Rejected;

        //check if first dot selected
        if (mSelectedDots.size() == 0) {
            mSelectedDots.add(dot);
            dot.selected = true;
            status = DotStatus.Added;
        } else if (!dot.selected) {
            //make sure new is same color and adjacent to last selected dot
            com.dotdot.finedot.Dot lastDot = getLastSelectedDot();
            if (lastDot.color == dot.color && lastDot.isAdjacent(dot)) {
                mSelectedDots.add(dot);
                dot.selected = true;
                status = DotStatus.Added;
            }
        } else if (mSelectedDots.size() > 1) {
            //Dot is already selected so remove last dot if backtracking
            com.dotdot.finedot.Dot secondLast = mSelectedDots.get(mSelectedDots.size() - 2);
            if (secondLast.equals(dot)){
                com.dotdot.finedot.Dot removedDot = mSelectedDots.remove(mSelectedDots.size() - 1);
                removedDot.selected = false;
                status = DotStatus.Removed;
            }
        }

        return status;
    }

    //sort rows ascending
    private void sortSelectedDots(){
        Collections.sort(mSelectedDots, new Comparator<com.dotdot.finedot.Dot>() {
            public int compare(com.dotdot.finedot.Dot dot1, com.dotdot.finedot.Dot dot2) {
                return dot1.row - dot2.row;
            }
        });
    }

    public void finishMove(){
        if(mSelectedDots.size() > 1 ){
            //sort by row so dots are processed top-down
            sortSelectedDots();

            //Move all dots above each selected dot down by changing color
            for (com.dotdot.finedot.Dot dot : mSelectedDots){
                for(int row = dot.row; row > 0; row--) {
                    com.dotdot.finedot.Dot dotCurrent = mDots[row][dot.col];
                    com.dotdot.finedot.Dot dotAbove = mDots[row -1][dot.col];
                    dotCurrent.color = dotAbove.color;
                }

                //add a new dot at top
                com.dotdot.finedot.Dot topDot = mDots[0][dot.col];
                topDot.setRandomColor();
            }

            mScore += mSelectedDots.size();
            mMovesLeft--;

            clearSelectedDots();
        }
    }

    public void newGame(){
        mScore = 0;
        mMovesLeft = INIT_MOVES;


        for(int row = 0; row < GRID_SIZE; row++){
            for (int col = 0; col < GRID_SIZE; col++) {
                mDots[row][col].setRandomColor();
            }
        }
    }

    public boolean isGameOver(){
        return mMovesLeft == 0;
    }
}
