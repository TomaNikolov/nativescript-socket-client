package com.tcpsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartUp {
    public static void main( String[] args )
    {


        String address = "192.168.129.22";
        int port = 8080;
        System.out.println( "Start server on port: " + port );

        SocketClient client = new SocketClient( address, port);
        client.connect();

        client.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected" );
                client.write("{\"handshake-key\":\"66ervca04kxz4mgja5d4jme59a8\"}");
            }
        });

        client.on("data", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    System.out.println("Emitter:" + arg.toString());
                }
            }
        });

        client.on("error", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    System.out.println("Error:" + arg.toString());
                }
            }
        });

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter String");
            String s = null;
            try {
                s = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(s != null) {
                if(s == "break") {
                    break;
                }

                client.write(s);
            }
        }

        client.disconnect();
    }
}
