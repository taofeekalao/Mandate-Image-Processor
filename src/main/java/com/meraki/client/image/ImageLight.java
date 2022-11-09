package com.meraki.client.image;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;

public class ImageLight {

    public ImageLight() {
    }

    static final String USERNAME = "t24";
    static final String PASSWORD = "t24";
    static final String CONNECTION_STRING = "jdbc:oracle:thin:@10.219.101.30:1521:BPRCHAN";

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName ("oracle.jdbc.OracleDriver");
            connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    private byte[] retrieveBaseSixtyFour(Connection connection, String recordId) {
        byte[] bytesArray;
        byte[] imageByteArray = null;
        String base64EncodedImageBytes = "";
        if (null != connection) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT XMLRECORD FROM PHOTOS WHERE RECID = " + "'" + recordId + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    bytesArray = resultSet.getBytes("XMLRECORD");
                    base64EncodedImageBytes = Base64.getEncoder().encodeToString(bytesArray);
                    imageByteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64EncodedImageBytes);
                    connection.close();
                    System.out.println(base64EncodedImageBytes);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return imageByteArray;
    }

    public void processImage( String recordId) {
        byte imageByteArray[] = retrieveBaseSixtyFour(getConnection(), recordId);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/Temenos/T24/bnk/UD/TEST.IM/" + recordId);
            FileOutputStream fileOut = new FileOutputStream("/Temenos/T24/bnk/UD/TEST.IM/" + recordId + "." + "png");
            fileOutputStream.write(imageByteArray);
            fileOut.write(imageByteArray);
            fileOutputStream.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}