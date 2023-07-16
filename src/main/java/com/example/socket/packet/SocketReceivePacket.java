package com.example.socket.packet;

import com.example.socket.org.json.JSONObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event triggered by Socket, notifying plugins that a packet has been received.
 * This event is triggered on the client thread.
 */
@AllArgsConstructor
public class SocketReceivePacket {

    @Getter(AccessLevel.PUBLIC)
    private JSONObject payload;

}
