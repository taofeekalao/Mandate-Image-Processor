package com.meraki.client.image;

import test.com.meraki.client.image.DbImageAccess;

public class Main {

    public static void main(String[] args) {
//        DbImageProcessor dbImageProcessor = new DbImageProcessor();
//        dbImageProcessor.processImage("029260999819.jpg");
//
//        ImageLight imageLight = new ImageLight();
//        imageLight.processImage("040160051212.jpg");

        DbImageAccess dbImageAccess = new DbImageAccess();
        dbImageAccess.processImage(Integer.toString(2));
    }
}
