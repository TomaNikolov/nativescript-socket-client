package com.tcpsocket;

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
        while (!stop && line != null && line.length() > 0) {
            emitter.emit(EmitterConstants.DATA, line);
            line = scanner.nextLine();
        }

        scanner.close();
    }

    public void stopRunning() {
        this.stop = true;
    }
}

