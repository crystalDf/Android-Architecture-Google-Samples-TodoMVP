package com.star.todomvp.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.star.todomvp.data.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource mTasksRemoteDataSource;
    private final TasksDataSource mTasksLocalDataSource;

    Map<String, Task> mCachedTasks;

    boolean mCacheIsDirty = false;

    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource,
                                              TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        checkNotNull(callback);

        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));

            return;
        }

        if (!mCacheIsDirty) {
            getTasksFromLocalDataSource(callback);
        } else {
            getTasksFromRemoteDataSource(callback);
        }
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        Task cachedTask = getTaskWithId(taskId);

        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        getTaskFromLocalDataSource(taskId, callback);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        refreshCache(task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        refreshCache(completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);

        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);

        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        refreshCache(activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);

        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId));

        mCachedTasks.remove(taskId);
    }

    private void getTasksFromLocalDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                getTasksFromRemoteDataSource(callback);
            }
        });
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void getTaskFromLocalDataSource(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                refreshCache(task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                getTaskFromRemoteDataSource(taskId, callback);
            }
        });
    }

    private void getTaskFromRemoteDataSource(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                refreshCache(task);
                refreshLocalDataSource(task);
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    private void refreshCache(Task task) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for (Task task : tasks) {
            mTasksLocalDataSource.saveTask(task);
        }
    }

    private void refreshLocalDataSource(Task task) {
        mTasksLocalDataSource.saveTask(task);
    }

    @Nullable
    private Task getTaskWithId(@NonNull String id) {
        checkNotNull(id);

        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }
}
