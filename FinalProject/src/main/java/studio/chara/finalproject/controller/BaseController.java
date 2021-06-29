package studio.chara.finalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import studio.chara.finalproject.entity.Member;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static studio.chara.finalproject.controller.JsonController.httpSessionMap;

@Controller
public class BaseController {
    @GetMapping("/")
    public String index() {
        return "new_meeting";
    }

    @GetMapping("/{uuid}")
    public String meeting(
            @PathVariable("uuid") String uuid,
            @CookieValue(value = "sessionID", required = false) String sessionID,
            HttpServletResponse response) {
        if (sessionID == null)
            return "join_meeting";

        HttpSession httpSession = httpSessionMap.get(sessionID);
        if (httpSession == null) {
            Cookie cookie = new Cookie("sessionID", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "join_meeting";
        }

        Member member = (Member) httpSession.getAttribute(uuid);
        if (member == null)
            return "join_meeting";

        return "meeting";
    }
}
