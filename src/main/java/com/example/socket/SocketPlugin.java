/*
 * Copyright (c) 2020, Charles Xu <github.com/kthisiscvpv>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.socket;

import com.example.socket.hash.AES256;
import com.example.socket.org.json.JSONArray;
import com.example.socket.org.json.JSONObject;
import com.example.socket.packet.SocketBroadcastPacket;
import com.example.socket.packet.SocketPlayerJoin;
import com.example.socket.packet.SocketPlayerLeave;
import com.example.socket.packet.SocketReceivePacket;
import com.google.inject.Provides;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.kit.KitType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
        name = "Socket",
        description = "Socket connection for broadcasting messages across clients.",
        tags = {"socket", "server", "discord", "connection", "broadcast"},
        enabledByDefault = false
)
public class SocketPlugin extends Plugin {

    // Config version changes between updates, hence we use a global variable.
    public static final String CONFIG_VERSION = "Socket Plugin v2.0.5";

    // To help users who decide to use weak passwords.
    public static final String PASSWORD_SALT = "REPLACE_WITH_YOUR_OWN_SALT";

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private Client client;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private EventBus eventBus;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private ClientThread clientThread;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private SocketConfig config;

    @Provides
    SocketConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketConfig.class);
    }

    // This variables controls the next UNIX epoch time to establish the next connection.
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    private long nextConnection;

    // This variables controls the current active connection.
    private SocketConnection connection = null;

    @Override
    protected void startUp() {
        this.nextConnection = 0L;

        eventBus.register(SocketReceivePacket.class);
        eventBus.register(SocketBroadcastPacket.class);

        eventBus.register(SocketPlayerJoin.class);
        eventBus.register(SocketPlayerLeave.class);
    }

    @Override
    protected void shutDown() {
        eventBus.unregister(SocketReceivePacket.class);
        eventBus.unregister(SocketBroadcastPacket.class);

        eventBus.unregister(SocketPlayerJoin.class);
        eventBus.unregister(SocketPlayerLeave.class);

        if (this.connection != null)
            this.connection.terminate(true);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Attempt connecting, or re-establishing connection to the socket server, only when the user is logged in.
        if (client.getGameState() == GameState.LOGGED_IN) {
            statCheckOnGameTick();
            if (this.connection != null) { // If an connection is already being established, ignore.
                SocketState state = this.connection.getState();
                if (state == SocketState.CONNECTING || state == SocketState.CONNECTED)
                    return;
            }

            if (System.currentTimeMillis() >= this.nextConnection) { // Create a new connection.
                this.nextConnection = System.currentTimeMillis() + 30000L;
                this.connection = new SocketConnection(this, this.client.getLocalPlayer().getName());
                new Thread(this.connection).start(); // Handler blocks, so run it on a separate thread.
            }

        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        // Notify the user to restart the plugin when the config changes.
        if (event.getGroup().equals(CONFIG_VERSION))
            this.clientThread.invoke(() -> this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=b4281e>Configuration changed. Please restart the plugin to see updates.", null));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        // Terminate all connections to the socket server when the user logs out.
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            if (this.connection != null)
                this.connection.terminate(false);
        }
    }

    @Subscribe
    public void onSocketBroadcastPacket(SocketBroadcastPacket packet) {
        try {
            // Handles the packets that alternative plugins broadcasts.
            if (this.connection == null || this.connection.getState() != SocketState.CONNECTED)
                return;

            String data = packet.getPayload().toString();
            log.debug("Deploying packet from client: {}", data);

            String secret = this.config.getPassword() + PASSWORD_SALT;

            JSONObject payload = new JSONObject();
            payload.put("header", SocketPacket.BROADCAST);
            payload.put("payload", AES256.encrypt(secret, data)); // Payload is now an encrypted string.

            PrintWriter outputStream = this.connection.getOutputStream();
            synchronized (outputStream) {
                outputStream.println(payload.toString());
            }
        } catch (Exception e) { // Oh no, something went wrong!
            e.printStackTrace();
            log.error("An error has occurred while trying to broadcast a packet.", e);
        }
    }
    private void statCheckOnGameTick() {
        if (client == null || client.getLocalPlayer() == null) {
            return;
        }
        if (deferredCheck != null && client.getTickCount() == deferredCheck.getTick())
        {
            checkStats();
            deferredCheck = null;

        }
    }
    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        onCheckAnimationChanged(event);
    }



    @AllArgsConstructor
    @Data
    public static class DeferredCheck {
        private int tick;
        private int anim;
        private int wep;
        private boolean piety;
    }

    private DeferredCheck deferredCheck;

    private boolean isInRegion(int regionID){
        List<Integer> regions = Arrays.asList(12631,13125,13122,13123,13379,12612,12611,12867);

        if(regions.contains(regionID)){
            return true;
        }
        return false;
    }

    private boolean nyloSlaveInteracting(NPC target) {
        if (target != null &&
                target.getName() != null){
            if (target.getName().toLowerCase().contains("nylocas")) {
                if (target.getName().toLowerCase().contains("vasil")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean isOtherBow(int i) {
        int[] e = {
                ItemID.MAGIC_SHORTBOW,
                ItemID.MAGIC_SHORTBOW_I,
                ItemID.CRAWS_BOW,
                ItemID.CRAWS_BOW_U
        };
        for (int i2 : e)
            if (i2 ==i)
                return true;
        return false;
    }

    private void checkStats() {
        int anim = deferredCheck.getAnim();
        int hammerBop = 401;
        int godBop = 7045;
        int bow = 426;
        int clawSpec = 7514;
        int clawBop = 393;
        int whip = 1658;
        int chalyBop =440;
        int chalySpec = 1203;
        int scy = 8056;
        int bggsSpec = 7643;
        int bggsSpec2 = 7642;
        int hammerSpec = 1378;
        int del = 1100;
        int lanceSmack = 8290;
        int lancePoke = 8288;
        int[] hits = {lancePoke, lanceSmack, clawSpec, clawBop, whip, chalySpec, scy, bggsSpec, bggsSpec2, hammerSpec};
        for (int i : hits) {
            if (anim == i) {
                int lvl = this.client.getBoostedSkillLevel(Skill.STRENGTH);
                boolean piety = deferredCheck.isPiety();
                boolean is118 = (lvl == 118 || lvl == 120);
                if (!piety || !is118) {
                    String s = "attacked";
                    if (i == clawSpec)
                        s = "claw speced";
                    else if (i == chalySpec)
                        s = "chaly speced";
                    else if (i == bggsSpec || i == bggsSpec2)
                        s = "bgs speced";
                    else if (i == hammerSpec)
                        s = "hammer speced";
                    String s2 = "";
                    if (!piety) {
                        if (!is118) {
                            s2 = " with " + lvl + " strength and without piety.";
                        } else {
                            s2 = " without piety.";
                        }
                    } else if (!is118) {
                        s2 = " with " + lvl + " strength.";
                    }
                    flagMesOut("You " + s + s2);
                }
                break;
            }
        }
    }

    public static int getCurrentRegionID(Client client) {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return -1;
        } else {
            WorldPoint wp = WorldPoint.fromLocalInstance(client, localPlayer.getLocalLocation());
            return wp == null ? -1 : wp.getRegionID();
        }
    }

    private void onCheckAnimationChanged(AnimationChanged event) {
        if (event == null)
            return;
        if (event.getActor() instanceof Player) {
            Player p = (Player) event.getActor();
            if (p == null)
                return;
            int anim = p.getAnimation();
            if (p.getPlayerComposition() == null)
                return;
            int wep = p.getPlayerComposition().getEquipmentId(KitType.WEAPON);
            int hammerBop = 401;
            int godBop = 7045;
            int bow = 426;
            int lanceSmack = 8290;
            int lancePoke = 8288;
            int clawSpec = 7514;
            int clawBop = 393;
            int whip = 1658;
            int chalyBop =440;
            int chalySpec = 1203;
            int scy = 8056;
            int bggsSspec = 7643;
            int hammerSpec = 1378;
            int trident = 1167;
            int surge = 7855;
            Actor interacting = p.getInteracting();
            NPC target = null;
            if(p.getInteracting() != null && p.getInteracting() instanceof NPC){
                target = (NPC) interacting;
            }
            if (p.equals(this.client.getLocalPlayer())) {
                if (anim != 0 && anim != -1) {
                    if (!nyloSlaveInteracting(target)) {
                        int style = client.getVar(VarPlayer.ATTACK_STYLE);
                        if (anim == scy) {
                            String b = "";
                            if(style == 0){
                                b = "accurate";
                            }
                            else if(style == 2){
                                b = "crush";
                            }
                            else if(style == 3){
                                b = "defensive";
                            }
                            //String a = style == 0 ? "accurate" : style == 2 ? "crush" : "defensive";
                            if(!b.equals("")){
                                if(isInRegion(getCurrentRegionID(client))){
                                    //in tob we want to print out everything
                                    flagMesOut("You scythed on " +b +".");
                                } else {
                                    if(!b.equals("crush")){
                                        flagMesOut("You scythed on " +b +".");
                                    }
                                }
                            }




                        }
                        else if (anim == bow && (!isOtherBow(wep)) && !this.client.isPrayerActive(Prayer.RIGOUR)) {
                            flagMesOut("You bowed without rigour active.");
                        }
                        else if (anim == hammerBop && wep == ItemID.DRAGON_WARHAMMER) {
                            flagMesOut("You hammer bopped.");
                        } else if (anim == godBop) {
                            flagMesOut("You godsword bopped.");
                        } else if (anim == chalyBop) {
                            flagMesOut("You chaly poked.");
                        }
                    }
                    deferredCheck = new DeferredCheck(client.getTickCount(), anim, wep, this.client.isPrayerActive(Prayer.PIETY));
                }
            }
        }
    }

    private void flagMesOut(String mes) {
        if (client == null || client.getLocalPlayer() == null || client.getLocalPlayer().getName() == null) {
            return;
        }
        String finalS = mes.toLowerCase().replaceAll("you ", client.getLocalPlayer().getName()+" ");
        JSONArray data = new JSONArray();
        JSONObject json$ = new JSONObject();
        json$.put("print", finalS);
        json$.put("sender", client.getLocalPlayer().getName());
        int[] mapRegions = client.getMapRegions() == null ? new int[0] : client.getMapRegions();
        json$.put("mapregion", Arrays.toString(mapRegions));
        json$.put("raidbit", client.getVar(Varbits.IN_RAID));
        data.put(json$);
        JSONObject send = new JSONObject();
        send.put("sLeech", data);
        eventBus.post(new SocketBroadcastPacket(send));
    }

}
