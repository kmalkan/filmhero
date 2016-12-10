package com.amazon.filmhero.listener;

import android.widget.AbsListView;

/**
 * Created by malkan on 12/1/2016.
 */

public abstract class EndlessGridScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 5;
    private int currentPage;
    private int previousTotalItemCount;
    private boolean loading = true;
    private int startingPageIndex;

    public EndlessGridScrollListener() {
    }

    public EndlessGridScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    protected EndlessGridScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
        }
    }

    public abstract boolean onLoadMore(int page, int totalItemsCount);

}
