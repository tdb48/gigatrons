package com.example.TestingSuite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
@Singleton
public class TestingSuitePanelOverlay extends OverlayPanel
{

	private final TestingSuitePlugin plugin;
	private final TestingSuiteConfig config;


	@Inject
	private TestingSuitePanelOverlay(final TestingSuitePlugin plugin, final TestingSuiteConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_LEFT);
		this.plugin = plugin;
		this.config = config;
		this.setPriority(OverlayPriority.LOW);
		addMenuEntry(RUNELITE_OVERLAY, "Schedule logout", "Nexatron", e -> plugin.resetNex());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showInfobox())
		{
			return null;
		}

		String title = "Nex debug";
		panelComponent.getChildren().clear();
		panelComponent.setBackgroundColor(new Color(16, 24, 32, 150));
		panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(242, 170, 76)).build());
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 150, 0));
		if (plugin.nex == null)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Nex: ").right("Null").build());
		}
		else
		{
			//Nex id and index
			//panelComponent.getChildren().add(LineComponent.builder().left("ID: ").right(String.valueOf(plugin.nex.getId())).build());
			//panelComponent.getChildren().add(LineComponent.builder().left("Index: ").right(String.valueOf(plugin.nex.getIndex())).build());
			//Nex animation stuff
			panelComponent.getChildren().add(LineComponent.builder().left("Anim: ").right(String.valueOf(plugin.nex.getAnimation())).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Pose Anim: ").right(String.valueOf(plugin.nex.getPoseAnimation())).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Graphic: ").right(String.valueOf(plugin.nex.getGraphic())).build());
			//Interacting
			//Nex tick tracking
			panelComponent.getChildren().add(LineComponent.builder().left("Interacting: ").right(plugin.nexInteracting == null ? "Null" : plugin.nexInteracting.getName()).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Prev. Interacting: ").right(plugin.previousNexInteracting == null ? "Null" : plugin.previousNexInteracting.getName()).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Ticks between swap: ").right(String.valueOf(plugin.ticksBetweenInteractingChanged)).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Attacks between swap:  ").right(String.valueOf(plugin.attacksSinceInteractingChanged)).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Attack(+S) between swap: ").right(String.valueOf(plugin.attacksSinceInteractingChangedIncSpecial)).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Attacks between special: ").right(String.valueOf(plugin.attacksSinceSpecial)).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Previous special: ").right(String.valueOf(plugin.previousSpecial)).build());
			if (plugin.nexOverhead != null)
			{
				panelComponent.getChildren().add(LineComponent.builder().left("Overhead Icon: ").right(plugin.nexOverhead.name()).build());
			}
		}

		return panelComponent.render(graphics);
	}
}
