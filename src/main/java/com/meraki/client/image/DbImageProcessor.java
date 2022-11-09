package com.meraki.client.image;

import oracle.jdbc.OracleResultSet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Base64;

public class DbImageProcessor {

    public DbImageProcessor() {
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

/*    private String retrieveBaseSixtyFour(Connection connection, String recordId) {
        Blob blob;
        String base64EncodedImageBytes = "";
        byte byteArray[] ;
        if (null != connection) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT XMLRECORD FROM PHOTOS WHERE RECID = " + "'" + recordId + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    blob = resultSet.getBlob("XMLRECORD");
                    byteArray = blob.getBytes(1L, (int) blob.length());
                    base64EncodedImageBytes = Base64.getEncoder().encodeToString(byteArray);
                    blob.free();
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

        return base64EncodedImageBytes;
    }*/


    private String retrieveBaseSixtyFour(Connection connection, String recordIdAndType) {

        String TABLE_NAME = recordIdAndType.split("]")[1];
        String imageId = recordIdAndType.split("]")[0];
        Blob blob;
        Blob blobs;
        String base64EncodedImageBytes = "";

        byte byteArray[] ;
        if (null != connection) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT XMLRECORD FROM " + TABLE_NAME + " WHERE RECID = " + "'" + imageId + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    blob = ((OracleResultSet)resultSet).getBLOB("XMLRECORD");
                    byteArray = blob.getBytes(1L, (int) blob.length());
                    base64EncodedImageBytes = Base64.getEncoder().encodeToString(byteArray);
                    System.out.println(base64EncodedImageBytes);
                    blob.free();


                    blobs = (Blob)resultSet.getObject("XMLRECORD");
                    InputStream inputStream = blobs.getBinaryStream(1L, blobs.length());
                    byte[] bytesArrayFromInputStream = new byte[(int) blobs.length()];
                    inputStream.read(bytesArrayFromInputStream);
                    String base64 = Base64.getEncoder().encodeToString(bytesArrayFromInputStream);
                    System.out.println(base64);
                    blobs.free();


                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return base64EncodedImageBytes;
    }

    public void processImage(String param) {
        String base64EncodedImageBytes  = retrieveBaseSixtyFour(getConnection(), param);
        byte[] imageByteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64EncodedImageBytes);
        DecodeAndWriteImageToFileFromAPI(imageByteArray, "jpg", param);
    }


    private void DecodeAndWriteImageToFileFromAPI(byte[] imageByteArray, String format, String filename) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/Temenos/T24/bnk/UD/TEST.IM/" + filename);
            fileOutputStream.write(imageByteArray);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
