package nu.te4.ws;

import java.io.IOException;
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
import nu.te4.support.Bot;

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
            userSession.getUserProperties().put("username", message);
            userSession.getBasicRemote().sendText(buildJsonData("system", "You are connected as " + message));

            //garanteed errors here. But aint got time for that.
        } else if (message.substring(0, 1).equals("/") && username != null) {
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
                    sendMessageToUser(sender, returnMessage);

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

    //eats json formated as String.
    public int sendMessageToUser(String sender, String jsonData) {
        String returnMessage = Json.createObjectBuilder()
                .add("username", message.substring(msg, message.indexOf(" ")))
                .add("message", message.substring(message.indexOf(" "), message.length()))
                .build().toString();
        return -1;
    }

    public int sendMessageToUsers(String sender, String usernames, String message) {
        String returnMessage = buildJsonData(sender, message);
        ArrayList<String> li = new ArrayList<>();
        int comma;
        while (usernames.contains(",")) {
            comma = usernames.indexOf(",");
            li.add(usernames.substring(0, comma));
            usernames = usernames.substring(comma + 1);
        }
        try {
            for (Session user : sessions) {
                String reciver = (String) user.getUserProperties().get("username");
                for (String recive : li) {
                    if (recive.equals(reciver)) {
                        //user.getBasicRemote().sendText(buildJsonUsers());
                        user.getBasicRemote().sendText(message);
                        return 1;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR; " + e.getMessage());
        }

        return -1;
    }

}
//https://www.youtube.com/watch?v=a8RUmnPL8aQ
