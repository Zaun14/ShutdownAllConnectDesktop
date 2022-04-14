package com.zaun;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.*;

public class Connect extends Thread {

    private static int port = 45000;
    private static InetAddress ip;
    private static byte[] bufferRead = new byte[256];
    private static final String startMsg = "start", pingMsg = "iscon", checkMsg = "wait";
    private static DatagramPacket read;
    private static DatagramSocket server;

    public Connect(String ip, int port) {
        try {
            this.ip = InetAddress.getByName(ip);
            this.port = port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            ip = InetAddress.getByName("192.168.90.80");
            server = new DatagramSocket(0);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                bufferRead = new byte[256];
                server.send(createPacket(startMsg.getBytes(), startMsg.length(), ip, port));
                read = createPacket(bufferRead, bufferRead.length);
                server.receive(read);
                if (new String(read.getData()).trim().equals("ok")) {
                    while (isConnectedToServer(server)) {
                        try {
                            read = createPacket(bufferRead, bufferRead.length);
                            server.send(createPacket(checkMsg.getBytes(), checkMsg.length(), ip, port));
                            server.receive(read);
                            shutdown(new String(bufferRead, read.getOffset(), read.getLength()));
                        } catch (SocketTimeoutException e) {
                            System.out.println("disconnect");
                            break;
                        }
                    }
                }
            } catch (SocketException ignored) {

            } catch (IOException e) {
                System.out.println("Internet don't available");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static boolean isConnectedToServer(DatagramSocket server) throws IOException {
        try {
            server.setSoTimeout(3000);
            server.send(new DatagramPacket(pingMsg.getBytes(), 0, pingMsg.length(), ip, port));
            server.receive(read);
            if (new String(read.getData(), 0, read.getLength()).trim().equals(pingMsg)) return true;
        } catch (SocketTimeoutException e) {
            return false;
        } finally {
            server.setSoTimeout(0);
        }
        return false;
    }

    private static void shutdown(@NotNull String msg) throws IOException {
        if (msg.equals("shutdown")) System.out.println("shutdown");
        else return;
        if (System.getProperty("os.name").equals("Linux")) {
            Runtime.getRuntime().exec("shutdown -P 0");
        } else if (System.getProperty("os.name").startsWith("Windows")) {
            Runtime.getRuntime().exec("shutdown /s /t 0");
        }
    }


    private static DatagramPacket createPacket(byte[] buffer, int msgLength, InetAddress ip, int port) {
        return new DatagramPacket(buffer, 0, msgLength, ip, port);
    }

    private static DatagramPacket createPacket(byte[] buffer, int bufferLength) {
        return new DatagramPacket(buffer, bufferLength);
    }
}

