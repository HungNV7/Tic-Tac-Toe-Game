/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.clients;

import sample.UIs.HomeUI;
import sample.utils.KeyWord;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class Client {
    Socket client;
    BufferedReader bf;
    BufferedWriter bw;
    Thread getData;
    Thread sendName;
    String name;
    ClientManager mng;

    public Client(ClientManager mng){
        this.mng=mng;
    }

    public boolean connect(String name)
    {
        try {
            client=new Socket(KeyWord.IP, 8080);
            bf=new BufferedReader(new InputStreamReader(client.getInputStream()));
            bw=new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            this.name=name;
            sendRequestCheckName();
            getDataFromServer();
            return true;
        } catch (IOException ex) {  
        }
        return false;
    }
    
    public void sendData(String data){
        try {
            bw.write(data);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
             System.out.println(ex);
        }
    }
    
    public void sendRequestCheckName(){
        sendData(KeyWord.USER_NAME+name);
    }

    public void getDataFromServer(){
        getData=new Thread(){

            @Override
            public void run() {
                try {
                   while(true){
                        String data;
                        if((data=bf.readLine())!=null){
                            System.out.println(data);
                            if(data.contains(KeyWord.NEW_CLIENT)){
                                System.out.println(data);
                            }
                            if(data.equals(KeyWord.USER_NAME_VALID)){
                                mng.setIsValidName(true);
                                mng.setFlag(true);
                                sendClientName();
                            }
                            if(data.equals(KeyWord.USER_NAME_INVALID)){
                                mng.setIsValidName(false);
                                mng.setFlag(true);
                            }
                            if(data.contains(KeyWord.NEW_CLIENT)){
                                mng.addPlayerNameToList(data);
                            }
                            if(data.contains(KeyWord.INIVTE)){
                                mng.respondToInvitation(data);
                            }
                            if(data.contains(KeyWord.REMOVE_NAME)){
                                mng.removePlayerName(data);
                            }
                            if(data.contains(KeyWord.RESPONE_INVITATION)){
                                mng.receiveRespone(data);
                            }
                            if(data.contains(KeyWord.YOUR_TURN)){
                                mng.receiveRequestUpdateUI(data);
                            }
                            if(data.contains(KeyWord.RESULT)){
                                mng.receiveResult(data);
                            }
                            if(data.contains(KeyWord.QUIT)){
                                mng.receiveQuitInfo(data);
                            }
                        }
                        Thread.sleep(100);
                    }    
                    
                } catch (Exception e) {
                }
               
            }  
        };
        getData.start();
        
    }
    
    public void sendClientName(){
        sendName=new Thread(){

            @Override
            public void run() {
                try {
                    while(true){                       
                        sendData(KeyWord.NEW_CLIENT+name);
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                }
            }
            
        };
        sendName.start();
    }
    
}
