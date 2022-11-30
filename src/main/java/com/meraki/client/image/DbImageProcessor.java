package com.meraki.client.image;

import oracle.jdbc.OracleResultSet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Base64;
import java.util.Properties;

public class DbImageProcessor {

    public DbImageProcessor() {
    }

    static String USERNAME;
    static String PASSWORD;
    static String CONNECTION_STRING;

    private static final Properties appProperties;

    static {
        appProperties = new Properties();
        try {
            ClassLoader classLoader = DbImageProcessor.class.getClassLoader();
            InputStream applicationPropertiesStream = classLoader.getResourceAsStream("application.properties");
            appProperties.load(applicationPropertiesStream);
            USERNAME = appProperties.getProperty("username");
            PASSWORD = appProperties.getProperty("password");
            CONNECTION_STRING = appProperties.getProperty("dbConnection");
            System.out.println("This is the user name : " + USERNAME);
            System.out.println("This is the password : " + PASSWORD);
            System.out.println("This is the connection string : " + CONNECTION_STRING);

            applicationPropertiesStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    private String retrieveBaseSixtyFour(Connection connection, String TABLE_NAME, String imageId) {
        Blob blob;
        String base64EncodedImageBytes = "";

        byte byteArray[];
        if (null != connection) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT XMLRECORD FROM " + TABLE_NAME + " WHERE RECID = " + "'" + imageId + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    blob = ((OracleResultSet) resultSet).getBLOB("XMLRECORD");
                    byteArray = blob.getBytes(1L, (int) blob.length());
                    base64EncodedImageBytes = Base64.getEncoder().encodeToString(byteArray);
                    blob.free();
                    connection.close();
                }
            } catch (SQLException exception) {
                exception.getMessage();
                System.out.println(exception.getMessage());
                System.out.println(exception.getCause());
                System.out.println(exception.getErrorCode());
                exception.printStackTrace();
            }
        }

        return base64EncodedImageBytes;
    }

    public void processImage(String recordIdAndType) {
        String imageId = recordIdAndType.split("]")[0];
        String TABLE_NAME = recordIdAndType.split("]")[1];
        String filePath = recordIdAndType.split("]")[2];
        String base64EncodedImageBytes = retrieveBaseSixtyFour(getConnection(), TABLE_NAME, imageId);
        byte[] imageByteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64EncodedImageBytes);
        DecodeAndWriteImageToFileFromAPI(imageByteArray, imageId, filePath);
    }

    private void DecodeAndWriteImageToFileFromAPI(byte[] imageByteArray, String filename, String filePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/Temenos/jboss/standalone/Images/shares/im.images/signatures/" + filename);
            fileOutputStream.write(imageByteArray);
            fileOutputStream.close();
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.getMessage();
        }
    }
}
