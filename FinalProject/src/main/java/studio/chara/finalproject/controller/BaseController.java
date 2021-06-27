package studio.chara.finalproject.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import studio.chara.finalproject.entity.MeetingRoom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.UUID;

@Controller
public class BaseController {
    public static HashSet<MeetingRoom> meetingRooms = new HashSet<>();

    @GetMapping("/")
    public String index() {
        return "new_meeting";
    }

    @PostMapping("/new-meeting")
    public ResponseEntity<String> newMeeting(@RequestBody String meetingName) {
        for (MeetingRoom meetingRoom : meetingRooms)
            if (meetingRoom.getName().equals(meetingName))
                return new ResponseEntity<>(HttpStatus.CONFLICT);

        UUID uuid = UUID.randomUUID();

        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setUuid(uuid);
        meetingRoom.setName(meetingName);

        meetingRooms.add(meetingRoom);

        return new ResponseEntity<>(uuid.toString(), HttpStatus.CREATED);
    }

    @GetMapping("/{uuid}")
    public String meeting(HttpServletRequest request,
                          HttpServletResponse response,
                          @PathVariable String uuid) {
        MeetingRoom meetingRoom = null;
        for (MeetingRoom item : meetingRooms)
            if (item.getUuid().toString().equals(uuid))
                meetingRoom = item;

        if (meetingRoom == null)
            return null;

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            Cookie cookie = new Cookie("meeting-name", meetingRoom.getName());
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setPath("/" + uuid);
            response.addCookie(cookie);
            return "join_meeting";
        }

        String memberName = null;
        for (Cookie item : cookies)
            if (item.getName().equals("member-name"))
                memberName = item.getValue();

        if (memberName == null) {
            return "join_meeting";
        }

        return "meeting";
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<Void> joinMeeting(HttpServletResponse response,
                                            @PathVariable String uuid,
                                            @RequestBody String memberName) {
        MeetingRoom meetingRoom = null;
        for (MeetingRoom item : meetingRooms)
            if (item.getUuid().toString().equals(uuid))
                meetingRoom = item;

        if (meetingRoom == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!meetingRoom.getMembers().add(memberName))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        Cookie cookie = new Cookie("member-name", memberName);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/" + uuid);
        response.addCookie(cookie);

        return new ResponseEntity<>(HttpStatus.RESET_CONTENT);
    }
}
