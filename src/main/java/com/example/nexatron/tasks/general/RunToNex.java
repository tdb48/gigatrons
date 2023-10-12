package com.example.nexatron.tasks.general;


import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Walker;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import static com.example.nexatron.tasks.nex.NexPrayers.AUGURY_UNLOCKED;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Run to Nex",
	priority = 1
)
public class RunToNex extends StagedTask
{
	@Inject
	Consumable consumable;
	public static final WorldPoint TROLLHEIM_CRAWL_TILE = new WorldPoint(2899, 3706, 0);
	public static final WorldPoint GWD_ENTRANCE_TILE = new WorldPoint(2914, 3745, 0);
	public static final WorldPoint NEX_ENTRANCE_TILE = new WorldPoint(2883, 5280, 2);

	@Inject
	public RunToNex(NexManager nexManager)
	{
		super(nexManager, Stage.RUN_TO_NEX);
	}

	public boolean execute()
	{
		Prayer defensive = getDefensive();
		if (NexConst.GWD.contains(nexManager.getPlayerPoint()))
		{
			Prayer tankPrayer = getTankPrayer();
			if (tankPrayer != null && !Prayers.isEnabled(tankPrayer))
			{
				nexManager.print("Enabling the tank prayer");
				Prayers.toggle(tankPrayer);
				incrementActionCount();
			}
		}
		if (defensive != null && !Prayers.isEnabled(defensive))
		{
			nexManager.print("Enabling the protection prayer");
			Prayers.toggle(defensive);
			incrementActionCount();
		}

		if (NexConst.TROLLHEIM.contains(nexManager.getPlayerPoint()))
		{
			TileObject crawl = TileObjects.search().withId(NexConst.TROLLHEIM_TO_GWD_CRAWL).first().orElse(null);
			if (crawl != null
			&& client.getLocalPlayer().getWorldLocation().distanceTo(crawl.getWorldLocation()) < 10)
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(crawl, false, "Crawl-through");
				incrementActionCount();
				nexManager.print("Crawling to GWD");
				return true;
			}
			if (NexConst.TROLLHEIM_TELEPORT_AREA.contains(nexManager.getPlayerPoint()))
			{
				TileObject rocks = TileObjects.search().withId(NexConst.TROLLHEIM_ROCKS).first().orElse(null);
				if (rocks != null)
				{
					MousePackets.queueClickPacket();
					ObjectPackets.queueObjectAction(rocks, false, "Climb");
					incrementActionCount();
					nexManager.print("Climbing down Trollheim rocks");
					return true;
				}
			}
			ArrayList<WorldPoint> path = (ArrayList<WorldPoint>) GlobalCollisionMap.findPath(TROLLHEIM_CRAWL_TILE);
			nexManager.print("Running to gwd entrance");
			Walker.stepAlongBigSteps(path);
			incrementActionCount();
			return true;
		}
		if (NexConst.GWD_OUTSIDE.contains(nexManager.getPlayerPoint()))
		{
			TileObject entrance = TileObjects.search().withId(NexConst.GWD_ENTRANCE).first().orElse(null);
			if (entrance != null)
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(entrance, false, "Climb-down");
				incrementActionCount();
				nexManager.print("Climbing down GWD entrance");
				return true;
			}
			ArrayList<WorldPoint> path = (ArrayList<WorldPoint>) GlobalCollisionMap.findPath(GWD_ENTRANCE_TILE);
			nexManager.print("Running to gwd entrance");
			Walker.stepAlongBigSteps(path);
			incrementActionCount();
			return true;
		}

		if (NexConst.GWD.contains(nexManager.getPlayerPoint()))
		{
			Widget stamina = Consumable.getStamina();
			if (stamina != null && !Movement.isStaminaBoosted())
			{
				nexManager.print("Drinking stamina");
				consumable.consume(stamina);
				incrementActionCount();
			}
			TileObject nexDoor = TileObjects.search().withId(NexConst.NEX_DOOR).first().orElse(null);
			if (nexDoor != null)
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nexDoor, false, "Open");
				incrementActionCount();
				nexManager.print("Entering Nex");
				return true;
			}
			ArrayList<WorldPoint> path = (ArrayList<WorldPoint>) GlobalCollisionMap.findPath(NEX_ENTRANCE_TILE);
			nexManager.print("Running to nex entrance");
			Walker.stepAlongBigSteps(path);
			incrementActionCount();
			return true;
		}

		return false;
	}

	public Prayer getDefensive()
	{
		if (NexConst.TROLLHEIM.contains(nexManager.getPlayerPoint()))
		{
			if (NPCs.search().nameContains("troll").interactingWithLocal().first().orElse(null) != null)
			{
				return Prayer.PROTECT_FROM_MISSILES;
			}
		}
		if (NexConst.GWD_OUTSIDE.contains(nexManager.getPlayerPoint()))
		{
			return Prayer.PROTECT_FROM_MELEE;
		}
		if (NexConst.GWD.contains(nexManager.getPlayerPoint()))
		{
			return Prayer.PROTECT_FROM_MISSILES;
		}
		return null;
	}

	public Prayer getTankPrayer()
	{
		return client.getVarbitValue(AUGURY_UNLOCKED) == 0
			? Prayer.PIETY
			: Prayer.AUGURY;
	}
}
