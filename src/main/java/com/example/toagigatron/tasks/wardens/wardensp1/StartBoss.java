package com.example.toagigatron.tasks.wardens.wardensp1;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Start wardens p1",
	priority = 10
)
public class StartBoss extends StagedTask
{
	@Inject
	public StartBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P1);
	}

	public boolean execute()
	{
		GameObject entry = ObjectUtil.getNearestGameObject(ToaConstants.WARDENS_P1_BOSS_ENTRY);
		NPC osmumten = NPCUtil.findNearest("Osmumten");
		if (entry == null || osmumten == null || osmumten.getAnimation() == ToaConstants.OSMUMTEN_START_ANIMATION)
		{
			return false;
		}
		// If you have a shadow, start with shadow gear equipped, otherwise wear bgs gear
		ArrayList<Integer> gearToStartWith = toaManager.meleeSetup.getAllItemsBgs();
		if (!toaManager.hasGearEquipped(gearToStartWith))
		{
			toaManager.swap(gearToStartWith);
		}
		if (Reachable.isWalkable(entry.getWorldLocation().dy(2)))
		{
			toaManager.print("Entering with crystal");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(entry, false, "Use");
			return true;
		}

		// Need one of each: brew, restore, salt, adren and ambrosia(?)
		Widget brew = Consumables.getBrew();
		if (brew == null)
		{
			brew = Consumables.getRestore();
		}
		if (brew != null && Inventory.getEmptySlots() < invSpaceRequired())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(brew, "Drop");
			return true;
		}
		if (!withdrawItems())
		{
			return true;
		}
		if (!toaManager.wardens12.bagOpened)
		{
			toaManager.openAndCloseBag();
		}
		else
		{
			toaManager.print("Starting p1");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(osmumten, "Begin");
			return true;
		}
		return false;

	}

	public boolean withdrawItems()
	{
		boolean returnValue = true;
		boolean ambrosia = toaManager.consumableTracker.totalAmbrosiaDoses > 0;
		boolean scarab = toaManager.consumableTracker.totalScarabDoses > 0;
		ArrayList<Integer> potsToWithdraw = new ArrayList<>();
		if (toaManager.consumableTracker.totalSaltDoses > 0 && toaManager.consumableTracker.inventorySaltDoses == 0)
		{
			toaManager.print("Withdrawing salt");
			potsToWithdraw.add(ItemID.SMELLING_SALTS_2);
			returnValue = false;
		}
		if (toaManager.consumableTracker.totalAdrenalineDoses > 0 && toaManager.consumableTracker.inventoryAdrenalineDoses == 0)
		{
			toaManager.print("Withdrawing adren");
			potsToWithdraw.add(ItemID.LIQUID_ADRENALINE_2);
			returnValue = false;
		}
		if (ambrosia && toaManager.consumableTracker.inventoryAmbrosiaDoses == 0)
		{
			toaManager.print("Withdrawing ambrosia");
			potsToWithdraw.add(ItemID.AMBROSIA_2);
			returnValue = false;
		}
		if (scarab && toaManager.consumableTracker.inventoryScarabDoses == 0)
		{
			toaManager.print("Withdrawing scarab");
			potsToWithdraw.add(ItemID.BLESSED_CRYSTAL_SCARAB_2);
			returnValue = false;
		}
		if (toaManager.consumableTracker.inventoryRaidRestoreDoses == 0)
		{
			toaManager.print("Withdrawing tears");
			potsToWithdraw.add(ItemID.TEARS_OF_ELIDINIS_4);
			returnValue = false;
		}
		if (toaManager.consumableTracker.inventoryRaidBrewDoses == 0)
		{
			toaManager.print("Withdrawing brew");
			potsToWithdraw.add(ItemID.NECTAR_4);
			returnValue = false;
		}
		if (potsToWithdraw.size() > 0)
		{
			toaManager.withdrawFromBag(potsToWithdraw);
		}
		return returnValue;
	}

	public int invSpaceRequired()
	{
		boolean ambrosia = toaManager.consumableTracker.totalAmbrosiaDoses > 0;
		boolean scarab = toaManager.consumableTracker.totalScarabDoses > 0;
		int i = ambrosia ? 5 : 4;
		i = scarab ? 6 : i;
		//int i = ambrosia ? 5 : 4;
		if (toaManager.consumableTracker.inventorySaltDoses > 0)
		{
			i--;
		}
		if (toaManager.consumableTracker.inventoryRaidBrewDoses > 0)
		{
			i--;
		}
		if (toaManager.consumableTracker.inventoryAdrenalineDoses > 0)
		{
			i--;
		}
		if (toaManager.consumableTracker.inventoryRaidRestoreDoses > 0)
		{
			i--;
		}
		if (ambrosia && toaManager.consumableTracker.inventoryAmbrosiaDoses > 0)
		{
			i--;
		}
		if (scarab && toaManager.consumableTracker.inventoryScarabDoses > 0)
		{
			i--;
		}
		toaManager.print("Space required: " + i);
		return i;
	}
}
