package com.meraki.client.image;

import oracle.jdbc.OracleResultSet;
import java.io.*;
import java.sql.*;
import java.util.Base64;

public class DbImageProcessor {

    public DbImageProcessor() {
    }

    static String USERNAME;
    static String PASSWORD;
    static String CONNECTION_STRING;

    private void propertyFileReader() {
        BufferedReader bufferedReader = null;
        try {
            String path = System.getProperty("user.dir");
            Reader reader = new FileReader(path + "/src/main/resources/config/application.properties");
            bufferedReader = new BufferedReader(reader);
            String lineString;
            while (null != (lineString = bufferedReader.readLine())) {
                String key = lineString.split("=")[0];
                String encodedValue = lineString.split("=")[1];
                byte[] decodedValueInByte = Base64.getDecoder().decode(encodedValue);
                String value = new String(decodedValueInByte);

                switch (key) {
                    case "username":
                        USERNAME = value;
                        System.out.println("This is the user name : " + USERNAME);
                        break;

                    case "password":
                        PASSWORD = value;
                        System.out.println("This is the password : " + PASSWORD);
                        break;

                    case "dbConnection":
                        CONNECTION_STRING = value;
                        System.out.println("This is the connection string : " + CONNECTION_STRING);
                        break;
                }
            }

            bufferedReader.close();
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.getMessage();
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
        propertyFileReader();
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
