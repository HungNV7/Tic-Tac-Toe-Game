/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class Server {

    private ServerSocket server;
    private HashMap<String, ThreadServer> listThread;
    private int id = 0;
    public Server() {
        try {
            server = new ServerSocket(8080);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        this.listThread = new HashMap<>();
    }

    public void acceptSocket() {
        Thread threadServer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Accepting client ..........");
                    try {
                        Socket client = server.accept();
                        if (client != null) {
                            ThreadServer thread = new ThreadServer(client, Server.this);
                            thread.start();
                        }
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
            }
        };
        threadServer.start();

    }

    public HashMap<String, ThreadServer> getListThread() {
        return listThread;
    }

    public void setListThread(HashMap<String, ThreadServer> listThread) {
        this.listThread = listThread;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptSocket();
    }
}
