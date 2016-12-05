package nu.te4.ws;

import java.io.IOException;
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
                            (String) userSession.getUserProperties().get("username")+" has left the room."));

        }
    }

    @OnMessage
    public void onMessage(String message, Session userSession) throws IOException {
        String username = (String) userSession.getUserProperties().get("username");

        //if user lacks username, set username as users first message
        if (username == null) {
            userSession.getUserProperties().put("username", message);
            userSession.getBasicRemote().sendText(buildJsonData("system", "You are connected as " + message));

        }else if(message.substring(0, 1).equals("/")){
            System.out.println("Du kommer hit");
            nu.te4.support.Bot
        }
        else {
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
                System.out.println("Error Users "+e.getMessage());
            }

        }
        return jsonArrayBuilder.build().toString();
    }
}
