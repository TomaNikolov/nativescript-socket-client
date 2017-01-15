package com.tcpsocket;

import java.io.IOException;
import java.net.Socket;

public class SocketClient extends Emitter {
    private Socket socket;
    private int port;
    private String address;
    private ResponseHandler responseHandler;

    public SocketClient(String address, int port) {
        this.port = port;
        this.address = address;
    }

    public void connect() {
      Thread thread = new Thread(() -> {
              try {
                  System.out.println("Listening for a connection");
                  SocketClient.this.socket = new Socket(address, port);

                  // Pass the socket to the RequestHandler thread for processing
                  RequestHandler requestHandler = new RequestHandler(SocketClient.this.socket, SocketClient.this);
                  requestHandler.start();

                  // Pass the socket to the RequestHandler thread for processing
                  SocketClient.this.responseHandler = new ResponseHandler(socket);
                  SocketClient.this.responseHandler.start();

              } catch (IOException ex) {
                  SocketClient.this.emit("error", ex.getMessage());
                  ex.printStackTrace();
              }
          });

        thread.start();
    }

    public void disconnect() {
        //this.interrupt();
    }

    public void write(String message) {
        if (this.responseHandler != null) {
            this.responseHandler.addMessage(message);
        }
    }
}