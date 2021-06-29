package studio.chara.finalproject.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.chara.finalproject.entity.Meeting;
import studio.chara.finalproject.entity.Member;
import studio.chara.finalproject.server.WebSocketServer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class JsonController {
    public static Map<String, HttpSession> httpSessionMap = new ConcurrentHashMap<>();
    public static Map<String, Meeting> meetingMap = new ConcurrentHashMap<>();

    @PostMapping("/")
    public ResponseEntity<String> newMeeting(
            @RequestBody String json) {
        JSONObject requestBody = new JSONObject(json);
        String meetingName = requestBody.getString("meetingName");

        String uuid = UUID.randomUUID().toString();
        Meeting meeting = new Meeting();
        meeting.setMeetingName(meetingName);

        meetingMap.put(uuid, meeting);

        JSONObject responseBody = new JSONObject();
        responseBody.put("uuid", uuid);

        return new ResponseEntity<>(responseBody.toString(), HttpStatus.CREATED);
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<Void> joinMeeting(
            @PathVariable("uuid") String uuid,
            @RequestBody String json,
            HttpServletRequest request,
            HttpServletResponse response) {
        JSONObject requestBody = new JSONObject(json);
        Meeting meeting = meetingMap.get(uuid);

        if (meeting == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        String memberName = requestBody.getString("memberName");

        if (memberName == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        HttpSession httpSession = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals("sessionID")) {
                    String sessionID = cookie.getValue();
                    httpSession = httpSessionMap.get(sessionID);
                }

        if (httpSession == null) {
            httpSession = request.getSession();
            String sessionID = httpSession.getId();
            httpSessionMap.put(sessionID, httpSession);
            Cookie cookie = new Cookie("sessionID", sessionID);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);
        }

        Member member = new Member();
        member.setMemberName(memberName);

        meeting.getMemberMap().put(httpSession.getId(), member);
        httpSession.setAttribute(uuid, member);

        for (Map.Entry<String, Member> entry : meeting.getMemberMap().entrySet())
            if (!entry.getKey().equals(httpSession.getId())) {
                Member dest = entry.getValue();
                WebSocketServer server = dest.getWebSocketServer();
                if (server != null)
                    server.sendJoin(memberName);
            }
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{uuid}/getMeetingName")
    public ResponseEntity<String> getMeetingName(
            @PathVariable("uuid") String uuid) {
        Meeting meeting = meetingMap.get(uuid);

        if (meeting == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        JSONObject responseBody = new JSONObject();
        responseBody.put("meetingName", meeting.getMeetingName());

        return new ResponseEntity<>(responseBody.toString(), HttpStatus.OK);
    }
}
