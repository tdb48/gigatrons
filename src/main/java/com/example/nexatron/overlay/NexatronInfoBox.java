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
		this.setPriority(OverlayPriority.LOW);
		addMenuEntry(RUNELITE_OVERLAY, "Teleport out", "Nexatron", e -> plugin.teleportOut());
		addMenuEntry(RUNELITE_OVERLAY, "Stop plugin at bank", "Nexatron", e -> plugin.setStopPlugin());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showInfobox())
		{
			return null;
		}
		Stage stage = plugin.nexManager.getStage();
		//String task = plugin.getManager().getCurrentTaskNew();
		String title = "Nexatron";
		Duration duration = Duration.between(plugin.nexManager.overall.botTimer, Instant.now());
		panelComponent.getChildren().clear();
		panelComponent.setBackgroundColor(new Color(16, 24, 32, 75));
		panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(242, 170, 76)).build());
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 100, 0));
		if (plugin.nexManager.nex.teleportOut)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Teleporting out").color(Color.red).build());
		}
		if (plugin.stopPlugin)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Stopping plugin @ Bank").color(Color.ORANGE).build());
		}
		if (plugin.nexManager.overall.botTimer != null)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Runtime ").right((duration.toHours() > 0 ? (duration.toHours() + ":") : ("")) + (new SimpleDateFormat("mm:ss").format(new Date(duration.toMillis())))).build());
		}
		panelComponent.getChildren().add(LineComponent.builder().left("Stage ").right(String.valueOf(stage)).build());
//		panelComponent.getChildren().add(LineComponent.builder().left("Task ").right(task).build());
//		panelComponent.getChildren().add(LineComponent.builder().left("Actions ").right(String.valueOf(plugin.getManager().actionCounter)).build());
		panelComponent.getChildren().add(LineComponent.builder().left("K / D / Fail ").right(plugin.nexManager.overall.killCount + " / " + plugin.nexManager.overall.deaths + " / " + plugin.nexManager.overall.failedKills).build());
		int ancientKcDifference = plugin.nexManager.getAncientKc() - plugin.nexManager.nex.startAncientKc;
		if (ancientKcDifference >= 0)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("KC since start").right("+" + ancientKcDifference).rightColor(Color.GREEN).build());
		}
		else
		{
			panelComponent.getChildren().add(LineComponent.builder().left("KC since start").right("" + ancientKcDifference).rightColor(Color.RED).build());
		}
		if (plugin.nexManager.nex.nex != null)
		{
//			panelComponent.getChildren().add(LineComponent.builder().left("Nex tick ").right(String.valueOf(plugin.nexManager.nex.nexAttackTick)).build());
//			panelComponent.getChildren().add(LineComponent.builder().left("Minion tick ").right(String.valueOf(plugin.nexManager.nex.minionAttackTick)).build());
//			panelComponent.getChildren().add(LineComponent.builder().left("Player tick ").right(String.valueOf(plugin.nexManager.gameTickManager.getAttackWait())).build());
			panelComponent.getChildren().add(LineComponent.builder().left("Should flick ").right(String.valueOf(plugin.nexManager.shouldFlick())).rightColor(plugin.nexManager.shouldFlick() ? Color.GREEN : Color.RED).build());

			if (plugin.nexManager.nex.nextSpecial != null)
			{
//				panelComponent.getChildren().add(LineComponent.builder().left("Attacks til special ").right(String.valueOf(plugin.nexManager.nex.attacksUntilSpecial)).build());
				panelComponent.getChildren().add(LineComponent.builder().left("Next special ").right(String.valueOf(plugin.nexManager.nex.nextSpecial)).build());
			}
//			panelComponent.getChildren().add(LineComponent.builder().left("Zaros counter ").right(String.valueOf(plugin.nexManager.nex.nexZarosAttacks)).build());
			if (plugin.nexManager.containsStage(Stage.MINION_ICE, Stage.NEX_ICE))
			{
				panelComponent.getChildren().add(LineComponent.builder().left("Prison active ").right(String.valueOf(plugin.nexManager.nex.prisonActive)).build());
				panelComponent.getChildren().add(LineComponent.builder().left("Prison tick?").right(String.valueOf(plugin.nexManager.nex.stuckInPrisonTick)).build());
			}
		}
		panelComponent.getChildren().add(LineComponent.builder().left("Idle Time ").right(plugin.nexManager.nex.getIdleTime()).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Rec Idle Time ").right(plugin.nexManager.nex.transformIdleTime(plugin.nexManager.nex.highestIdleTime)).build());

		return panelComponent.render(graphics);
	}

}
