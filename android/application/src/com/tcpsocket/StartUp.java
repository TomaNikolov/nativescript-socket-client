package com.tcpsocket;

import jdk.nashorn.api.scripting.JSObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.*;

public class StartUp {
    public static void main( String[] args )
    {


        String address = "192.168.56.2";
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
                    try {
                        System.out.println("Emitter:" + arg);
                        JSONObject  obj = new JSONObject(arg.toString());
                        JSONObject file =  obj.getJSONObject("file") ;
                        System.out.println("file:" + file);
                        JSONArray fileBytes = file.getJSONArray("data");
                        fileBytes.
                        byte[] bytes = new byte[fileBytes.length()];
                        for (int i = 0; i < bytes.length; i++) {
                             bytes[i] = ((byte) fileBytes.get(i));
                        }

                        FileOutputStream fos = new FileOutputStream("./app.zip");
                        fos.write(bytes);
                        fos.close();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
