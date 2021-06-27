package studio.chara.finalproject.controller;

import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/chat/{uuid}")
public class WebSocket {
}
