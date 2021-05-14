package com.nampt.socialnetworkproject;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

public abstract class PaginationScrollListener implements ViewTreeObserver.OnScrollChangedListener {
    private NestedScrollView mScrollView;
    private LinearLayoutManager linearLayoutManager;

    public PaginationScrollListener(NestedScrollView mScrollView, LinearLayoutManager linearLayoutManager) {
        this.mScrollView = mScrollView;
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrollChanged() {
        {
            View view = (View) mScrollView.getChildAt(mScrollView.getChildCount() - 1);

            int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                    .getScrollY()));

            if (diff == 0) {
                // your pagination code
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (isLoading() || isLastPage()) {
                    return;
                }
                if (firstVisibleItemPosition >= 0 && (firstVisibleItemPosition + visibleItemCount) >= (totalItemCount)) {
                    loadMoreItems();
                }
            }
        }
    }

    public abstract void loadMoreItems();

    public abstract boolean isLoading();

    public abstract boolean isLastPage();
}
