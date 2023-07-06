package com.example.nexatron.taskformat;

import java.util.function.BooleanSupplier;

public abstract class Task
{
	private int tickSleep;
	private int tickSleepIncrement;
	private BooleanSupplier condition;

	public abstract boolean run();

	public void sleep(int ticks)
	{
		this.tickSleep = ticks;
	}

	public void sleepWhile(int ticks, BooleanSupplier condition)
	{
		this.condition = condition;
		this.tickSleep = ticks;
		this.tickSleepIncrement = ticks;
	}

	public boolean sleeping()
	{
		if (this.condition != null && this.condition.getAsBoolean())
		{
			this.tickSleep = this.tickSleepIncrement;
			return true;
		}
		if (this.condition != null)
		{
			this.condition = null;
			this.tickSleepIncrement = 0;
			this.tickSleep = 0;
			return false;
		}
		--this.tickSleep;
		if (this.tickSleep <= 0)
		{
			this.tickSleep = 0;
			return false;
		}
		return true;
	}

	public int getTickSleep()
	{
		return this.tickSleep;
	}

	public int getTickSleepIncrement()
	{
		return this.tickSleepIncrement;
	}

	public BooleanSupplier getCondition()
	{
		return this.condition;
	}
}