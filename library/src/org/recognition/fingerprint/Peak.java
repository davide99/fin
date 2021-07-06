package org.recognition.fingerprint;

import org.recognition.utils.Consts;

public class Peak implements Comparable<Peak> {
    private int freq;
    private float power;
    private int time;

    Peak(int freq, float power, int time) {
        this.freq = freq;
        this.power = power;
        this.time = time;
    }

    int getFreq() {
        return freq;
    }

    int getTime() {
        return time;
    }

    private int getBand() {
        try {
            for (int i = 0; i < Consts.BANDS.length - 1; i++)
                if (Consts.BANDS[i + 1] > freq)
                    return i;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        return -1;
    }

    boolean compareBand(Peak p) {
        int b1 = this.getBand();
        int b2 = p.getBand();

        return (b1 == b2) && (b1 != -1);
    }

    @Override //Ascending
    public int compareTo(Peak o) {
        return Float.compare(this.power, o.power);
    }
}