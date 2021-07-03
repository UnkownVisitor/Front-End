package studio.chara.finalproject.server;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import studio.chara.finalproject.entity.Meeting;
import studio.chara.finalproject.entity.Member;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        sendMemberList();
    }

    @OnClose
    public void onClose() {
        Meeting meeting = meetingMap.get(uuid);
        if (meeting == null) {
            sendError();
            return;
        }
        Member member = meeting.getMemberMap().get(sessionID);
        if (member == null) {
            sendError();
            return;
        }
        member.setWebSocketServer(null);
    }

    @OnMessage
    public void onMessage(String json) {
        Meeting meeting = meetingMap.get(uuid);
        if (meeting == null) {
            sendError();
            return;
        }
        Member member = meeting.getMemberMap().get(sessionID);
        if (member == null) {
            sendError();
            return;
        }
        for (Member each : meeting.getMemberMap().values())
            if (!each.equals(member)) {
                WebSocketServer server = each.getWebSocketServer();
                if (server != null)
                    server.sendText(json);
            }

    }

    public void sendText(String text) {
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

    public void sendMemberList() {
        Meeting meeting = meetingMap.get(uuid);
        if (meeting == null) {
            sendError();
            return;
        }

        List<String> memberList = new ArrayList<>();

        for (Member member : meeting.getMemberMap().values())
            memberList.add(member.getMemberName());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "memberList");
        jsonObject.put("memberList", memberList);

        sendText(jsonObject.toString());
    }
}
