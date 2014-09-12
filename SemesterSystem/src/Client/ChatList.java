/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author frederikolesen
 */
public interface ChatList {

    void onlineArrived(String data);

    void messageArrived(String userName, String data);
}
