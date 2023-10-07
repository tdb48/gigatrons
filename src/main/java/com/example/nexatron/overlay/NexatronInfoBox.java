package com.example.nexatron.overlay;

import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.model.constants.Stage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
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
public class NexatronInfoBox extends OverlayPanel
{
	private final NexatronPlugin plugin;
	private final NexatronConfig config;

	@Inject
	private NexatronInfoBox(final NexatronPlugin plugin, final NexatronConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_LEFT);
		this.plugin = plugin;
		this.config = config;
//		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
		this.setPriority(OverlayPriority.LOW);
		addMenuEntry(RUNELITE_OVERLAY, "TELEPORT OUT", "Nexatron", e -> plugin.teleportOut());

	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showInfobox())
		{
			return null;
		}
		Stage stage = plugin.nexManager.getStage();
		String title = "Nexatron";
		Duration duration = Duration.between(plugin.nexManager.overall.botTimer, Instant.now());
		panelComponent.getChildren().clear();
		panelComponent.setBackgroundColor(new Color(16, 24, 32, 75));
		panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(242, 170, 76)).build());
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 100, 0));
		if (plugin.nexManager.nex.teleportOut)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("TELEPORTING OUT").color(Color.red).build());
		}
		if (plugin.stopPlugin)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Stopping ASAP").color(Color.red).build());
		}
		if (plugin.finishKill)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Finishing current kill").color(Color.ORANGE).build());
		}
		if (plugin.nexManager.overall.botTimer != null)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Runtime ").right((duration.toHours() > 0 ? (duration.toHours() + ":") : ("")) + (new SimpleDateFormat("mm:ss").format(new Date(duration.toMillis())))).build());
		}
		panelComponent.getChildren().add(LineComponent.builder().left("Stage ").right(String.valueOf(stage)).build());
		panelComponent.getChildren().add(LineComponent.builder().left("K/D/F ").right(plugin.nexManager.overall.killCount + " / " + plugin.nexManager.overall.deaths + " / " + plugin.nexManager.overall.failedKills).build());
		if (plugin.nexManager.nex.nex != null)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Overhead int ").right(String.valueOf(plugin.nexManager.getOverheadIcon(plugin.nexManager.nex.nex))).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Nex tick ").right(String.valueOf(plugin.nexManager.nex.nexAttackTick)).build());
//			panelComponent.getChildren().add(LineComponent.builder().left("Minion tick ").right(String.valueOf(plugin.nexManager.nex.minionAttackTick)).build());
//			panelComponent.getChildren().add(LineComponent.builder().left("Player tick ").right(String.valueOf(plugin.nexManager.gameTickManager.getAttackWait())).build());

//			if (plugin.nexManager.nex.nextSpecial != null)
//			{
//				panelComponent.getChildren().add(LineComponent.builder().left("attacks until spec ").right(String.valueOf(plugin.nexManager.nex.attacksUntilSpecial)).build());
//				panelComponent.getChildren().add(LineComponent.builder().left("Next special ").right(String.valueOf(plugin.nexManager.nex.nextSpecial)).build());
//			}
//			panelComponent.getChildren().add(LineComponent.builder().left("Zaros counter ").right(String.valueOf(plugin.nexManager.nex.nexZarosAttacks)).build());
			if (plugin.nexManager.containsStage(Stage.MINION_ICE, Stage.NEX_ICE))
			{
				panelComponent.getChildren().add(LineComponent.builder().left("Prison active ").right(String.valueOf(plugin.nexManager.nex.prisonActive)).build());
				panelComponent.getChildren().add(LineComponent.builder().left("Prison tick?").right(String.valueOf(plugin.nexManager.nex.stuckInPrisonTick)).build());

			}
		}
		return panelComponent.render(graphics);
	}

}
