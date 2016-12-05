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
import javax.json.JsonObjectBuilder;

/**
 *
 * @author daca97002
 */
public class Bot {

    public static String checkString(String botMessage) {
        if (botMessage.indexOf(":") != -1) {
            //end of command (obs SPAM is 2 commands)
            int cmd = botMessage.indexOf(":");
            //Begining of message
            int msg = cmd + 1;
            String message = "";
            String check = botMessage.substring(0, msg);
            message = botMessage.substring(msg, botMessage.length());

            if (check.equals("USER:")) {
                String returnMessage = Json.createObjectBuilder()
                        .add("username", message.substring(msg,message.indexOf(" ")))
                        .add("message", message.substring(message.indexOf(" "), message.length()))
                                .build().toString();
                return returnMessage;
            }else if (check.equals("WHISPER:")){
                //KAN MAN HA EN JSONARRAY TOSTRING?
            }

            return check;/*exists.add("USER:");
        exists.add("SPAM:");
        exists.add("WHISPER");
        for(String li:exists){
        if (li.equals(check)){
        }
        }*/


        } else {
            String message = friendlyBot(check);
            return message;
        }
    }

    public static String friendlyBot(String greating) {

    }

}
