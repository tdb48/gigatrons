package com.example.TestingSuite;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDependency(EthanApiPlugin.class)
@PluginDescriptor(name = "Testing Suite", description = "", enabledByDefault = false, tags = {"Testing"})
@Slf4j
public class TestingSuitePlugin extends Plugin
{

	@Inject
	Client client;
	@Inject
	EthanApiPlugin api;
	@Inject
	TestingSuiteConfig config;
	@Inject
	TestingSuiteOverlay overlay;
	@Inject
	TestingSuitePanelOverlay overlayPanel;
	@Inject
	OverlayManager overlayManager;
	@Inject
	ClientThread clientThread;
	@Inject
	EventBus eventBus;

	//Smoke
	public String SMOKE_SPAWN_MSG = "Fill my soul with smoke!";
	public String COUGH_SPECIAL_MSG = "Let the virus flow through you!";
	public String DASH_SPECIAL_MSG = "There is...";
	public String FUMUS_SPAWN_MSG = "Fumus, don't fail me!";

	//Shadow
	public String SHADOW_SPAWN_MSG = "Darken my shadow!";
	public String SHADOW_DARKNESS_SPECIAL_MSG = "Embrace darkness!";
	public String SHADOW_POOL_SPECIAL_MSG = "Fear the shadow!";
	public String UMBRA_SPAWN_MSG = "Umbra, don't fail me!";

	//Blood
	public String BLOOD_SPAWN_MSG = "Flood my lungs with blood!";
	public String BLOOD_SIPHON_SPECIAL_MSG = "A siphon will solve this!";
	public String BLOOD_SACRIFICE_SPECIAL_MSG = "I demand a blood sacrifice!";
	public String CRUOR_SPAWN_MSG = "Cruor, don't fail me!";

	//Ice
	public String ICE_SPAWN_MSG = "Infuse me with the power of ice!";
	public String ICE_PRISON_SPECIAL_MSG = "Die now, in a prison of ice!";
	public String ICE_CONTAIN_SPECIAL_MSG = "Contain this!";
	public String GLACIES_SPAWN_MSG = "Glacies, don't fail me!";

	//Zaros
	public String ZAROS_SPAWN_MSG = "NOW, THE POWER OF ZAROS!";

	public ArrayList<String> specialAttackMessages = new ArrayList<>(List.of(
		COUGH_SPECIAL_MSG,
		DASH_SPECIAL_MSG,
		SHADOW_DARKNESS_SPECIAL_MSG,
		SHADOW_POOL_SPECIAL_MSG,
		BLOOD_SIPHON_SPECIAL_MSG,
		BLOOD_SACRIFICE_SPECIAL_MSG,
		ICE_PRISON_SPECIAL_MSG,
		ICE_CONTAIN_SPECIAL_MSG
	));

	public NPC nex = null;
	public HeadIcon nexOverhead = null;
	public Actor nexInteracting = null;
	public Actor previousNexInteracting = null;
	public int nexAnimation;
	public int nexPoseAnimation;
	public int nexGraphic;

	public int tickWhenInteractingChanged;

	public int ticksBetweenInteractingChanged;

	public String previousSpecial = "";

	public int attacksSinceSpecial;
	public int attacksSinceInteractingChanged;
	public int attacksSinceInteractingChangedIncSpecial;

	public int nexShadowChargeAnim;
	public int nexShadowChargeGraphic;
	public int nexShadowChargePoseAnim;
	public int nexShadowResetToMiddleAnim;
	public int nexShadowResetToMiddlePoseAnim;
	public int nexShadowResetToMiddleGraphic;

	@Override
	@SneakyThrows
	public void startUp()
	{
		this.overlayManager.add(overlay);
		this.overlayManager.add(overlayPanel);
		nex = null;
		nexInteracting = null;
		nexAnimation = -1;
		nexPoseAnimation = -1;
		nexGraphic = -1;
		tickWhenInteractingChanged = -1;
		ticksBetweenInteractingChanged = -1;
		previousSpecial = "";
		attacksSinceSpecial = -1;
		nexShadowChargeAnim = -1;
		nexShadowChargeGraphic = -1;
		nexShadowChargePoseAnim = -1;
		nexShadowResetToMiddleAnim = -1;
		nexShadowResetToMiddlePoseAnim = -1;
		nexShadowResetToMiddleGraphic = -1;
		nexOverhead = null;
		previousNexInteracting = null;
		attacksSinceInteractingChanged = -1;
		attacksSinceInteractingChangedIncSpecial = -1;
	}

	public void resetNex()
	{
		nex = null;
		nexInteracting = null;
		nexAnimation = -1;
		nexPoseAnimation = -1;
		nexGraphic = -1;
		tickWhenInteractingChanged = -1;
		ticksBetweenInteractingChanged = -1;
		previousSpecial = "";
		attacksSinceSpecial = -1;
		nexShadowChargeAnim = -1;
		nexShadowChargeGraphic = -1;
		nexShadowChargePoseAnim = -1;
		nexShadowResetToMiddleAnim = -1;
		nexShadowResetToMiddlePoseAnim = -1;
		nexShadowResetToMiddleGraphic = -1;
		nexOverhead = null;
		previousNexInteracting = null;
		attacksSinceInteractingChanged = -1;
		attacksSinceInteractingChangedIncSpecial = -1;
	}

