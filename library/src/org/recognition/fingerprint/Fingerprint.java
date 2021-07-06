package org.recognition.fingerprint;

import org.recognition.io.AbstractReader;
import org.recognition.utils.Consts;
import org.recognition.utils.FixedSizeTreeSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.recognition.spectrogram.Spectrogram;

import java.util.*;

public class Fingerprint {
    private List<Peak> peakList;

    public Fingerprint(AbstractReader reader) {
        peakList = new ArrayList<>();

        //Get the raw time-domain data from the wav and generate its org.recognition.spectrogram
        Spectrogram spectrogram = new Spectrogram(reader.getData());
        Set<Peak> tmp = new FixedSizeTreeSet<>(Consts.NPEAKS, true);

        //for each band
        for (int b = 0; b < Consts.BANDS.length - 1; b++) {
            int currBand = Consts.BANDS[b];
            int nextBand = Consts.BANDS[b + 1];

            //for each window in the org.recognition.spectrogram
            for (int i = 0; i < spectrogram.getWinFFT().size(); i++) {

                /*
                 * Every C, or at the end of the window add tmp to peakList
                 * and reset tmp
                 */
                if (i % Consts.C == 0 || i == spectrogram.getWinFFT().size() - 1) {
                    peakList.addAll(tmp);
                    tmp.clear();
                }

                //Get the current window
                Complex[] fft = spectrogram.getWinFFT().get(i);

                //Extract between bands
                Complex[] fft_band = ArrayUtils.subarray(fft, currBand, nextBand);
                PeaksFinder find = new PeaksFinder(fft_band, currBand, i);

                tmp.addAll(find.getPeaks());
            }
        }

        //Order by time, ascending
        peakList.sort(Comparator.comparingInt(Peak::getTime));
    }

    public List<Peak> getPeakList() {
        return peakList;
    }
}