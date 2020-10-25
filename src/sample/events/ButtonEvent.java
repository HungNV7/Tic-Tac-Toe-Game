/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.events;

import sample.clients.ClientManager;
import sample.UIs.GameUI;
import sample.utils.KeyWord;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class ButtonEvent implements MouseListener{
    ClientManager mng;
    GameUI gameUI;
    
    public ButtonEvent(ClientManager mng, GameUI gameUI) {
        this.mng=mng;
        this.gameUI=gameUI;
    }

    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        JLabel lb=(JLabel)e.getSource();
        String text=lb.getText();
        if(text.isEmpty()){
            if(mng.getListYourTurn().get(gameUI.getRoomID())){
                lb.setText(mng.getListYourText().get(gameUI.getRoomID()));
                lb.setFont(new Font("Tahoma", Font.PLAIN, 24));            
                gameUI.updateArr();
                mng.getListYourTurn().put(gameUI.getRoomID(), Boolean.FALSE);
                gameUI.setLbTurn(mng.getListYourText().get(gameUI.getRoomID()).equals("X")?"O":"X");
                 if(gameUI.checkWin()){
                     mng.sendResult(KeyWord.RESULT+gameUI.getRoomID()+"-You lose!");//"result:{roomID}-{result}"
                    JOptionPane.showMessageDialog(gameUI,"You win!"); 
                    mng.setUpNewGame(gameUI.getRoomID());
                }else if(gameUI.isFull()){
                     mng.sendResult(KeyWord.RESULT+gameUI.getRoomID()+"-Draw!");
                    JOptionPane.showMessageDialog(gameUI, "Draw!");                  
                        mng.setUpNewGame(gameUI.getRoomID());
                }   
            }
            else{
                JOptionPane.showMessageDialog(gameUI, "Not your turn!");
            }
        }
        
    }
    
//    public void setUpNewGame(){
//        gameUI.drawNewGame();
//        String text=mng.getListYourText().get(gameUI.getRoomID());
//        text=text.equals("X")?"O":"X";
//        mng.getListYourText().put(gameUI.getRoomID(),text);
//        boolean yourTurn=text.equals("X");
//        mng.getListYourTurn().put(gameUI.getRoomID(), yourTurn);
//        gameUI.setLbTurn("X");
//        gameUI.setLbName(mng.getName()+":"+text+"-player");
//    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
}
