package org.recognition.utils;

/**
 * Classe <b>Consts</b>
 *
 * @author Davide
 * <p>
 * Classe contenente alcune costanti utilizzate all'interno
 * delle altre classi
 */

public class Consts {
    public static final int OVERLAP = 256;
    public static final int WIN_SIZE = 512;
    public static final int SAMPLE_RATE = 8000;

    /* SONG SPECS */
    public static final int CHANNELS = 1;

    /* FINGERPRINT */
    public static final float MINFREQ = 100;
    public static final float MAXFREQ = 2000;
    public static final int NPEAKS = 3;
    public static final int C = 32;
    public static final int[] BANDS = {11, 22, 35, 50, 69, 91, 117, 149, 187, 231};
    public static float[] freqs;
    public static float[] times;

    /* PEAKS */
    public static final int PEAKRANGE = 5; //vicini

    /* DATABASE */
    public static final String DB_DRIVER = "org.mariadb.jdbc.Driver";
    public static final String DB_URL = "jdbc:mariadb://127.0.0.1:3306/";
    public static final String DB_USER = "davide";
    public static final String DB_PASSWORD = "ubuntu99!";

    /* SEARCH */
    public static final int MINHINT = 15;

    /* SQL CONSTANTS */
    public static final String CREATE_TABLE_RECORD =
            "CREATE TEMPORARY TABLE musiclibrary._record (" +
                    "hash_r VARCHAR(40) NOT NULL," +
                    "start INTEGER NOT NULL," +
                    "PRIMARY KEY (hash_r, start)" +
                    ") ENGINE=MEMORY;";
    public static final String FIND_SONG =
            "SELECT musiclibrary.hashtable.songId, COUNT(*) AS n " +
                    "FROM musiclibrary.hashtable INNER JOIN musiclibrary._record " +
                    "ON hashtable.hash=_record.hash_r " +
                    "GROUP BY " +
                    "hashtable.time-_record.start, songId " +
                    "ORDER BY n DESC";
}
