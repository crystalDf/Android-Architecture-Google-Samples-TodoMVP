package com.star.todomvp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.star.todomvp.data.source.TasksRepository;
import com.star.todomvp.data.source.local.TasksLocalDataSource;
import com.star.todomvp.data.source.local.ToDoDatabase;
import com.star.todomvp.data.source.remote.TasksRemoteDataSource;
import com.star.todomvp.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        ToDoDatabase database = ToDoDatabase.getInstance(context);
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(new AppExecutors(),
                        database.taskDao()));
    }
}