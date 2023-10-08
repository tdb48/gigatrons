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
		//If a game tick has not yet passed since our last iteration, return

		if (tickCounter <= previousTick)
		{
			return;
		}
		randomSleepCounter++;
		//If we have not yet slept enough client ticks, return
		if (randomSleepCounter < randomSleep)
		{
			return;
		}
		randomSleepCounter = 0;
		previousTick = tickCounter;
		if (Static.getClient().getLocalPlayer().getPoseAnimation() == 5538)
		{
			return;
		}
		for (Task task : this.tasks)
		{
//            System.out.println("Iterating tasks");
			if (task.sleeping())
			{
				continue;
			}

			TaskDescriptor descriptor = this.descriptorHashMap.get(task);
			if (descriptor.client() || !task.run())
			{
				//System.out.println("Not running task -> " + descriptor.name());
				continue;
			}

			this.currentTask = task;
			//System.out.println("Current task -> " + descriptor.name());
			if (!descriptor.blocking())
			{
				//System.out.println("Task is not blocking, continuing to next task.");
				continue;
			}
			//System.out.println("Task IS blocking, ending the task loop here until next game tick.");
			break;
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		tickCounter++;
		randomSleep = ThreadLocalRandom.current().nextInt(0, 20);
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