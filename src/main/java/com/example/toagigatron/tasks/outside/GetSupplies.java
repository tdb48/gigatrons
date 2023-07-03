package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.BankUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.TileObject;

@TaskDescriptor(
	name = "Get supplies",
	priority = 1,
	blocking = true
)
public class GetSupplies extends StagedTask
{

	GameTickManager gameTickManager;

	@Inject
	public GetSupplies(ToaManager toaManager, GameTickManager gameTickManager)
	{
		super(toaManager, Stage.OUTSIDE);
		this.gameTickManager = gameTickManager;
	}

	public boolean execute()
	{
		if (toaManager.overall.died || toaManager.hasTooManySupplies() || !toaManager.isPrePotted() || toaManager.hasRequiredSupplies())
		{
			return false;
		}
		if (toaManager.onBreak())
		{
			return false;
		}
		if (toaManager.needsBreak() && !toaManager.allowedToBreak)
		{
			toaManager.allowedToBreak = true;
			return false;
		}
		if (gameTickManager.isTickWaiting())
		{
			return false;
		}
		if (!BankUtil.isOpen())
		{
			TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
			if (camel != null)
			{
				toaManager.print("Clicking banker");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(camel, false, "Bank");
				toaManager.print("Its actually this one, weird");
				gameTickManager.setTickWait(2);
				return true;
			}
			toaManager.print("Cant find banker somehow");
			return false;
		}
		if (!BankUtil.isMainTabOpen())
		{
			BankUtil.openMainTab();
			return true;
		}
		if (toaManager.hasTooManySupplies())
		{
			BankUtil.depositInventory();
			return true;
		}
		ArrayList<Integer> itemsToWithdraw = generateRequiredItemsList();
		toaManager.withdraw(itemsToWithdraw);
		return true;
	}

	public ArrayList<Integer> generateRequiredItemsList()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i = 0; i < toaManager.requiredAnti(); i++)
		{
			returnList.add(Consumables.FULL_DOSE_ANTI);
		}
		for (int i = 0; i < toaManager.requiredBrew(); i++)
		{
			returnList.add(Consumables.FULL_DOSE_BREW);
		}
		for (int i = 0; i < toaManager.requiredSanfew(); i++)
		{
			returnList.add(Consumables.FULL_DOSE_SANFEW);
		}
		for (int i = 0; i < toaManager.requiredScb(); i++)
		{
			returnList.add(Consumables.FULL_DOSE_SCB);
		}
		for (int i = 0; i < toaManager.requiredStam(); i++)
		{
			returnList.add(Consumables.FULL_DOSE_STAM);
		}
		for (int i = 0; i < toaManager.requiredRestore(); i++)
		{
			returnList.add(Consumables.FULL_DOSE_RESTORE);
		}
		return returnList;
	}

}

