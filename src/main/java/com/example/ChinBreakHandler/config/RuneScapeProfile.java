package com.example.ChinBreakHandler.config;

import lombok.Data;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;

/**
 * A profile/save of a OSRS account. Each account can 1 profile per {@link RuneScapeProfileType}
 * (ie Standard/League/DMM}.
 */
@Data
public class RuneScapeProfile
{
    public static final int ACCOUNT_HASH_INVALID = -1;

    private final String displayName;
    private final RuneScapeProfileType type;
    private final long accountHash;

    /**
     * Profile key used to save configs for this profile to the config store. This will
     * always start with {@link ConfigManager#RSPROFILE_GROUP}
     */
    private final String key;
}
