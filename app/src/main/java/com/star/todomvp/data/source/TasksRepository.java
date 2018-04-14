package com.star.todomvp.data.source;

import android.support.annotation.NonNull;

import com.star.todomvp.data.Task;

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

    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull Task task) {

    }

    @Override
    public void activateTask(@NonNull String taskId) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteTask(@NonNull String taskId) {

    }
}
