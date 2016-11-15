package com.example.test;

import android.util.Log;
import android.widget.AbsListView;

public abstract class MyScrollListener implements AbsListView.OnScrollListener {
    // The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 8;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    private boolean isFirst = false;

    public MyScrollListener() {
    }

    public MyScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public MyScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    /*@Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        Log.d("MyScrollListener", "On Listener");
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
            Log.d("MyScrollListener", "Creating");

        }
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
            Log.d("MyScrollListener", "Refreshing");
        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
            loading = onLoadMore(currentPage + 1, totalItemCount);
            Log.d("MyScrollListener", "Must Load");
        }
    }*/

    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    //public abstract boolean onLoadMore(int page, int totalItemsCount);

    public  abstract  boolean onLoadMore(int page);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        //Log.d("SCROLL", "YES");
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!isFirst)
            isFirst = true;
        else {
                if (!loading) {
                   // Log.d("FIRST VIVIBLE ITEM", ((Integer) (firstVisibleItem)).toString());
                    if (firstVisibleItem + visibleItemCount + visibleThreshold >= totalItemCount) {
                        onLoadMore(totalItemCount);
                    } else if (firstVisibleItem - visibleThreshold < 0) {
                        onLoadMore(0);
                        loading = true;
                    }
                    //Log.d("Total item count --1-- ", ((Integer) (firstVisibleItem - visibleThreshold)).toString());
                    //Log.d("Total item count --2-- ", ((Integer) (totalItemCount)).toString());
                }


        }
        previousTotalItemCount = totalItemCount;
    }
}