package com.NHS.UCLTeam9.WellWellWell;

/**
 * Created by Zuka on 3/14/19.
 */

public class AnonymousData {
    String postcode;
    int score;
    int errorRate;

    public AnonymousData(String postcode, int score, int errorRate) {
        this.postcode = postcode;
        this.score = randomize(score);
        this.errorRate = errorRate;
    }

    public int randomize(int score){
        int answer = score;
        double randomNumber = Math.random() * 100;
        if(randomNumber > 70){
            double randscore = Math.random() * 11 * 10000;
            answer = (int)(randscore);
            answer /= 10000;
        }
        return answer;
    }
}
