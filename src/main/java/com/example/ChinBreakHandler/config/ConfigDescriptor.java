package com.example.ChinBreakHandler.config;

import lombok.Getter;

import java.util.Collection;

@Getter
public class ConfigDescriptor
{
    private final ConfigGroup group;
    private final Collection<ConfigSectionDescriptor> sections;
    private final Collection<ConfigTitleDescriptor> titles;
    private final Collection<ConfigItemDescriptor> items;

    public ConfigDescriptor(ConfigGroup group, Collection<ConfigSectionDescriptor> sections, Collection<ConfigTitleDescriptor> titles, Collection<ConfigItemDescriptor> items)
    {
        this.group = group;
        this.sections = sections;
        this.titles = titles;
        this.items = items;
    }
}
