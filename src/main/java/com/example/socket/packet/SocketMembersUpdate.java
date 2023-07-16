package com.example.socket.packet;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event triggered when the list of party members is updated.
 */
@AllArgsConstructor
public class SocketMembersUpdate
{

    @Getter(AccessLevel.PUBLIC)
    private List<String> members;

}