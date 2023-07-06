package com.example.ChinBreakHandler.config;

import java.lang.reflect.Type;
import lombok.Value;


@Value
public class ConfigItemDescriptor implements ConfigObject
{
    private final ConfigItem item;
    private final Type type;
    private final Range range;
    private final Alpha alpha;
    private final Units units;

    @Override
    public String key()
    {
        return item.keyName();
    }

    @Override
    public String name()
    {
        return item.name();
    }

    @Override
    public int position()
    {
        return item.position();
    }
}
