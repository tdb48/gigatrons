package com.example.toagigatron.taskformat;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.inject.Inject;
import net.runelite.api.Client;

public abstract class StagedTask extends Task
{
	protected final ToaManager toaManager;
	private BooleanSupplier sleepCondition;
	private final Set<Stage> activations;
	private int sleepTicks;
	private int incrementSleepTicks;
	@Inject
	protected Client client;

	@Inject
	public StagedTask(ToaManager toaManager, Stage... activate)
	{
		this.toaManager = toaManager;
		this.activations = new HashSet<>(Arrays.asList(activate));
	}

	public void sleepWhile(int ticks, BooleanSupplier sleepCondition)
	{
		this.sleepTicks = ticks;
		this.sleepCondition = sleepCondition;
		this.incrementSleepTicks = ticks;
	}

	public boolean activated()
	{
		return this.activations.contains(this.toaManager.getStage());
	}

	public boolean run()
	{
		if (this.toaManager != null && this.toaManager.getConfig() != null)
		{
			if (this.sleepCondition != null && this.sleepCondition.getAsBoolean())
			{
				this.sleepTicks = this.incrementSleepTicks;
			}

			if (this.sleepTicks > 0)
			{

				--this.sleepTicks;
				return false;
			}
			else
			{
				this.incrementSleepTicks = 0;
				this.sleepCondition = null;
				return this.activated() && this.execute();
			}
		}
		else
		{
			return false;
		}
	}

	public abstract boolean execute();

	public Set<Stage> getActivations()
	{
		return this.activations;
	}
}
