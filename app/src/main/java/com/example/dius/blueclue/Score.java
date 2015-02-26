package com.example.dius.blueclue;

/**
 * Created by elgaby on 26/02/15.
 */
public class Score {

    private int myScore;
    private int theirScore;

    public Score() {
        myScore = 0;
        theirScore = 0;
    }

    public int getMyScore() {
        return myScore;
    }

    public int getTheirScore() {
        return theirScore;
    }

    public void increaseMyScoreBy(int points) {
        myScore += points;
    }

    public void increaseTheirScoreBy(int points) {
        theirScore += points;
    }
}
