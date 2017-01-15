package com.tcpsocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

class RequestHandler extends Thread
{
    private final Emitter emitter;
    private Socket socket;
    RequestHandler(Socket socket, Emitter emitter) {
        this.socket = socket;
        this.emitter = emitter;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              Scanner scanner = new Scanner(input)) {
                emitter.emit("connect");

                // Get input streams
               // BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               // Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\r\n");

                String line = scanner.nextLine();
                while (line != null && line.length() > 0) {
                    emitter.emit("data", line);
                    line = scanner.nextLine();
                }

                // Close our connection
               // scanner.close();
                emitter.emit("close");
        }
        catch( Exception e ) {
            emitter.emit("error", e.getMessage());
            e.printStackTrace();
        }
    }
}

