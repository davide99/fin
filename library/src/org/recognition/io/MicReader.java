package org.recognition.io;

import org.recognition.utils.Consts;
import org.recognition.utils.Helper;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.sound.sampled.*;

public class MicReader extends Thread {
    private boolean stopped;
    private TargetDataLine line;
    private int seconds;
    private ByteArrayOutputStream out;

    public MicReader(int seconds) {
        //Indica se la registrazione è stoppata o meno
        stopped = false;
        out = new ByteArrayOutputStream();
        this.seconds = seconds;

        //Imposto il formato: sample rate, sample size in bits, channels, signed, bigEndian
        AudioFormat format = new AudioFormat(Consts.SAMPLE_RATE, 16, Consts.CHANNELS, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        //Ottengo la linea
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, Helper.getPreferredBufferSize(format));
        } catch (LineUnavailableException e) {
            System.out.println("Errore nel ottenimento della linea");
            e.printStackTrace();
        }
    }

    public void run() {
        //Lambda per stoppare la registrazione
        new Thread(() -> {
            try {
                sleep(seconds * 1000);
                stopped = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        int numBytesRead;
        //https://stackoverflow.com/questions/31897177/reduce-delay-when-playing-audio-from-mic-in-java
        byte[] buffer = new byte[line.getBufferSize()];

        // Inizio a catturare audio
        line.start();

        while (!stopped) {
            // Leggo il chunk successivo dalla TargetDataLine.
            numBytesRead = line.read(buffer, 0, buffer.length);

            // Salvo il chunk
            out.write(buffer, 0, numBytesRead);
        }

        line.drain();
        line.close();
    }

    public synchronized float[] getData() {
        //L'oggetto ByteArrayOutputStream viene convertito in un array statico
        byte[] bytes = out.toByteArray();
        //La dimensione del nuovo array sarà esattamente la metà dell'altro
        float[] data = new float[bytes.length / 2];
        float max = -1;

        for (int i = 0; i < bytes.length - 1; i += 2) {
            //Viene utilizzata la libreria Apache EndianUtils per convertire due byte in uno short
            data[i / 2] = EndianUtils.readSwappedShort(bytes, i);
            if (Math.abs(data[i / 2]) > max)
                max = Math.abs(data[i / 2]);
        }

        float mul = Short.MAX_VALUE / max;

        for (int i = 0; i < data.length; i++)
            data[i] *= mul;

        return data;
    }
}
