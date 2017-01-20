package com.tcpsocket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

class RequestHandler extends Thread
{
    private final Emitter emitter;
    private boolean stop;
    private BufferedReader input;
    RequestHandler(Socket socket, Emitter emitter) throws IOException {
        this.emitter = emitter;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        // Get input streams
        Scanner scanner = new Scanner(this.input);
        scanner.useDelimiter("\r\n");

        String line = scanner.nextLine();
        String buffer = "";
        while (!stop && line != null && line.length() > 0) {
            if(!line.endsWith("\n")){
                buffer += line;
            } else {
                emitter.emit(EmitterConstants.DATA, buffer + line);
                buffer = "";
            }

            line = scanner.nextLine();
        }

        scanner.close();
    }

    public void stopRunning() {
        this.stop = true;
    }
}
