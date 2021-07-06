package org.recognition.utils;

import javax.sound.sampled.AudioFormat;

public class Helper {
    public static boolean checkAudioFormat(AudioFormat f) {
        boolean b;
        b = f.getChannels() == Consts.CHANNELS;
        b = b && (f.getSampleRate() != Consts.SAMPLE_RATE);
        b = b && (f.getEncoding() != AudioFormat.Encoding.PCM_SIGNED);

        return b;
    }

    public static int getPreferredBufferSize(AudioFormat f) {
        return (int) (f.getFrameRate() * f.getFrameSize() / 10);
    }

    public static double[] floatArrayToDouble(float[] f) {
        double[] d = new double[f.length];

        for (int i = 0; i < f.length; i++)
            d[i] = f[i];

        return d;
    }
}
