package org.recognition.spectrogram;

import org.recognition.utils.Consts;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.FastMath;

class Window {
    private final float[] window;

    Window() {
        window = new float[Consts.WIN_SIZE];

        //Calc hamming function
        for (int i = 0; i < window.length; i++)
            window[i] = (float) (0.5 * (1 - FastMath.cos(2 * FastMath.PI * i / (window.length - 1))));
    }

    float[] extractWindow(float[] data, int pos) {
        //Extract the values
        float[] values = ArrayUtils.subarray(data, pos, pos + window.length);

        //Mul by window
        for (int i = 0; i < values.length; i++)
            values[i] *= window[i];

        return values;
    }
}
