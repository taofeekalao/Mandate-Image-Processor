package com.meraki.client.image;

import test.com.meraki.client.image.DbImageAccess;

public class Main {

    public static void main(String[] args) {
        DbImageProcessor dbImageProcessor = new DbImageProcessor();
//        dbImageProcessor.processImage("029260999819.jpg");

//
        ImageLight imageLight = new ImageLight();
        dbImageProcessor.processImage("20220118912240978.png]SIGNATURES]A");


//        DbImageAccess dbImageAccess = new DbImageAccess();
//        dbImageAccess.processImage("20220118292729515.png]SIGNATURES]A");
    }
}
