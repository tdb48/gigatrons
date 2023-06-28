package com.example.toagigatron.overlay;

import com.example.toagigatron.ToaGigatronConfig;
import com.example.toagigatron.ToaGigatronPlugin;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class ConsumablesTrackerInfobox extends OverlayPanel {

    private final ToaGigatronPlugin plugin;
    private final ToaGigatronConfig config;


    @Inject
    private ConsumablesTrackerInfobox(final ToaGigatronPlugin plugin, final ToaGigatronConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
        this.setPriority(OverlayPriority.HIGH);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Toa Megatron"));
    }
    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showConsumablesInfobox())
        {
            return null;
        }

        String title = "Supplies - Inv/Bag/Tot";

        panelComponent.getChildren().clear();
        panelComponent.setBackgroundColor(new Color(16, 24, 32, 150));
        panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(242, 170, 76)).build());
        panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 100, 0));

        panelComponent.getChildren().add(LineComponent.builder().left("Ambrosia ").right(plugin.toaManager.consumableTracker.inventoryAmbrosiaDoses +
                " / " + plugin.toaManager.consumableTracker.bagAmbrosiaDoses + " / " + plugin.toaManager.consumableTracker.totalAmbrosiaDoses).build());

        panelComponent.getChildren().add(LineComponent.builder().left("Adrenaline ").right(plugin.toaManager.consumableTracker.inventoryAdrenalineDoses +
                " / " + plugin.toaManager.consumableTracker.bagAdrenalineDoses + " / " + plugin.toaManager.consumableTracker.totalAdrenalineDoses).build());

        panelComponent.getChildren().add(LineComponent.builder().left("Nectar ").right(plugin.toaManager.consumableTracker.inventoryRaidBrewDoses +
                " / " + plugin.toaManager.consumableTracker.bagRaidBrewDoses + " / " + plugin.toaManager.consumableTracker.totalRaidBrewDoses).build());

        panelComponent.getChildren().add(LineComponent.builder().left("Tears ").right(plugin.toaManager.consumableTracker.inventoryRaidRestoreDoses +
                " / " + plugin.toaManager.consumableTracker.bagRaidRestoreDoses + " / " + plugin.toaManager.consumableTracker.totalRaidRestoreDoses).build());

        panelComponent.getChildren().add(LineComponent.builder().left("Salt ").right(plugin.toaManager.consumableTracker.inventorySaltDoses +
                " / " + plugin.toaManager.consumableTracker.bagSaltDoses + " / " + plugin.toaManager.consumableTracker.totalSaltDoses).build());
        panelComponent.getChildren().add(LineComponent.builder().left("Scarab ").right(plugin.toaManager.consumableTracker.inventoryScarabDoses +
                " / " + plugin.toaManager.consumableTracker.bagScarabDoses + " / " + plugin.toaManager.consumableTracker.totalScarabDoses).build());

        return panelComponent.render(graphics);

    }
}
