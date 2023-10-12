package com.example.nexatron.taskformat;

import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.inject.Inject;
import net.runelite.api.Client;

public abstract class StagedTask extends Task
{
	protected final NexManager nexManager;
	private final Set<Stage> activations;
	@Inject
	protected Client client;
	private BooleanSupplier sleepCondition;
	private int sleepTicks;
	private int incrementSleepTicks;

	@Inject
	public StagedTask(NexManager nexManager, Stage... activate)
	{
		this.nexManager = nexManager;
		this.activations = new HashSet<>(Arrays.asList(activate));
	}

	public boolean activated()
	{
		return this.activations.contains(this.nexManager.getStage());
	}

	public boolean run()
	{
		if (this.nexManager != null && this.nexManager.getConfig() != null)
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

	public void sleepWhile(int ticks, BooleanSupplier sleepCondition)
	{
		this.sleepTicks = ticks;
		this.sleepCondition = sleepCondition;
		this.incrementSleepTicks = ticks;
	}

	public abstract boolean execute();


	public List<Stage> getActivationsList()
	{
		if(this.activations != null && this.activations.size() > 0)
		{
			return new ArrayList<>(this.activations);
		}
		return Collections.emptyList();
	}
	public Set<Stage> getActivations()
	{
		return this.activations;
	}
}
