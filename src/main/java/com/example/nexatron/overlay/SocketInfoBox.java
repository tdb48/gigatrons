package com.example.nexatron.overlay;

import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
@Singleton
public class SocketInfoBox extends OverlayPanel
{
	private final NexatronPlugin plugin;
	private final NexatronConfig config;

	@Inject
	private SocketInfoBox(final NexatronPlugin plugin, final NexatronConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_LEFT);
		this.plugin = plugin;
		this.config = config;
		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
		this.setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showSocketInfobox())
		{
			return null;
		}
		String title = "Socket";

		panelComponent.getChildren().clear();
		panelComponent.setBackgroundColor(new Color(16, 24, 32, 75));
		panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(242, 170, 76)).build());
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 100, 0));

		panelComponent.getChildren().add(LineComponent.builder().left("Master ").right(String.valueOf(plugin.nexManager.socket.isMaster)).rightColor(plugin.nexManager.socket.isMaster ? Color.GREEN : Color.RED).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Other Player ").right(String.valueOf(plugin.nexManager.socket.otherName)).build());
//		panelComponent.getChildren().add(LineComponent.builder().left("Teleport out ").right(String.valueOf(plugin.nexManager.socket.teleportOut)).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Us ready ").right(String.valueOf(plugin.nexManager.socket.readyToStart)).rightColor(plugin.nexManager.socket.readyToStart ? Color.GREEN : Color.RED).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Other Ready ").right(String.valueOf(plugin.nexManager.socket.otherReadyToStart)).rightColor(plugin.nexManager.socket.otherReadyToStart ? Color.GREEN : Color.RED).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Other Inside ").right(String.valueOf(plugin.nexManager.socket.otherIsInside)).rightColor(plugin.nexManager.socket.otherIsInside ? Color.GREEN : Color.RED).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Other World ").right(String.valueOf(plugin.nexManager.socket.otherWorld)).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Need kc ").right(String.valueOf(plugin.nexManager.socket.needKc)).rightColor(plugin.nexManager.socket.needKc ? Color.GREEN : Color.RED).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Other need kc ").right(String.valueOf(plugin.nexManager.socket.otherNeedKc)).rightColor(plugin.nexManager.socket.otherNeedKc ? Color.GREEN : Color.RED).build());

		return panelComponent.render(graphics);
	}

}
