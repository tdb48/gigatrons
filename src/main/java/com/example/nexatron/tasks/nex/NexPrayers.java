//package com.example.nexatron.tasks.nex;
//
//
//import com.example.Utility.Combat;
//import com.example.Utility.Prayer;
//import com.example.Utility.Prayers;
//import com.example.Utility.Static;
//import com.example.nexatron.NexatronPlugin;
//import com.example.nexatron.manager.GameTickManager;
//import com.example.nexatron.manager.NexManager;
//import com.example.nexatron.model.constants.Stage;
//import com.example.nexatron.model.constants.WeaponMap;
//import com.example.nexatron.taskformat.StagedTask;
//import com.example.nexatron.taskformat.TaskDescriptor;
//import java.util.List;
//import javax.inject.Inject;
//import net.runelite.api.InventoryID;
//import net.runelite.api.Item;
//import net.runelite.api.ItemContainer;
//import net.runelite.api.widgets.Widget;
//
//@TaskDescriptor(
//	name = "Nex prayers",
//	priority = Integer.MAX_VALUE
//)
//public class NexPrayers extends StagedTask
//{
//	public static final int AUGURY_UNLOCKED = 5452;
//	@Inject
//	GameTickManager gameTickManager;
//
//	@Inject
//	NexatronPlugin plugin;
//
//	@Inject
//	public NexPrayers(NexManager nexManager)
//	{
//		super(nexManager,
//			Stage.MINION_SMOKE,
//			Stage.NEX_SMOKE,
//			Stage.MINION_SHADOW,
//			Stage.NEX_SHADOW,
//			Stage.MINION_BLOOD,
//			Stage.NEX_BLOOD,
//			Stage.MINION_ICE,
//			Stage.NEX_ICE,
//			Stage.NEX_ZAROS);
//	}
//
//	public boolean execute()
//	{
////		if (Prayers.getPoints() == 0)
////		{
////			return false;
////		}
////		if (!this.getPrayers().isEmpty())
////		{
////			if (nexManager.config.prayFlick()
////				&& plugin.getManager().actionCounter < 8
////				&& Prayers.hasEnabled(getPrayers()))
////			{
////				for (Prayer prayer : getPrayers())
////				{
////					Prayers.toggle(prayer);
////					incrementActionCount();
////				}
////				for (Prayer prayer : getPrayers())
////				{
////					Prayers.toggle(prayer);
////					incrementActionCount();
////				}
////			}
////			else
////			{
////				for (Prayer prayer : getPrayers())
////				{
////					if (!Prayers.isEnabled(prayer))
////					{
////						Prayers.toggle(prayer);
////						incrementActionCount();
////					}
////				}
////			}
////			return true;
////		}
////		else if (this.getPrayers().isEmpty() && Prayers.anyActive())
////		{
////			Prayers.disableAll();
////			setActionCount(getActionCount() + Prayers.getActivePrayers().size());
////			return true;
////		}
////		return false;
////	}
//
//}
