package nu.te4.ws;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author daca97002
 */
@ServerEndpoint("/chatserver")
public class ChattServerWSEnd {

    static Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void open(Session userSession) {
        sessions.add(userSession);

    }

    @OnClose
    public void close(Session userSession) throws IOException {
        sessions.remove(userSession);
        Iterator<Session> users = sessions.iterator();
        while (users.hasNext()) {
            Session user = users.next();
            user.getBasicRemote().sendText(buildJsonUsers());
            user.getBasicRemote().sendText(
                    buildJsonData("System",
                            (String) userSession.getUserProperties().get("username") + " has left the room."));

        }
    }

    @OnMessage
    public void onMessage(String message, Session userSession) throws IOException {
        String username = (String) userSession.getUserProperties().get("username");

        //if user lacks username, set username as users first message
        if (username == null) {
            //SEE SESSION CONTAINS USERNAMES, AND IF USERNAME ALREADY EXISTS.
            boolean temp = false;
            for (Session user : sessions) {
                String tempUser = (String) user.getUserProperties().get("username");
                if (tempUser != null && tempUser.equals(message)) {
                    userSession.getBasicRemote().sendText(buildJsonData("System", "Username already exists"));
                    temp = true;
                    break;
                }
            }
            //ELSE SET USERNAME AS FIRST MESSAGE
            if (temp == false) {
                userSession.getUserProperties().put("username", message);
                userSession.getBasicRemote().sendText(buildJsonData("System", "You are connected as " + message));
            }
        } //garanteed errors here. But aint got time for that.
        else if ((message.equals("help")) || message.equals("Help")) {
            message = "/HELP";
            checkString(username, message);

        } else if (message.substring(0, 1).equals("/")) {
            System.out.println("hall¨å");
            System.out.println(username + " " + message);
            System.out.println("Du kommer hit");
            int i = checkString(username, message);
            //this is for troubleshooting
            if (i < 0) {
                System.out.println("ERORS in formating (hopefully) " + i);
            }

        } else {
            String returnMessage = buildJsonData(username, message);
            for (Session user : sessions) {
                user.getBasicRemote().sendText(buildJsonUsers());
                user.getBasicRemote().sendText(returnMessage);
            }
        }
    }

    private String buildJsonData(String username, String message) {
        //Creates json as String, formated as: {"username":username, "message":message}
        return Json.createObjectBuilder().add("username", username).add("message", message).build().toString();
    }

    private String buildJsonUsers() {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        Iterator<Session> users = sessions.iterator();
        while (users.hasNext()) {
            try {
                jsonArrayBuilder.add(
                        Json.createObjectBuilder()
                        .add("username", (String) users.next().getUserProperties().get("username"))
                        .build()
                );
            } catch (Exception e) {
                System.out.println("Error Users " + e.getMessage());
            }

        }
        return jsonArrayBuilder.build().toString();
    }

    public int checkString(String sender, String botMessage) {
        System.out.println("Du kommer till check");
        if (botMessage.substring(1, 5).equals("HELP")) {
            System.out.println("DU KOMMER TILL HELPMESSAGE");
            String helpMessage = "\nTo change your username, reload webpage"
                    + "\n\n/USER:username message\n"
                    + "/WHISPER:username,username2,unwieder message\nComma important,username isn't\n"
                    + "/SPAM:secret";
            String returnMessage;
            returnMessage = buildJsonData("Das Bååt", helpMessage);
            System.out.println(returnMessage);
            sendMessageToUser(sender, returnMessage);
        } else if (botMessage.indexOf(":") != -1) {
            System.out.println("Du kommer till en command");
            //end of command (obs SPAM is 2 commands)
            int cmd = botMessage.indexOf(":");
            //Begining of message
            int msg = cmd + 1;
            System.out.println(cmd + " " + msg);
            String check = botMessage.substring(1, msg);
            String message = botMessage.substring(msg, botMessage.length());
            System.out.println(check + " " + message);
            String returnMessage = "";
            String users = "";
            if (check.equals("USER:")) {
                System.out.println("DU KOMMER TILL IFFFFF " + message);
                String reciever = message.substring(0, message.indexOf(" "));
                System.out.println(reciever + " hemsjvejs");
                returnMessage = buildJsonData(sender, message.substring(message.indexOf(" "), message.length()));
                try {
                    System.out.println("Innan försök");
                    int res = sendMessageToUser(reciever, returnMessage);
                    System.out.println("Efter försök");
                    if (res < 0) {
                        System.out.println("User don´t exist, or something " + res);
                    }

                } catch (Exception e) {
                    System.out.println("Error " + e.getMessage());
                    return -1;
                }
                //succeded return 1
                return 1;

                //should plan the strucktural designe of this else if...
            } else if (check.equals("WHISPER:")) {
                System.out.println("Du kommer till whisper");
                try {
                    System.out.println(message);
                    users = message.substring(0, message.indexOf(" "));
                    message = message.substring(message.indexOf(" ") + 1, message.length());
                    returnMessage = buildJsonData(sender, message);
                    sendMessageToUsers(users, returnMessage);
                    System.out.println("Success sålångt");

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    return -1;
                }
                //succeded, return 1
                return 1;

            } else if (check.equals("SPAM:")) {
                //CODELING HERE
            }
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

            //KAN MAN HA EN JSONARRAY TOSTRING?
        } else {
            String message = friendlyBot(botMessage);
            System.out.println("this is from bot: " + message);
            return -2;
        }
        return -10;
    }

    public static String friendlyBot(String greating) {
        return greating;
    }

    //eats json formated as String.
    public int sendMessageToUser(String reciver, String message) {
        System.out.println("du skickar till singularis här");
        System.out.println(reciver);
        for (Session user : sessions) {
            String taker = (String) user.getUserProperties().get("username");
            System.out.println("TAGARE::: " + taker);
            if (taker.equals(reciver)) {
                try {
                    user.getBasicRemote().sendText(message);
                    return 1;

                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    return -1;
                }
                //SEE HOW MUTCH RETURNS NEEDED
            }

        }
        return -2;
    }

    public int sendMessageToUsers(String usernames, String jsonData) {
        System.out.println("Du kommer till users");
        ArrayList<String> li = new ArrayList<>();
        int comma;
        li.add(usernames.substring(0, usernames.indexOf(",")));
        usernames = usernames.substring(usernames.indexOf(","), usernames.length());
        System.out.println("HÄrrr är random " + usernames);
        while (usernames.contains(",")) {
            comma = usernames.indexOf(",");
            usernames = usernames.substring(comma + 1);
            if (usernames.contains(",") == false && usernames.length() > 0) {
                li.add(usernames);
                System.out.println("IF I WHISPERRRR Like Gollumn");
                break;
            }
            li.add(usernames.substring(0, usernames.indexOf(",")));

            System.out.println(usernames);
        }
        try {
            for (Session user : sessions) {
                System.out.println("Du kommer till for");
                String reciver = (String) user.getUserProperties().get("username");
                System.out.println(reciver + "  PRISA GUDARNA");
                for (String recive : li) {
                    System.out.println("MER KOMPLICERAT " + recive);
                    if (recive.equals(reciver)) {

                        //user.getBasicRemote().sendText(buildJsonUsers());
                        user.getBasicRemote().sendText(jsonData);

                    }
                }
            }
            return 1;
        } catch (Exception e) {
            System.out.println("ERROR; " + e.getMessage());
        }

        return -1;
    }

}
//https://www.youtube.com/watch?v=a8RUmnPL8aQ
