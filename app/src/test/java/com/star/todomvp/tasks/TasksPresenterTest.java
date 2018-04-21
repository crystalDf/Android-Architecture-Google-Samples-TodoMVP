package com.star.todomvp.tasks;

import com.google.common.collect.Lists;
import com.star.todomvp.data.Task;
import com.star.todomvp.data.source.TasksDataSource;
import com.star.todomvp.data.source.TasksRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TasksPresenterTest {

    private static List<Task> sTasks;

    @Mock
    private TasksRepository mTasksRepository;

    @Mock
    private TasksContract.View mTasksView;

    private TasksPresenter mTasksPresenter;

    @Captor
    private ArgumentCaptor<TasksDataSource.LoadTasksCallback> mLoadTasksCallbackArgumentCaptor;

    @Before
    public void setupTasksPresenter() {

        MockitoAnnotations.initMocks(this);

        mTasksPresenter = new TasksPresenter(mTasksRepository, mTasksView);

        when(mTasksView.isActive()).thenReturn(true);

        sTasks = Lists.newArrayList(new Task("Title1", "Description1"),
                new Task("Title2", "Description2", true),
                new Task("Title3", "Description3", true));
    }

    @Test
    public void createPresenter_setsThePresenterToView() {

        mTasksPresenter = new TasksPresenter(mTasksRepository, mTasksView);

        verify(mTasksView).setPresenter(mTasksPresenter);
    }

    @Test
    public void loadAllTasksFromRepositoryAndLoadIntoView() {

        mTasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
        mTasksPresenter.loadTasks(true);

        verify(mTasksRepository).getTasks(mLoadTasksCallbackArgumentCaptor.capture());
        mLoadTasksCallbackArgumentCaptor.getValue().onTasksLoaded(sTasks);

        InOrder inOrder = inOrder(mTasksView);
        inOrder.verify(mTasksView).setLoadingIndicator(true);

        inOrder.verify(mTasksView).setLoadingIndicator(false);

        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void loadActiveTasksFromRepositoryAndLoadIntoView() {
        mTasksPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
        mTasksPresenter.loadTasks(true);

        verify(mTasksRepository).getTasks(mLoadTasksCallbackArgumentCaptor.capture());
        mLoadTasksCallbackArgumentCaptor.getValue().onTasksLoaded(sTasks);

        verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 1);
    }

    @Test
    public void loadCompletedTasksFromRepositoryAndLoadIntoView() {
        mTasksPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
        mTasksPresenter.loadTasks(true);

        verify(mTasksRepository).getTasks(mLoadTasksCallbackArgumentCaptor.capture());
        mLoadTasksCallbackArgumentCaptor.getValue().onTasksLoaded(sTasks);

        verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 2);
    }

    @Test
    public void clickOnFab_ShowsAddTaskUi() {
        mTasksPresenter.addNewTask();

        verify(mTasksView).showAddTask();
    }

    @Test
    public void clickOnTask_ShowsDetailUi() {
        Task requestedTask = new Task("Details Requested", "For this task");

        mTasksPresenter.openTaskDetails(requestedTask);

        verify(mTasksView).showTaskDetailsUi(any(String.class));
    }

    @Test
    public void completeTask_ShowsTaskMarkedComplete() {
        Task task = new Task("Details Requested", "For this task");

        mTasksPresenter.completeTask(task);

        verify(mTasksRepository).completeTask(task);
        verify(mTasksView).showTaskMarkedComplete();
    }

    @Test
    public void activateTask_ShowsTaskMarkedActive() {
        Task task = new Task("Details Requested", "For this task", true);
        mTasksPresenter.loadTasks(true);

        mTasksPresenter.activateTask(task);

        verify(mTasksRepository).activateTask(task);
        verify(mTasksView).showTaskMarkedActive();
    }

    @Test
    public void unavailableTasks_ShowsError() {
        mTasksPresenter.setFiltering(TasksFilterType.ALL_TASKS);
        mTasksPresenter.loadTasks(true);

        verify(mTasksRepository).getTasks(mLoadTasksCallbackArgumentCaptor.capture());
        mLoadTasksCallbackArgumentCaptor.getValue().onDataNotAvailable();

        verify(mTasksView).showLoadingTasksError();
    }
}