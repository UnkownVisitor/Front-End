package studio.chara.finalproject.entity;

import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data
public class MeetingRoom {
    UUID uuid;
    String name;
    HashSet<String> members;

    public MeetingRoom() {
        members = new HashSet<>();
    }
}
