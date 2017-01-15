package com.tcpsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartUp {
    public static void main( String[] args )
    {


        String address = "192.168.1.202";
        int port = 8080;
        System.out.println( "Start server on port: " + port );

        SocketClient client = new SocketClient( address, port);
//        Thread clientThread = new Thread(client);
//        clientThread.start();
        client.connect();

        client.on("data", new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (Object arg : args) {
                System.out.print("Emitter:" + arg.toString());
            }
        }
    });

        while (true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter String");
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
