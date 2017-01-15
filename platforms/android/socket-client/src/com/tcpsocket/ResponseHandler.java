package com.tcpsocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Stack;

public class ResponseHandler extends Thread
{
    private OutputStream output;
    private boolean stop;
    private Stack<String> messages;

    public ResponseHandler(Socket socket) throws IOException
    {
        this.output = socket.getOutputStream();
        this.messages = new Stack<>();
    }

    @Override
    public void run()
    {
        // Send message with the handshake key
        this.sendMessage("{\"handshake-key\":\"66ervca04kxz4mgja5d4jme59a8\"}");

        while (!stop)
        {
            if(!messages.empty()) {
                String message = messages.pop();

                this.sendMessage(message);
            }
        }

        try {
            this.output.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String message) {
        this.messages.push(message);
    }

    private void sendMessage(String msg) {
        byte[] utf8;
        try
        {
            utf8 = msg.getBytes("UTF8");
        }
        catch (UnsupportedEncodingException e1)
        {
            utf8 = null;
            e1.printStackTrace();
        }

        if (utf8 != null)
        {
            try
            {
                String s = "Content-Length: " + utf8.length;
                byte[] arr = s.getBytes("UTF8");
                output.write(arr);

                output.write(utf8);
                output.flush();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}