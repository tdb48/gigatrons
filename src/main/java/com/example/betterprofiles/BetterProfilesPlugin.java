package com.example.betterprofiles;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.security.GeneralSecurityException;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(
        name = "Better Profiles",
        enabledByDefault = false,
        description = "Allow for a allows you to easily switch between multiple OSRS Accounts",
        tags = {"profile", "account", "login", "log in", "pklite"}
)
@Slf4j
public class BetterProfilesPlugin extends Plugin
{
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private BetterProfilesConfig config;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ScheduledExecutorService executorService;

    private BetterProfilesPanel panel;
    private NavigationButton navButton;

    @Provides
    BetterProfilesConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(BetterProfilesConfig.class);
    }

    @Override
    protected void startUp()
    {
        panel = injector.getInstance(BetterProfilesPanel.class);
        panel.init();

        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "profiles_icon.png");

        navButton = NavigationButton.builder()
                .tooltip("Profiles")
                .icon(icon)
                .priority(8)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        if (!config.switchPanel())
        {
            return;
        }
        if (event.getGameState().equals(GameState.LOGIN_SCREEN))
        {
            if (!navButton.isSelected())
            {
                openPanel();
            }
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("betterProfiles"))
        {
            if (event.getKey().equals("rememberPassword"))
            {
                panel = injector.getInstance(BetterProfilesPanel.class);
                this.shutDown();
                this.startUp();
            }
            if (!event.getKey().equals("rememberPassword"))
            {
                panel = injector.getInstance(BetterProfilesPanel.class);
                try
                {
                    panel.redrawProfiles();
                }
                catch (GeneralSecurityException gse)
                {
                    log.error("Error redrawing profiles panel", gse);
                }
            }
        }
    }

    private void openPanel()
    {
        if (config.switchPanel())
        {
            clientThread.invokeLater(() ->
            {
                //todo something w this
//                if (!ClientUI.getFrame().isVisible())
//                {
//                    return false;
//                }

                if (navButton.isSelected())
                {
                    return true;
                }

                SwingUtilities.invokeLater(() -> executorService.submit(() -> navButton.getOnSelect().run()));
                return true;
            });
        }
    }

}