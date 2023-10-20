package com.example.nexatron.tasks.general;


import com.example.Utility.BankUtil;
import com.example.Utility.Game;
import com.example.Utility.Tab;
import com.example.Utility.Tabs;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Switch tab"
)
public class SwitchTab extends Task
{
	@Inject
	NexManager nexManager;

	public boolean run()
	{
		if (BankUtil.isOpen() || Game.isWelcomeVisible())
		{
			return false;
		}
		if (Tabs.isOpen(Tab.LOG_OUT))
		{
			nexManager.print("Opening inventory");
			Tabs.open(Tab.INVENTORY);
			return true;
		}
		return false;
	}

}
