package org.recognition.database;

import org.recognition.io.WavReader;
import org.recognition.model.Song;
import org.recognition.utils.Consts;
import org.recognition.fingerprint.Fingerprint;
import org.recognition.fingerprint.Links;

import java.sql.*;
import java.util.List;

public class DB {
    private Connection dbConn;
    private Statement dbStatement;

    public DB() {
        try {
            Class.forName(Consts.DB_DRIVER);
            dbConn = DriverManager.getConnection(Consts.DB_URL, Consts.DB_USER, Consts.DB_PASSWORD);

            //The following two lines tell mysql to stream the results row by row
            //https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-implementation-notes.html
            dbStatement = dbConn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            dbStatement.setFetchSize(Integer.MIN_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void insert(WavReader file) {
        try {
            //Insert song details into musicinfo table and get its PK
            String sql = "INSERT INTO musiclibrary.musicinfo (title, path) VALUES ( ? , ? );";

            PreparedStatement inStmnt = dbConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            inStmnt.setString(1, file.getTitle());
            inStmnt.setString(2, file.getFileName());
            inStmnt.executeUpdate();

            ResultSet rs = inStmnt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            rs.close();

            //Insert fingerprints
            StringBuilder buf = new StringBuilder("INSERT INTO musiclibrary.hashtable (hash, songId, time) VALUES");

            Fingerprint fingerprint = new Fingerprint(file.getData());
            Links linkList = new Links(fingerprint.getPeakList());

            //Key is the hash, value is the time
            linkList.forEach((link) -> {
                buf.append("(\"").append(link.getHash()).append("\",");
                buf.append(id).append(",");
                buf.append(link.getTime()).append("),");
            });

            buf.replace(buf.length() - 1, buf.length(), ";");

            dbStatement.execute(buf.toString());

        } catch (SQLException e) {
            System.out.print(e.getSQLState());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized int searchId(List<Links.Link> list ) {
        StringBuilder SQLin = new StringBuilder("INSERT INTO musiclibrary._record VALUES ");

        //Left is hash, right is time
        list.forEach(
                (link) -> SQLin.append("(\"").append(link.getHash()).append("\",").append(link.getTime()).append("),")
        );
        SQLin.setCharAt(SQLin.length() - 1, ';');

        try {
            dbStatement.executeUpdate(Consts.CREATE_TABLE_RECORD);
            dbStatement.executeUpdate(SQLin.toString());
            ResultSet resultSet = dbStatement.executeQuery(Consts.FIND_SONG);

            if (resultSet.next() && resultSet.getInt(2) > Consts.MINHINT)
                return resultSet.getInt(1);

        } catch (SQLException e) {
            System.out.print(e.getSQLState());
            e.printStackTrace();
        }

        return -1;
    }

    public synchronized Song getSongById(int id){
        try {
            String sql = "SELECT title FROM musiclibrary.musicinfo WHERE id = ?;";
            PreparedStatement inStmnt = dbConn.prepareStatement(sql);
            inStmnt.setInt(1, id);
            ResultSet rs = inStmnt.executeQuery();

            if(rs.next())
                return new Song(id, rs.getString("title"));

        } catch (SQLException e) {
            System.out.print(e.getSQLState());
            e.printStackTrace();
        }

        return null;
    }
}