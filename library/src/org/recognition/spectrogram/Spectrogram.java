package org.recognition.spectrogram;

import org.recognition.utils.Consts;
import org.recognition.utils.Helper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.List;

public class Spectrogram {
    private final List<Complex[]> winFFT;

    public Spectrogram(float[] data) {
        winFFT = new ArrayList<>();
        int stepSize = Consts.WIN_SIZE - Consts.OVERLAP;
        Consts.freqs = new float[Consts.WIN_SIZE / 2];
        Consts.times = new float[data.length / stepSize];

        Window window = new Window();

        for (int i = 0; i + Consts.WIN_SIZE < data.length; i += stepSize) {
            double[] win = Helper.floatArrayToDouble(window.extractWindow(data, i));

            FastFourierTransformer f = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] out = f.transform(win, TransformType.FORWARD);
            out = ArrayUtils.subarray(out, 0, out.length / 2);

            winFFT.add(out);
        }

        //Calc time
        for (int i = 0; i < Consts.freqs.length; i++)
            Consts.freqs[i] = Consts.SAMPLE_RATE * i / Consts.freqs.length;

        //Calc freq
        for (int i = 0; i < Consts.times.length; i++)
            Consts.times[i] = stepSize * i / Consts.SAMPLE_RATE;

    }

    public List<Complex[]> getWinFFT() {
        return winFFT;
    }
}
