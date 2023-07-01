package com.example.toagigatron.tasks.inside;

import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.WidgetUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;
import java.util.ArrayList;

@TaskDescriptor(
	name = "Claiming supplies",
	priority = 1,
	blocking = true
)
public class ClaimSupplies extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public ClaimSupplies(ToaManager toaManager)
	{
		super(toaManager, Stage.INSIDE);
	}

	// First claim, get salt from either power or chaos
	// Second claim, get supplies
	public boolean execute()
	{
		if (gameTickManager.isTickWaiting() || !toaManager.inside.canClaimSupplies())
		{
			return false;
		}
		NPC supplyGhost = NPCUtil.findNearest(ToaConstants.HELPFUL_SPIRIT);
		if (supplyGhost == null)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (supplyGhost.getWorldLocation().distanceTo(playerPoint) > 2)
		{
			WorldPoint tileSouthOfGhost = supplyGhost.getWorldLocation().dy(-1);
			Movement.walk(tileSouthOfGhost);
		}

		ArrayList<Widget> staminaPotion = InventoryUtil.getAll(Consumables.STAM.stream().mapToInt(i -> i).toArray());
		if (!staminaPotion.isEmpty())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(staminaPotion.get(0), "Drop");
			return true;
		}

		ArrayList<Widget> antiPotion = InventoryUtil.getAll(Consumables.ANTI.stream().mapToInt(i -> i).toArray());
		if (!antiPotion.isEmpty())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(antiPotion.get(0), "Drop");
			return true;
		}

		ArrayList<Widget> scbPotion = InventoryUtil.getAll(Consumables.COMBAT.stream().mapToInt(i -> i).toArray());
		if (!scbPotion.isEmpty())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(scbPotion.get(0), "Drop");
			return true;
		}
		//If you don't have the supply widget open, click the npc
		if (!WidgetUtil.isVisible(client.getWidget(777, 1)))
		{
			toaManager.print("Clicking on ghost");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(supplyGhost, "Claim");
			return true;
		}
		Widget bestOption = findBestOption();
		if (bestOption == null)
		{
			toaManager.print("cant find option");
			return false;
		}
		gameTickManager.setTickWait(2);
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetAction(bestOption, "Choose");
		//WidgetPackets.widgetAction(bestOption, "Choose");
		return true;
	}

	public boolean hasSupplyBag()
	{
		return InventoryUtil.contains(Consumables.SUPPLY_BAG);
	}

	public Widget findBestOption()
	{
		if (!WidgetUtil.isVisible(client.getWidget(777, 10))
			|| !WidgetUtil.isVisible(client.getWidget(777, 7))
			|| !WidgetUtil.isVisible(client.getWidget(777, 4)))
		{
			return null;
		}
		Widget life = client.getWidget(777, 4);
		Widget chaos = client.getWidget(777, 7);
		Widget power = client.getWidget(777, 10);

		int chaosSalt = getSaltInWidget(chaos);
		int powerSalt = getSaltInWidget(power);

		if (hasSupplyBag())
		{
			//If we already have a bag but we have 1 or less salt doses and the chaos widget has 1 or more salt doess, take chaos
			if(lowSalt() && chaosSalt >= 1){
				updateDoses(chaos);
				toaManager.print("Picking chaos");
				return client.getWidget(777, 9);
			} else {
				toaManager.print("Picking life");
				updateDoses(life);
				return client.getWidget(777, 6);
			}

		}


		if (chaosSalt > powerSalt || (chaosSalt == 1 && powerSalt == 1))
		{
			updateDoses(chaos);
			toaManager.print("Picking chaos");
			return client.getWidget(777, 9);
		}
		else
		{
			toaManager.print("Picking power");
			updateDoses(power);
		}
		return client.getWidget(777, 12);
	}

	private void updateDoses(Widget widget)
	{
		toaManager.consumableTracker.bagRaidBrewDoses += getBrewInWidget(widget) * 4;
		toaManager.consumableTracker.bagRaidRestoreDoses += getRestoreInWidget(widget) * 4;
		toaManager.consumableTracker.bagAmbrosiaDoses += getAmbrosiaInWidget(widget) * 2;
		toaManager.consumableTracker.bagSaltDoses += getSaltInWidget(widget) * 2;
		toaManager.consumableTracker.bagAdrenalineDoses += getAdrenalineInWidget(widget) * 2;
		toaManager.consumableTracker.bagScarabDoses += getScarabInWidget(widget) * 2;
	}

	public int getSaltInWidget(Widget widget)
	{
		if (!WidgetUtil.isVisible(widget))
		{
			return 0;
		}
		for (Widget itemWidget : widget.getDynamicChildren())
		{
			if (Consumables.SALT.contains(itemWidget.getItemId()))
			{
				return itemWidget.getItemQuantity();
			}
		}
		return 0;
	}
	public int getScarabInWidget(Widget widget)
	{
		if (!WidgetUtil.isVisible(widget))
		{
			return 0;
		}
		for (Widget itemWidget : widget.getDynamicChildren())
		{
				if (Consumables.SCARAB.contains(itemWidget.getItemId()))
			{
				return itemWidget.getItemQuantity();
			}
		}
		return 0;
	}

	public int getAdrenalineInWidget(Widget widget)
	{
		if (!WidgetUtil.isVisible(widget))
		{
			return 0;
		}
		for (Widget itemWidget : widget.getDynamicChildren())
		{
			if (Consumables.SPEC.contains(itemWidget.getItemId()))
			{
				return itemWidget.getItemQuantity();
			}
		}
		return 0;
	}

	public int getAmbrosiaInWidget(Widget widget)
	{
		if (!WidgetUtil.isVisible(widget))
		{
			return 0;
		}
		for (Widget itemWidget : widget.getDynamicChildren())
		{
			if (Consumables.AMBROSIA.contains(itemWidget.getItemId()))
			{
				return itemWidget.getItemQuantity();
			}
		}
		return 0;
	}

	public int getRestoreInWidget(Widget widget)
	{
		if (!WidgetUtil.isVisible(widget))
		{
			return 0;
		}
		for (Widget itemWidget : widget.getDynamicChildren())
		{
			if (Consumables.RESTORE.contains(itemWidget.getItemId()))
			{
				return itemWidget.getItemQuantity();
			}
		}
		return 0;
	}

	public int getBrewInWidget(Widget widget)
	{
		if (!WidgetUtil.isVisible(widget))
		{
			return 0;
		}
		for (Widget itemWidget : widget.getDynamicChildren())
		{
			if (Consumables.BREW.contains(itemWidget.getItemId()))
			{
				return itemWidget.getItemQuantity();
			}
		}
		return 0;
	}

	private boolean lowSalt(){
		if(toaManager.consumableTracker.totalSaltDoses >= 2){
			return false;
		}

		return toaManager.consumableTracker.totalSaltDoses == 0 ||
				(toaManager.consumableTracker.totalSaltDoses == 1 && toaManager.overall.saltInTicks <= 300);
	}


}
