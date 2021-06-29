package studio.chara.finalproject.server;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import studio.chara.finalproject.entity.Meeting;
import studio.chara.finalproject.entity.Member;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static studio.chara.finalproject.controller.JsonController.meetingMap;

@Component
@ServerEndpoint("/{uuid}/{sessionID}")
public class WebSocketServer {
    private Session session;
    private String uuid;
    private String sessionID;

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("uuid") String uuid,
            @PathParam("sessionID") String sessionID) {
        this.session = session;

        Meeting meeting = meetingMap.get(uuid);
        if (meeting == null) {
            sendError();
            return;
        }
        this.uuid = uuid;

        Member member = meeting.getMemberMap().get(sessionID);
        if (member == null) {
            sendError();
            return;
        }
        this.sessionID = sessionID;

        member.setWebSocketServer(this);
    }

    private void sendText(String text) {
        session.getAsyncRemote().sendText(text);
    }

    public void sendError() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "error");
        sendText(jsonObject.toString());
    }

    public void sendJoin(String memberName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "join");
        jsonObject.put("memberName", memberName);
        sendText(jsonObject.toString());
    }
}
