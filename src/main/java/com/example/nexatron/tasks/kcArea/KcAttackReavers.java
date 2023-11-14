package com.example.nexatron.tasks.kcArea;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.Combat;
import com.example.Utility.Hopping;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.Prayers;
import com.example.Utility.TileItemUtil;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.Reaver;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldService;

@TaskDescriptor(
	name = "KC Reavers",
	priority = 1
)
public class KcAttackReavers extends StagedTask
{
	@Inject
	WorldService worldService;
	@Inject
	GameTickManager gameTickManager;
	@Inject
	ItemManager itemManager;

	@Inject
	public KcAttackReavers(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	@Override
	public boolean execute()
	{
		if (!nexManager.shouldKc())
		{
			return false;
		}
		if (nexManager.kcArea.canKillMage())
		{
			return false;
		}
		Widget restore = Consumable.getRestore();
		NPC npcInteractingWithUs = NPCs.search().alive().interactingWithLocal().first().orElse(null);
		if (Prayers.getPoints() == 0
			|| restore == null)
		{
			if (npcInteractingWithUs != null)
			{
				nexManager.print("Attacking npc that's attacking us, then stopping");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(npcInteractingWithUs, "Attack");
				incrementActionCount();
			}
			return false;
		}

		ETileItem loot = TileItemUtil.getClosestETileItem(NexConst.KC_LOOT);
		if (loot != null
			&& !InventoryUtil.isFull())
		{
			nexManager.print("Picking up " + itemManager.getItemComposition(loot.tileItem.getId()).getName());
			MousePackets.queueClickPacket();
			TileItemPackets.queueTileItemAction(loot, false);
			incrementActionCount();
			return true;
		}

		//We need to hop so this should take priority over kcing
		if (nexManager.kcArea.shouldHop)
		{
			//If we have a reaver agrod on us we need to finish it before we can hop
			if (nexManager.getPlugin().reaverManager.hasAtleastOneReaverAgrod() && !client.getLocalPlayer().isInteracting())
			{
				NPC n = nexManager.getPlugin().reaverManager.getHittableInteractingReaver();
				if (n == null)
				{
					//System.out.println("Backup reaver is also null");
					nexManager.print("Reaver somehow null (we r trying to finish them off n hop worlds)");
					return false;
				}
				nexManager.print("Attacking " + n.getName() + "Index: " + n.getIndex());
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(n, "Attack");
				incrementActionCount();
				return true;
			}
			if (gameTickManager.isTickWaiting())
			{
				nexManager.print("Tick waiting in should hop logic.");
				return true;
			}
			gameTickManager.setTickWait(4);
			nexManager.print("We should be hopping, gonna hop to " + Hopping.getValidWorld(true, worldService));
			Hopping.hop(Hopping.getValidWorld(true, worldService), worldService);
			return true;
		}

		if (Combat.getSpecEnergy() >= 80
			&& !Combat.isSpecEnabled()
			&& Equipment.search().withId(ItemID.TOXIC_BLOWPIPE).first().orElse(null) != null)
		{
			nexManager.print("Toggling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
		}
		if (!nexManager.getPlugin().reaverManager.centralArea.contains(client.getLocalPlayer().getWorldLocation()))
		{
			//System.out.println("Alert! Alert! Player is not in the designated kc area. Please return to the green tiled zone.");
			WorldPoint tile = nexManager.findClosestTileToWorldPoint((ArrayList<WorldPoint>)
				nexManager.getPlugin().reaverManager.centralArea.toWorldPointList(), client.getLocalPlayer().getWorldLocation());
			if (tile != null)
			{
				//System.out.println("Moving back to KC green zone, WP: " + tile);
				Movement.move(tile);
				incrementActionCount();
				return true;
			}
		}

		//Need to add a check here for the distance to the reaver NPC.
		//If its > 5 that means we arent attempting to attack it, we are pre pathing to the closest acceptable tile in our world area
		//Also need to do a null check, if the reaver npc is null that means we want to prepath toward the respawn tile.
		Reaver reaver = nexManager.kcArea.getReaverTarget();
		if (reaver == null)
		{
			//System.out.println("Reaver class object is null in KcAttackReavers.");
			if (nexManager.getPlugin().reaverManager.hasAtleastOneReaverAgrod())
			{
				NPC n = nexManager.getPlugin().reaverManager.getHittableInteractingReaver();
				if (n == null)
				{
					//System.out.println("Backup reaver is also null");
					return false;
				}
				//System.out.println("It worked!");
				reaver = nexManager.getPlugin().reaverManager.reavers.get(n.getIndex());
			}
			else
			{
				//System.out.println("Reaver class object is null and we have no reavers aggroed");
				return false;
			}

		}
		nexManager.reaverTest = reaver.getReaver();
		NPC targetNPC = reaver.getReaver();
		if (targetNPC == null)
		{

			if (reaver.getTimeUntilRespawn() > 0)
			{
				//System.out.println("Time until respawn: " + reaver.getTimeUntilRespawn() + " -> pathing to appropriate tile");
				WorldPoint prepathTile = nexManager.findClosestTileToWorldPoint((ArrayList<WorldPoint>)
					nexManager.getPlugin().reaverManager.centralArea.toWorldPointList(), reaver.getSpawnLocation());
				if (prepathTile != null && gameTickManager.isAttackWaiting())
				{
					//System.out.println("Pathing to prepath tile waiting on reaver respawn, WP: " + prepathTile);
					Movement.move(prepathTile);
					incrementActionCount();
					return true;
				}
				if (prepathTile == null)
				{
					//System.out.println("Prepath tile is null somehow in the reaver respawn logic thing.");
				}
			}
			else
			{
				//System.out.println("Our target npc is null in the attack task.");
				if (!client.getLocalPlayer().isInteracting())
				{
					//We have a reaver on us and our target is null, lets just make sure we r attacking the reaver thats on us
					if (nexManager.getPlugin().reaverManager.hasAtleastOneReaverAgrod())
					{
						NPC newTarget = nexManager.getPlugin().reaverManager.getHittableInteractingReaver();
						if (newTarget != null)
						{
							//System.out.println("Attacking the backup target cause target was null?");
							MousePackets.queueClickPacket();
							NPCPackets.queueNPCAction(newTarget, "Attack");
							incrementActionCount();
							return true;
						}
					}
				}
			}
		}

		else
		{
			//Should do a check here for if we have a reaver agrod on us already
			//If we do have a reaver, then we should only move by bp walking i think

			boolean hasReaver = nexManager.getPlugin().reaverManager.hasAtleastOneReaverAgrod();

			//Reaver is not null, do a distance check to find out if we are pathing or attacking
			//We know that the options are either attack or path, because target reaver is not null and we made it down here
			int distance = targetNPC.getWorldLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea);
			//Reaver is more than 7 tiles away from our kc area meaning we need to path to the closest tile
			//Move only if we dont have a reaver agrod on us, or we do have one but we are attack waiting
			if (distance > 7 && (!hasReaver || gameTickManager.isAttackWaiting()))
			{
				//System.out.println("Should be pathing to closest tile to the reaver");
				WorldPoint wp = nexManager.findClosestTileToWorldPoint((ArrayList<WorldPoint>)
					nexManager.getPlugin().reaverManager.centralArea.toWorldPointList(), targetNPC.getWorldLocation());
				//Only proceed if wp is not null and the local player isn't already on the wp tile
				if (wp != null && !client.getLocalPlayer().getWorldLocation().equals(wp))
				{
					//We are on attack cd so we can move safely without tick loss
					//Alternatively we have no reavers agrod on us so we can move safely also
					if (gameTickManager.isAttackWaiting() || !hasReaver)
					{
						//System.out.println("Pathing to most appropriate tile, WP: " + wp);
						Movement.move(wp);
						incrementActionCount();
					}
				}
				else
				{
					if (client.getLocalPlayer().getWorldLocation().equals(wp))
					{
						//System.out.println("Player is already on the wp its trying to path to, will attack a closer reaver while waiting (if available)");
						if (client.getLocalPlayer().isInteracting() && client.getLocalPlayer().getInteracting() != null
							&& client.getLocalPlayer().getInteracting().equals(targetNPC))
						{
							//System.out.println("Returning without doin anythin cuz we already attacking a reaverington");
							return false;
						}
						//System.out.println("Found a reaver. Attacking.");
						MousePackets.queueClickPacket();
						NPCPackets.queueNPCAction(targetNPC, "Attack");
						incrementActionCount();
						return true;
					}
					else
					{
						//System.out.println("Worldpoint is somehow null in this reaver pathing thingsy, how?");
					}

				}
			}
			else
			{
				if (client.getLocalPlayer().isInteracting() && client.getLocalPlayer().getInteracting() != null
					&& client.getLocalPlayer().getInteracting().equals(targetNPC))
				{
					//System.out.println("We are already attacking the npc we want to attack. do nuthin.");
					return false;
				}
				//System.out.println("Found an attackable reaver. Attacking.");
				nexManager.print("Attacking " + targetNPC.getName());
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(targetNPC, "Attack");
				incrementActionCount();
				return true;
			}
		}


//		NPC targetNPC = getNPC();
//		if (targetNPC != null)
//		{
//			if (client.getLocalPlayer().isInteracting())
//			{
//				return false;
//			}
//			nexManager.print("Attacking " + targetNPC.getName());
//			MousePackets.queueClickPacket();
//			NPCPackets.queueNPCAction(targetNPC, "Attack");
//			incrementActionCount();
//			return true;
//		}
		/**
		 *
		 */
		return false;
	}

//	public NPC getNPC()
//	{
//		return nexManager.kcArea.getTarget();
//	}
}
