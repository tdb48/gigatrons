package com.example.socket.packet;

import com.example.socket.org.json.JSONObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event triggered by alternative plugins, broadcasting a packet to the server.
 */
@AllArgsConstructor
public class SocketBroadcastPacket {

    @Getter(AccessLevel.PUBLIC)
    private JSONObject payload;

}
