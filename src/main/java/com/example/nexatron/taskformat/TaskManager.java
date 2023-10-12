package com.example.nexatron.taskformat;

import com.example.Utility.Static;
import com.google.inject.Injector;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManager
{
	private static final Logger log = LoggerFactory.getLogger(TaskManager.class);
	private final CopyOnWriteArrayList<Task> tasks = new CopyOnWriteArrayList<>();
	private final HashMap<Task, TaskDescriptor> descriptorHashMap = new HashMap<>();
	private final EventBus eventBus;
	private final long lastTaskRun = System.currentTimeMillis();
	int tickCounter = 0;
	int randomSleep = 0;
	int randomSleepCounter = 0;
	int previousTick = 0;
	private Task currentTask;

	private Task currentTaskNew = null;

	private final CopyOnWriteArrayList<Task> currentTasks = new CopyOnWriteArrayList<>();

	public int actionCounter = 0;

	@Inject
	public TaskManager(EventBus eventBus)
	{
		this.eventBus = eventBus;
	}

	public void registerTasks(Injector injector, Class<?>[] tasks)
	{
		for (Class<?> task : tasks)
		{
			if (!task.isAnnotationPresent(TaskDescriptor.class))
			{
				log.error("Task {} is not annotated with @TaskDescriptor", task.getSimpleName());
				continue;
			}
			if (!Task.class.isAssignableFrom(task))
			{
				log.error("Task {} is not a subclass of Task", task.getAnnotation(TaskDescriptor.class).name());
				continue;
			}
			this.registerTask(injector, (Class<? super Task>) task);
		}
	}

	public void registerTask(Injector injector, Class<? super Task> task)
	{
		Task instance = (Task) injector.getInstance(task);
		TaskDescriptor descriptor = task.getAnnotation(TaskDescriptor.class);
		this.registerTask(instance, descriptor);
	}

	public String getCurrentTask()
	{
		return this.currentTask == null ? "None" : this.descriptorHashMap.get(this.currentTask).name();
	}

	public String getCurrentTaskNew()
	{
		if (this.currentTaskNew == null)
		{
			return "None";
		}
		String name = this.descriptorHashMap.get(this.currentTaskNew).name();
		if (name == null || name.length() == 0)
		{
			return "None";
		}
		return name;
	}

	private void registerTask(Task task, TaskDescriptor descriptor)
	{
		this.tasks.add(task);
		if (descriptor.register())
		{
			this.eventBus.register(task);
		}
		this.descriptorHashMap.put(task, descriptor);
	}

	public void start()
	{
		this.eventBus.register(this);
		this.tasks.sort(Comparator.comparing((t) -> this.descriptorHashMap.get(t).priority()).reversed());
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		/**
		 * Random sleep code to be added back later if we want
		 */
		//If a game tick has passed since our last iteration, we are allowed to do our random sleep
		if (tickCounter > previousTick)
		{
			randomSleepCounter++;
			//If we have not yet slept enough client ticks, return
			if (randomSleepCounter < randomSleep)
			{
				//System.out.println("Randomly sleeping before attempting to execute tasks.");
				return;
			}
		}

		randomSleepCounter = 0;
		previousTick = tickCounter;

		//DONT run tasks while phasing into instances which is what i think this pose anim is
		if (Static.getClient().getLocalPlayer().getPoseAnimation() == 5538)
		{
			return;
		}

		if (!currentTasks.isEmpty())
		{
			currentTaskNew = currentTasks.get(0);
			currentTasks.remove(currentTaskNew);
			if (currentTaskNew.sleeping())
			{
				return;
			}
			TaskDescriptor descriptor = this.descriptorHashMap.get(currentTaskNew);
			//resetting the action counter to 0 for this task, it will be incremented based on how many actions are performed during the tasks run() call
			currentTaskNew.setActionCount(0);
			if (currentTaskNew.run())
			{
				//System.out.println("Running task: " + descriptor.name() + " - Current task actions -> " + currentTaskNew.getActionCount() + " - Total actions: " + (actionCounter+currentTaskNew.getActionCount()));
				if (descriptor.blocking())
				{
					currentTasks.clear();
				}
			}
			actionCounter += currentTaskNew.getActionCount();
			if (actionCounter >= 10)
			{
				System.out.println("Clearing current task list as we have sent " + actionCounter + " actions to the server on this gametick.");
				currentTasks.clear();
			}
			//Moved the blocking check into the run() check because i think it should only block if it runs
//			currentTaskNew.run();
//			if (descriptor.blocking())
//			{
//				currentTasks.clear();
//			}
		}

		/**
		 * LEGACY TASK SYSTEM
		 */

//		for (Task task : this.tasks)
//		{
////            System.out.println("Iterating tasks");
//			if (task.sleeping())
//			{
//				continue;
//			}
//
//			TaskDescriptor descriptor = this.descriptorHashMap.get(task);
//			if (descriptor.client() || !task.run())
//			{
//				//System.out.println("Not running task -> " + descriptor.name());
//				continue;
//			}
//
//			this.currentTask = task;
//			//System.out.println("Current task -> " + descriptor.name());
//			if (!descriptor.blocking())
//			{
//				//System.out.println("Task is not blocking, continuing to next task.");
//				continue;
//			}
//			//System.out.println("Task IS blocking, ending the task loop here until next game tick.");
//			break;
//		}
	}


	public void decideTasks()
	{
		for (Task task : this.tasks)
		{
			if (task instanceof StagedTask)
			{
				if (((StagedTask) task).activated())
				{
					currentTasks.add(task);
				}
			}
			else
			{
				currentTasks.add(task);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		actionCounter = 0;
		decideTasks();
		//System.out.println("current tasks length -> " + currentTasks.size());
//		for(Task task : currentTasks)
//		{
//			TaskDescriptor descriptor = this.descriptorHashMap.get(task);
//			System.out.println("Task name -> " + descriptor.name());
//		}
		//System.out.println();
		int tasksSize = currentTasks.size();
		//Upper limit of sleep is between 0 and 25-number of tasks (leaving 5 ticks at the end of the game tick for overhead).
		int upperBound = tasksSize >= 25 ? 0 : 25 - tasksSize;
		tickCounter++;
		randomSleep = ThreadLocalRandom.current().nextInt(0, upperBound);
		//System.out.println("Our next random sleep will be " + randomSleep + " client ticks.");
	}

	public void stop()
	{
		this.eventBus.unregister(this);
		for (Task task : this.tasks)
		{
			this.eventBus.unregister(task);
		}
		this.tasks.clear();
		this.descriptorHashMap.clear();
		this.currentTask = null;
	}

}