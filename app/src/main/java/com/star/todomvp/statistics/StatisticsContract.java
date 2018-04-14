package com.star.todomvp.statistics;

import com.star.todomvp.BasePresenter;
import com.star.todomvp.BaseView;

public interface StatisticsContract {

    interface View extends BaseView<Presenter> {

        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

    }
}
