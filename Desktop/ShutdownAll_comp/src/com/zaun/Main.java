package com.zaun;                                           //Packet com.zaun

public class Main {

    private static final String IP ="192.168.90.80";
    private static final int  PORT =45000 ;

    public static void main(String[] args) {
        Thread th = new Connect(IP , PORT);
        th.setDaemon(true);
        th.setName("Connect");
        th.start();
        while (true);
    }
}