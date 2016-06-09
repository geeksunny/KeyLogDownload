package com.radicalninja.logdownload;

public class Main {

    private static final String LOG_DIRECTORY = "logs";

    public static void main(String[] args) {
        new LogPuller().doPullOperation();
    }

}
