package studio.chara.finalproject.entity;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Meeting {
    private String meetingName;
    private Map<String, Member> memberMap = new ConcurrentHashMap<>();
}
