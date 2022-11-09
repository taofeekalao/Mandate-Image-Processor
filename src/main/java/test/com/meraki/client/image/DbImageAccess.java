package test.com.meraki.client.image;

import oracle.jdbc.OracleResultSet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Base64;

public class DbImageAccess {

    public DbImageAccess() {
    }

    static final String USERNAME = "DBA";
    static final String PASSWORD = "MySQL@dm1n$";
    static final String CONNECTION_STRING = "jdbc:mysql://127.0.0.1/Sakila";

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName ("com.mysql.cj.jdbc.Driver").newInstance();;
            connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return connection;
    }

    private String retrieveBaseSixtyFour(Connection connection, String recordId) {
        Blob blob;
        Blob blobs;
        String base64EncodedImageBytes = "";
        byte byteArray[] ;
        if (null != connection) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT PICTURE FROM STAFF WHERE STAFF_ID = " + "'" + recordId + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    blob = resultSet.getBlob("PICTURE");
                    byteArray = blob.getBytes(1L, (int) blob.length());
                    base64EncodedImageBytes = Base64.getEncoder().encodeToString(byteArray);
                    System.out.println(base64EncodedImageBytes);
                    blob.free();


/*                    blobs = resultSet.getObject("PICTURE");
                    InputStream inputStream = blobs.getBinaryStream(1L, blobs.length());
                    byte[] bytesArrayFromInputStream = new byte[(int) blobs.length()];
                    inputStream.read(bytesArrayFromInputStream);
                    String base64 = Base64.getEncoder().encodeToString(bytesArrayFromInputStream);
                    System.out.println(base64);
                    blobs.free();*/


                    connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            } /*catch (IOException e) {
                throw new RuntimeException(e);
            }*/
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
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Tawfiq\\Pictures\\ImageTesting\\" + filename + ".jpg");
            fileOutputStream.write(imageByteArray);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