	@Override
	public void shutDown()
	{
		this.overlayManager.remove(overlay);
		this.overlayManager.remove(overlayPanel);
	}

	@Provides
	public TestingSuiteConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TestingSuiteConfig.class);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{

	}

	@Subscribe
	public void onGroundObjectSpawned(GroundObjectSpawned event)
	{

	}


	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{

	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String msg = event.getMessage();
		String spec = determineSpecial(msg);
		if (spec.length() > 0)
		{
			attacksSinceSpecial = 0;
			previousSpecial = spec;
			attacksSinceInteractingChangedIncSpecial++;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc() == null || event.getNpc().getName() == null)
		{
			return;
		}
		if (event.getNpc().getName().toLowerCase().contains("nex"))
		{
			if (nex != null)
			{
				//print("New nex has spawned while old nex is not null.");
				//print("Old nex -> " + nex.getId() + ", index -> " + nex.getIndex());
				//print("New nex -> " + nex.getId() + ", index -> " + nex.getIndex());
			}
			else
			{
				//print("Nex spawned -> " + event.getNpc().getId() + ", index -> " + event.getNpc().getIndex());
			}
			nex = event.getNpc();
		}

	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (nex == null)
		{
			return;
		}
		if (event.getNpc() == null || event.getNpc().getName() == null)
		{
			return;
		}
		if (event.getNpc().getName().equals(nex.getName()))
		{
			//print("Nex despawned, stored nex id -> " + nex.getId() + " Despawned nex id -> " + event.getNpc().getId());
			nex = null;
			resetNex();
		}

	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (nex == null)
		{
			return;
		}
		Actor a = event.getActor();
		if (!(a instanceof NPC))
		{
			return;
		}
		NPC n = (NPC) event.getActor();
		if (n.getIndex() != nex.getIndex())
		{
			return;
		}
		//normal nex attack
		//9180 = melee
		//9189 = shadow range
		//9188 = blood/ice mage
		//9184 = dying
		if (n.getAnimation() == 9189 || n.getAnimation() == 9188 || n.getAnimation() == 9180)
		{
			attacksSinceSpecial++;
			attacksSinceInteractingChanged++;
			attacksSinceInteractingChangedIncSpecial++;
		}

	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (nex == null)
		{
			return;
		}
		if (!nex.getLocalLocation().isInScene() || !nex.getWorldLocation().isInScene(client))
		{
			return;
		}

		try
		{
			if (EthanApiPlugin.getHeadIcon(nex) != null)
			{
				nexOverhead = EthanApiPlugin.getHeadIcon(nex);
			}
			else
			{
				nexOverhead = null;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			nexOverhead = null;
		}

		if (nex.isInteracting())
		{
			if (nexInteracting == null || !nex.getInteracting().equals(nexInteracting))
			{
				if (tickWhenInteractingChanged <= 0)
				{
					ticksBetweenInteractingChanged = 0;
				}
				else
				{
					ticksBetweenInteractingChanged = client.getTickCount() - tickWhenInteractingChanged;
				}
				attacksSinceInteractingChanged = 1;
				attacksSinceInteractingChangedIncSpecial = 1;
				previousNexInteracting = nexInteracting;
				nexInteracting = nex.getInteracting();
				tickWhenInteractingChanged = client.getTickCount();
			}
		}
		if (!nex.isInteracting() && nexInteracting != null)
		{
			previousNexInteracting = nexInteracting;
			nexInteracting = null;
		}
		if (nex.getAnimation() != nexAnimation)
		{
			nexAnimation = nex.getAnimation();
		}
		if (nex.getPoseAnimation() != nexPoseAnimation)
		{
			nexPoseAnimation = nex.getPoseAnimation();
		}
		if (nex.getGraphic() != nexGraphic)
		{
			nexGraphic = nex.getGraphic();
		}


	}


	@Subscribe
	public void onGameTick(GameTick event)
	{

	}

	public void print(String msg)
	{

		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, "");

	}

	public String determineSpecial(String msg)
	{
		String returnStr = "";
		if (msg.contains(COUGH_SPECIAL_MSG))
		{
			returnStr = "Cough";
		}
		else if (msg.contains(DASH_SPECIAL_MSG))
		{
			returnStr = "Dash";
		}
		else if (msg.contains(SHADOW_DARKNESS_SPECIAL_MSG))
		{
			returnStr = "Darkness";
		}
		else if (msg.contains(SHADOW_POOL_SPECIAL_MSG))
		{
			returnStr = "Shadow Pools";
		}
		else if (msg.contains(BLOOD_SIPHON_SPECIAL_MSG))
		{
			returnStr = "Siphon";
		}
		else if (msg.contains(BLOOD_SACRIFICE_SPECIAL_MSG))
		{
			returnStr = "Sacrifice";
		}
		else if (msg.contains(ICE_PRISON_SPECIAL_MSG))
		{
			returnStr = "Ice prison";
		}
		else if (msg.contains(ICE_CONTAIN_SPECIAL_MSG))
		{
			returnStr = "Ice contain";
		}
		return returnStr;
	}


}
