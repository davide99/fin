package org.recognition.io;

import org.recognition.utils.Helper;
import org.apache.commons.io.EndianUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class WavReader {
    private String title, fileName;
    private float[] data;

    public WavReader(String fileName) {
        try {
            this.fileName = fileName;
            this.title = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.indexOf(".wav"));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            AudioInputStream in = AudioSystem.getAudioInputStream(new File(fileName));

            AudioFormat format = in.getFormat();

            if (Helper.checkAudioFormat(format))
                throw new Exception("Audio format not supported");

            int numBytesRead;
            byte[] buffer = new byte[Helper.getPreferredBufferSize(format)];

            while ((numBytesRead = in.read(buffer)) != -1)
                out.write(buffer, 0, numBytesRead);

            byte[] bytes = out.toByteArray();
            data = new float[bytes.length / 2];

            for (int i = 0; i < data.length; i++)
                data[i] = EndianUtils.readSwappedShort(bytes, i * 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float[] getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName() {
        return fileName;
    }
}
