package com.example.toagigatron.overlay;

import com.example.toagigatron.ToaGigatronConfig;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.puzzlemodel.BabaPuzzleSpecial;
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
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import net.runelite.client.ui.overlay.OverlayLayer;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
@Singleton
public class ToaGigatronInfoBox extends OverlayPanel
{
	private final ToaGigatronPlugin plugin;
	private final ToaGigatronConfig config;

	@Inject
	private ToaGigatronInfoBox(final ToaGigatronPlugin plugin, final ToaGigatronConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_LEFT);
		this.plugin = plugin;
		this.config = config;
		this.setLayer(OverlayLayer.ALWAYS_ON_TOP);
		this.setPriority(OverlayPriority.HIGH);
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "One Click Nex"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showInfobox())
		{
			return null;
		}

		Stage stage = plugin.toaManager.getStage();
		String title = "ToA Megatron";
		Duration duration = Duration.between(plugin.toaManager.overall.botTimer, Instant.now());

		panelComponent.getChildren().clear();
		panelComponent.setBackgroundColor(new Color(16, 24, 32, 150));
		panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(242, 170, 76)).build());
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(title) + 100, 0));
		if (plugin.stopPlugin)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Stopping ASAP").color(Color.red).build());
		}
		if (plugin.finishRaid)
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Finishing current raid").color(Color.ORANGE).build());
		}
		if (plugin.toaManager.overall.botTimer != null)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Runtime ").right((duration.toHours() > 0 ? (duration.toHours() + ":") : ("")) + (new SimpleDateFormat("mm:ss").format(new Date(duration.toMillis())))).build());
		}
		panelComponent.getChildren().add(LineComponent.builder().left("Stage ").right(String.valueOf(stage)).build());
		panelComponent.getChildren().add(LineComponent.builder().left("K/D ").right(plugin.toaManager.overall.killCount + " / " + plugin.toaManager.overall.deaths).build());
		panelComponent.getChildren().add(LineComponent.builder().left("Resigns ").right(String.valueOf(plugin.toaManager.overall.totalResigns)).build());
		if (plugin.toaManager.baba.currentSpecial != BabaPuzzleSpecial.NULL)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Current special ").right(plugin.toaManager.baba.currentSpecial.name()).build());
		}
		if (plugin.toaManager.getStage() == Stage.BABA_BOSS)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Close to proccing: ").right(plugin.toaManager.baba.closeToProccing() + "").build());
		}
		if (plugin.toaManager.getStage() == Stage.KEPHRI_BOSS)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Phase: ").right(plugin.toaManager.kephri.kephriPhase + "").build());
		}
		if (plugin.toaManager.getStage() == Stage.WARDENS_P3)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Skull tick: ").right(plugin.toaManager.wardens3.skullTick + "").build());
		}
		if (plugin.toaManager.getStage() == Stage.WARDENS_P2)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Core tick:  ").right(plugin.toaManager.wardens12.coreTick + "").build());
		}
		if (plugin.toaManager.akkha.puzzle != null && plugin.toaManager.getStage() == Stage.AKKHA_PUZZLE && !plugin.toaManager.akkha.puzzle.layoutName.isEmpty())
		{
			String layout = plugin.toaManager.akkha.puzzle.layoutName;
			layout = layout.replace(".txt", "");
			panelComponent.getChildren().add(LineComponent.builder().left("Current layout ").right(layout).build());
		}
		if (plugin.toaManager.getStage() == Stage.OUTSIDE)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("BP darts ").right(
				(plugin.toaManager.chargesTracker.dartType == null ? "unknown" : plugin.toaManager.chargesTracker.dartType.name)
					+ " x " + plugin.toaManager.chargesTracker.blowpipeDarts).build());
			panelComponent.getChildren().add(LineComponent.builder().left("BP scales ").right(plugin.toaManager.chargesTracker.blowpipeScales + "").build());
			panelComponent.getChildren().add(LineComponent.builder().left("Mage charges ").right(plugin.toaManager.chargesTracker.mageCharges + "").build());
		}
		return panelComponent.render(graphics);
	}

}
