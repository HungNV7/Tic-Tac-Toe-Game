/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.clients;

import sample.UIs.GameUI;
import sample.UIs.HomeUI;
import sample.utils.KeyWord;
import sample.UIs.LoginUI;
import java.awt.Font;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class ClientManager {
    private LoginUI loginUI;
    private HomeUI homeUI;
    private HashMap<Integer, GameUI> listGameUI;
    private Client client;
    private String name;
    private boolean flag=false;
    private boolean isValidName;
    HashMap<Integer, String> listCounter;
    HashMap<Integer, Boolean> listYourTurn;
    HashMap<Integer, String> listYourText;

    public ClientManager() {
        loginUI=new LoginUI(this);
        client=new Client(this);
        loginUI.setVisible(true);
        listCounter=new HashMap<>();
        listGameUI=new HashMap<>();
        listYourTurn=new HashMap<>();
        listYourText=new HashMap<>();
    }

    public void getConnection(String name){
        client=new Client(this);
            if(client.connect(name)){
                while(!flag){ // wait until having a first message from server  
                     System.out.println();
                }
                  System.out.println(isValidName);
                    if(isValidName){                 
                        this.name=name;
                        this.loginUI.dispose();
                        homeUI=new HomeUI(this);
                        homeUI.setVisible(true);
                    }else{
                        JOptionPane.showMessageDialog(this.loginUI, "Please choose other name!");
                        flag=false;
                    }
            }else{
                JOptionPane.showMessageDialog(loginUI, "Server is not run!");
            }
    }
    
    public void removePlayer(){
        client.sendData(KeyWord.REMOVE_NAME+name);
    }
    
    public void sendInvitation(String playerInvited){
        client.sendData(KeyWord.INIVTE+playerInvited);
    }
    
    public void respondToInvitation(String data){
        String str[]=data.split("[:-]");
        String counter_player=str[2];
        
        String[] options = {"Accept", "Reject"};
        int x=JOptionPane.showOptionDialog(homeUI, "Do you want to play with "+counter_player+"?", "Invite", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
        System.out.println(x);
        if(x==0){
            
            int roomID=createRoomID();
            listCounter.put(roomID, counter_player);
            
            GameUI gameUI=new GameUI(this,roomID); // loi tu day
            
            this.listGameUI.put(roomID, gameUI);
           // gameUI.setLbName(this.name);
            gameUI.setLbRoomID(""+roomID);
            
            Random rd=new Random();
            int index=rd.nextInt(2);
            String textDisplayed=(index==0)?"X":"O";           
            this.listYourText.put(roomID,textDisplayed); //loi dong nay
            System.out.println("_______________________");
            if(textDisplayed.equals("X")){
                //yourTurn=true;
                this.listYourTurn.put(roomID, true);
            }else{
                this.listYourTurn.put(roomID, false);
            }
            //this.homeUI.setVisible(false);
            gameUI.setVisible(true);
            
            gameUI.setLbName(this.name+":"+textDisplayed+"-player");
            gameUI.setLbTurn(this.listYourTurn.get(roomID)?textDisplayed:(textDisplayed.equals("X")?"O":"X"));
            client.sendData(KeyWord.RESPONE_INVITATION+"OK-"+str[1]+"-"+str[2]+"-"+textDisplayed+"-"+roomID); //"response:
        }else{
            client.sendData(KeyWord.RESPONE_INVITATION+"NO-"+str[1]+"-"+str[2]);
        }
    }
    
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    
    public void addPlayerNameToList(String data){
        String str[]=data.split(":");
        String name=str[1];
        homeUI.addNewElement(name);
    }

    public void removePlayerName(String data){
        String str[]=data.split(":");
        String name=str[1];
        homeUI.removeElement(name);
    }
    
    public void receiveRespone(String data){
        if(data.contains("OK")){
            String str[]=data.split("-");
            String turn=str[3];
            String textDisplayed=(turn.equals("X"))?"O":"X";

            int roomID=Integer.parseInt(str[4]);
            GameUI gameUI=new GameUI(this, roomID);
             this.listYourText.put(roomID, textDisplayed);
             if(textDisplayed.equals("X")){
                //yourTurn=true;
                this.listYourTurn.put(roomID, true);
            }
            else{
                this.listYourTurn.put(roomID, false);
            }
            this.listGameUI.put(roomID, gameUI);
            gameUI.setLbRoomID(str[4]);
            //this.homeUI.setVisible(false);
            gameUI.setVisible(true);
            
            gameUI.setLbName(this.name+":"+textDisplayed+"-player");
            gameUI.setLbTurn(this.listYourTurn.get(roomID)?textDisplayed:(textDisplayed.equals("X")?"O":"X"));
            this.listCounter.put(Integer.parseInt(str[4]), str[1]);
        }
        else{
            String str[]=data.split("-");
            JOptionPane.showMessageDialog(homeUI, str[1]+" reject!");
        }
    }
    
    public void sendRequestUpdateUI(String data){
        String str[]=data.split("[:-]");
        String roomID=str[2];
        String msg=data+"-"+listCounter.get(Integer.parseInt(roomID)); //"your-turn:{roomID}-{position}-{counterPlayer}"
        client.sendData(msg);
        
    }
    
    public int createRoomID(){
        Random rd=new Random();
        return rd.nextInt(900000)+100000; //roomID with 6 digits
    }
    
    public void receiveRequestUpdateUI(String data){
        String str[]=data.split("[:-]");
        String roomID=str[2];
        GameUI gameUI=listGameUI.get(Integer.parseInt(roomID));
        int index=Integer.parseInt(str[3]);
        String textDisplayed=listYourText.get(Integer.parseInt(roomID));
        (gameUI.getArr())[index]=textDisplayed.equals("X")?1:0;
        (gameUI.getListLb())[index].setText(textDisplayed.equals("X")?"O":"X");
        gameUI.setLbTurn(textDisplayed.equals("X")?"X":"O");
        
        this.listYourTurn.put(Integer.parseInt(roomID),true);
    }
    
    public void sendResult(String data){
        String str[]=data.split("[:-]");
        String roomID=str[1];
        String msg=data+"-"+listCounter.get(Integer.parseInt(roomID)); //"result:{roomID}-{result}-{counter-player}"
        client.sendData(msg);
    }
    
    public void receiveResult(String data){
       String str[]=data.split("[:-]");
        int roomID=Integer.parseInt(str[1]);
        if(data.contains("You lose!")){
            JOptionPane.showMessageDialog(listGameUI.get(roomID), "You lose!");
            setUpNewGame(roomID);
        }else if(data.contains("Draw!")){
            JOptionPane.showMessageDialog(listGameUI.get(roomID), "Draw!");
            setUpNewGame(roomID);
        }
    }
    
    public void setUpNewGame(int roomID){
        GameUI gameUI=listGameUI.get(roomID);
        gameUI.drawNewGame();
        String text=listYourText.get(roomID);
        text=text.equals("X")?"O":"X";
        listYourText.put(roomID,text);
        boolean yourTurn=text.equals("X");
        listYourTurn.put(roomID, yourTurn);
        gameUI.setLbTurn("X");
        gameUI.setLbName(this.name+":"+text+"-player");
    }
    
    public void sendQuitGameInFo(int roomID){
        String msg=KeyWord.QUIT+roomID+"-"+listCounter.get(roomID);
        client.sendData(msg);
    }
    
    public void receiveQuitInfo(String data){
        String str[]=data.split("[:-]");
        int roomID=Integer.parseInt(str[1]);
        JOptionPane.showMessageDialog(listGameUI.get(roomID),listCounter.get(roomID)+" quited");
//        listGameUI.get(roomID).dispose();
    }
    
    public void setIsValidName(boolean isValidName) {
        this.isValidName = isValidName;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, Boolean> getListYourTurn() {
        return listYourTurn;
    }

    public HashMap<Integer, String> getListYourText() {
        return listYourText;
    }
}
