/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.servers;

import sample.utils.KeyWord;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Admin
 */
public class ThreadServer extends Thread{
   private Socket client;
   private BufferedReader bf;
   private BufferedWriter bw;
   private Server server;
   private String name;
   
   public ThreadServer(Socket client, Server server) throws IOException{
       this.client=client;
       this.server=server;
       bf=new BufferedReader(new InputStreamReader(client.getInputStream()));
       bw=new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while(true){
                String data;
                if((data=bf.readLine())!=null){
                    System.out.println(data);   
                    if(data.contains(KeyWord.USER_NAME)){
                        checkNameClient(data);
                    }
                    if(data.contains(KeyWord.REMOVE_NAME)){
                         removePlayer(data);
                    }
                    if(data.contains(KeyWord.NEW_CLIENT)){
                        sendDataToAll(data);
                    } 
                    if(data.contains(KeyWord.INIVTE)){
                        sendToPlayerInvited(data);
                    }
                    if(data.contains(KeyWord.RESPONE_INVITATION)){
                        sendRespone(data);
                    }
                    if(data.contains(KeyWord.YOUR_TURN)){
                        sendRequestUpdateUIToClient(data);
                    }
                    if(data.contains(KeyWord.RESULT)){
                        sendResultToClient(data);
                    }
                    if(data.contains(KeyWord.QUIT)){
                        sendQuitToClient(data);
                    }
                }
            }
        } catch (Exception e) {
        }
    }  
    
   public void sendDataToAll(String data){
       for(String key: server.getListThread().keySet()){
           if(!key.equals(name)){
               server.getListThread().get(key).sendData(data);
           }
       }
   }
    public void checkNameClient(String data){
        String str[]=data.split(":");
        this.name=str[1];
        if(!server.getListThread().containsKey(name)){
            server.getListThread().put(name, this);
            sendData(KeyWord.USER_NAME_VALID);
        }else{
            sendData(KeyWord.USER_NAME_INVALID);
        }
        
    }
    
    public void removePlayer(String data){
        String str[]=data.split(":");
        String name=str[1];
        server.getListThread().remove(name);
        sendDataToAll(data);
    }
    
    public void sendToPlayerInvited(String data){
        String str[]=data.split(":");
        String name=str[1];
        server.getListThread().get(name).sendData(data+"-"+this.name);//"invite:player1-player2", player1-nguoi dc moi; player2-nguoi moi
    }
    
    public void sendRespone(String data){
        String str[]=data.split("-");
        String name=str[2];
        server.getListThread().get(name).sendData(data);
    }
    
    public void sendRequestUpdateUIToClient(String data){
        String str[]=data.split("-");
        String name=str[3];
        server.getListThread().get(name).sendData(data);
    }
   
    public void sendResultToClient(String data){
        String str[]=data.split("-");
        String name=str[2];
        server.getListThread().get(name).sendData(data);
    }
    
    public void sendQuitToClient(String data){
        String str[]=data.split("[:-]");
        String name=str[2];
        server.getListThread().get(name).sendData(data);
    }
    
   public void sendData(String data){
       try {
           bw.write(data);
           bw.newLine();
           bw.flush();
       } catch (IOException ex) {
           System.out.println("Error send data from server:"+ex);
       }
   }

   
   
   
}
