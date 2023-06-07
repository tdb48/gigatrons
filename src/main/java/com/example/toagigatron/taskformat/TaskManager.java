package com.example.toagigatron.taskformat;

import javax.inject.Inject;

import com.example.Utility.Static;
import com.google.inject.Injector;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskManager
{
    private static final Logger log = LoggerFactory.getLogger(TaskManager.class);
    private final CopyOnWriteArrayList<com.example.toagigatron.taskformat.Task> tasks = new CopyOnWriteArrayList<>();
    private final HashMap<com.example.toagigatron.taskformat.Task, com.example.toagigatron.taskformat.TaskDescriptor> descriptorHashMap = new HashMap<>();
    private final EventBus eventBus;
    private long lastTaskRun = System.currentTimeMillis();
    private com.example.toagigatron.taskformat.Task currentTask;

    @Inject
    public TaskManager(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    public void registerTasks(Injector injector, Class<?>[] tasks)
    {
        for (Class<?> task : tasks)
        {
            if (!task.isAnnotationPresent(com.example.toagigatron.taskformat.TaskDescriptor.class))
            {
                log.error("Task {} is not annotated with @TaskDescriptor", task.getSimpleName());
                continue;
            }
            if (!com.example.toagigatron.taskformat.Task.class.isAssignableFrom(task))
            {
                log.error("Task {} is not a subclass of Task", task.getAnnotation(com.example.toagigatron.taskformat.TaskDescriptor.class).name());
                continue;
            }
//			System.out.println("Successfully registering task -> " + task.getAnnotation(com.example.toagigatron.taskformat.TaskDescriptor.class).name());
            this.registerTask(injector, (Class<? super com.example.toagigatron.taskformat.Task>) task);
        }
    }

    public void registerTask(Injector injector, Class<? super com.example.toagigatron.taskformat.Task> task)
    {
        com.example.toagigatron.taskformat.Task instance = (com.example.toagigatron.taskformat.Task) injector.getInstance(task);
        com.example.toagigatron.taskformat.TaskDescriptor descriptor = task.getAnnotation(com.example.toagigatron.taskformat.TaskDescriptor.class);
        this.registerTask(instance, descriptor);
    }

    public String getCurrentTask()
    {
        return this.currentTask == null ? "None" : this.descriptorHashMap.get(this.currentTask).name();
    }

    private void registerTask(com.example.toagigatron.taskformat.Task task, com.example.toagigatron.taskformat.TaskDescriptor descriptor)
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
    public void onGameTick(GameTick gameTick)
    {
        if (Static.getClient().getLocalPlayer().getPoseAnimation() == 5538)
        {
            return;
        }
        for (com.example.toagigatron.taskformat.Task task : this.tasks)
        {
            if (task.sleeping())
            {
                continue;
            }

            com.example.toagigatron.taskformat.TaskDescriptor descriptor = this.descriptorHashMap.get(task);
            if (descriptor.client() || !task.run())
            {
                continue;
            }

            this.currentTask = task;
            if (!descriptor.blocking())
            {
                continue;
            }
            break;
        }
    }

    public void loop()
    {
        for (com.example.toagigatron.taskformat.Task task : this.tasks)
        {
            TaskDescriptor descriptor = this.descriptorHashMap.get(task);
            if (!descriptor.client() || System.currentTimeMillis() - this.lastTaskRun < 15L || !task.run())
            {
                continue;
            }
            this.lastTaskRun = System.currentTimeMillis();
            System.out.println("Ran gt task: " + task.getClass().getSimpleName());
            if (!descriptor.blocking())
            {
                continue;
            }
            break;
        }
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

    public boolean canPause()
    {
        return this.currentTask == null || this.descriptorHashMap.get(this.currentTask).stoppable();
    }

}