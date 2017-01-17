package com.tcpsocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Stack;

public class ResponseHandler extends Thread
{
    private final Emitter emitter;
    private OutputStream output;
    private boolean stop;
    private Stack<String> messages;

    public ResponseHandler(Socket socket, Emitter emitter) throws IOException {
        this.output = socket.getOutputStream();
        this.emitter = emitter;
        this.messages = new Stack<>();
    }

    @Override
    public void run()
    {
        while (!stop) {
            if(!messages.empty()) {
                String message = messages.pop();
                this.sendMessage(message);
            }
        }

        try {
            this.output.close();
        }
        catch(IOException e) {
            this.emitter.emit(EmitterConstants.ERROR);
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        this.stop = true;
    }

    public void addMessage(String message) {
        this.messages.push(message);
    }

    private void sendMessage(String msg) {
        byte[] utf8;
        try {
            utf8 = msg.getBytes("UTF8");
        } catch (UnsupportedEncodingException ex) {
            utf8 = null;
            ex.printStackTrace();
        }

        if (utf8 != null) {
            try {
                output.write(utf8);
                output.flush();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}