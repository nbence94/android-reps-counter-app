package nbey.bens.counterproject;

import android.util.Log;

public class ConvertTime {

    public String convertSecs(int value) {
        int minutes = value / 60;
        int seconds = value % 60;
        String zero = (minutes < 10) ? "0" : "";
        return zero + minutes + ":" + seconds;
    }

    public String getMinutes(int value) {
        int minutes = value / 60;
        String zero = (minutes < 10) ? "0" : "";
        return zero + minutes;
    }

    public String getSeconds(int value) {
        int seconds = value % 60;
        String zero = (seconds < 10) ? "0" : "";
        return zero + seconds;
    }

    public int sumSeconds(float step, int reps, int rounds, int pause) {
        int summary = 0;
        summary += step * 2 * reps * rounds;
        summary += (rounds - 1) * pause;
        return summary;
    }

}
