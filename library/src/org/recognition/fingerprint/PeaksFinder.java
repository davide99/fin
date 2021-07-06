package org.recognition.fingerprint;

import org.recognition.utils.Consts;
import org.recognition.utils.FixedSizeTreeSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;

import java.util.Arrays;
import java.util.Set;

/**
 * Find local peaks in fft data.
 */

class PeaksFinder {
    private Set<Peak> peaks;

    PeaksFinder(Complex[] data, int band, int time) {
        /*
         * The peaks found has to be in ascending order by power (crescente).
         * If a 4th is added (after sorting) the left-most element (the one
         * with the lowest power) needs to be removed. See Peak.compareTo.
         */
        peaks = new FixedSizeTreeSet<>(Consts.NPEAKS, true);

        for (int i = 0; i < data.length; i++) {
            //Get current peak value
            float pi = (float) data[i].abs();
            float pl = -1, pr = -1;

            //Extract respectively 5 Complex element after and before i
            Complex[] tmpLeft = ArrayUtils.subarray(data, i - Consts.PEAKRANGE, i);
            Complex[] tmpRight = ArrayUtils.subarray(data, i + 1, i + Consts.PEAKRANGE + 1);

            //if there actually are 5 element before
            if (tmpLeft.length == Consts.PEAKRANGE)
                pl = (float) Arrays.stream(tmpLeft)
                        .max((a, b) -> Float.compare((float) a.abs(), (float) b.abs()))
                        .get().abs();

            if (tmpRight.length == Consts.PEAKRANGE)
                pr = (float) Arrays.stream(tmpRight)
                        .max((a, b) -> Float.compare((float) a.abs(), (float) b.abs()))
                        .get().abs();

            if (pi >= pl || pi >= pr) {
                //i is just the index relative to the extractWindow,
                //so let's add band offset from org.recognition.fingerprint class
                int freqIndex = i + band;
                float peakFreq = Consts.freqs[freqIndex];

                //Also need to set the time, given by the org.recognition.fingerprint class
                if ((peakFreq >= Consts.MINFREQ) && (peakFreq <= Consts.MAXFREQ))
                    peaks.add(new Peak(freqIndex, pi, time));
            }
        }
    }

    Set<Peak> getPeaks() {
        return peaks;
    }
}