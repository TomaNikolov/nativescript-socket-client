package com.tcpsocket.client;

import java.io.IOException;
import java.net.Socket;

public class SocketClient extends Emitter {
    private Socket socket;
    private int port;
    private String address;
    private ResponseHandler responseHandler;
    private RequestHandler requestHandler;

    public SocketClient(String address, int port) {
        this.port = port;
        this.address = address;
    }

    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketClient.this.socket = new Socket(address, port);

                    // Pass the socket to the RequestHandler thread for processing
                    SocketClient.this.requestHandler = new RequestHandler(SocketClient.this.socket, SocketClient.this);
                    SocketClient.this.requestHandler.start();

                    // Pass the socket to the RequestHandler thread for processing
                    SocketClient.this.responseHandler = new ResponseHandler( SocketClient.this.socket, SocketClient.this);
                    SocketClient.this.responseHandler.start();
                    SocketClient.this.emit(EmitterConstants.CONNECT);
                } catch (IOException ex) {
                    SocketClient.this.emit(EmitterConstants.ERROR, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void disconnect() {
       this.requestHandler.stopRunning();
       this.responseHandler.stopRunning();

       try {
           this.socket.close();
       } catch (IOException ex) {
           ex.printStackTrace();
       }
       this.emit(EmitterConstants.CLOSE);
    }

    public void write(String message) {
        if (this.responseHandler != null) {
            this.responseHandler.addMessage(message);
        }
    }
}