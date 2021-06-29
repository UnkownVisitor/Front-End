package studio.chara.finalproject.entity;

import lombok.Data;
import studio.chara.finalproject.server.WebSocketServer;

@Data
public class Member {
    private String memberName;
    private WebSocketServer webSocketServer;
}
