package com.star.todomvp.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.star.todomvp.data.Task;
import com.star.todomvp.data.source.TasksDataSource;
import com.star.todomvp.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TasksLocalDataSource implements TasksDataSource {

    private static volatile TasksLocalDataSource INSTANCE;

    private TasksDao mTasksDao;
    private AppExecutors mAppExecutors;

    private TasksLocalDataSource(@NonNull AppExecutors appExecutors,
                                 @NonNull TasksDao tasksDao) {
        mAppExecutors = appExecutors;
        mTasksDao = tasksDao;
    }

    public static TasksLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                   @NonNull TasksDao tasksDao) {
        if (INSTANCE == null) {
            synchronized (TasksLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TasksLocalDataSource(appExecutors, tasksDao);
                }
            }
        }

        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        Runnable runnable = () -> {
            final List<Task> tasks = mTasksDao.getTasks();

            mAppExecutors.getMainThread().execute(() -> {
                if (tasks.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onTasksLoaded(tasks);
                }
            });
        };

        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        Runnable runnable = () -> {
            final Task task = mTasksDao.getTaskById(taskId);

            mAppExecutors.getMainThread().execute(() -> {
                if (task != null) {
                    callback.onTaskLoaded(task);
                } else {
                    callback.onDataNotAvailable();
                }
            });
        };

        mAppExecutors.getDiskIO().execute(runnable);
    }

    @Override
    public void saveTask(@NonNull final Task task) {
        checkNotNull(task);

        Runnable saveRunnable = () -> mTasksDao.insertTask(task);

        mAppExecutors.getDiskIO().execute(saveRunnable);
    }

    @Override
    public void completeTask(@NonNull final Task task) {
        Runnable completeRunnable = () -> mTasksDao.updateCompleted(task.getId(), true);

        mAppExecutors.getDiskIO().execute(completeRunnable);
    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull final Task task) {
        Runnable activateRunnable = () -> mTasksDao.updateCompleted(task.getId(), false);

        mAppExecutors.getDiskIO().execute(activateRunnable);
    }

    @Override
    public void activateTask(@NonNull String taskId) {

    }

    @Override
    public void clearCompletedTasks() {
        Runnable clearTasksRunnable = () -> mTasksDao.deleteCompletedTasks();

        mAppExecutors.getDiskIO().execute(clearTasksRunnable);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        Runnable deleteRunnable = () -> mTasksDao.deleteTasks();

        mAppExecutors.getDiskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteTask(@NonNull final String taskId) {
        Runnable deleteRunnable = () -> mTasksDao.deleteTaskById(taskId);

        mAppExecutors.getDiskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }
}
