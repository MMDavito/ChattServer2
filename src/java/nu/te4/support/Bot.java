/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nu.te4.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author daca97002
 */

//THIS CLASS SHOULD CONTAIN ELIMINATE ALL ERRORS CAUSED BY USERINPUT-ERRORS
public class Bot {

    public int checkString(String sender, String botMessage) {
        if (botMessage.indexOf(":") != -1) {
            //end of command (obs SPAM is 2 commands)
            int cmd = botMessage.indexOf(":");
            //Begining of message
            int msg = cmd + 1;
            String check = botMessage.substring(0, msg);
            String message = botMessage.substring(msg, botMessage.length());
            String returnMessage = "";
            String users = "";
            if (check.equals("USER:")) {
                returnMessage = Json.createObjectBuilder()
                        .add("username", message.substring(msg, message.indexOf(" ")))
                        .add("message", message.substring(message.indexOf(" "), message.length()))
                        .build().toString();
                try {
                    nu.te4.ws.ChattServerWSEnd.sendMessageToUser(sender, returnMessage);

                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                    return -1;
                }
                //succeded return 1
                return 1;

                //should plan the strucktural designe of this else if...
            } else if (check.equals("WHISPER:")) {
                try {
                    users = message.substring(0, message.indexOf("/"));
                    returnMessage = message.substring(message.indexOf("/") + 1, message.length());
                    nu.te4.ws.ChattServerWSEnd.sendMessageToUsers(sender, users, returnMessage);

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return -1;
                }
                //succeded, return 1
                return 1;

            }
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

            //KAN MAN HA EN JSONARRAY TOSTRING?
        } else {
            String message = friendlyBot(botMessage);
            System.out.println("this is from bot: " + message);
            return -2;
        }
        return -20;
    }

    public static String friendlyBot(String greating) {
        return greating;
    }

}
//https://www.youtube.com/watch?v=a8RUmnPL8aQ
