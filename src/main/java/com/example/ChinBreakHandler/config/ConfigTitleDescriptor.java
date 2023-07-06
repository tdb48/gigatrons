package com.example.ChinBreakHandler.config;

import lombok.Value;

@Value
public class ConfigTitleDescriptor implements ConfigObject
{
    private final String key;
    private final ConfigTitle title;

    @Override
    public String key()
    {
        return key;
    }

    @Override
    public String name()
    {
        return title.name();
    }

    @Override
    public int position()
    {
        return title.position();
    }
}
