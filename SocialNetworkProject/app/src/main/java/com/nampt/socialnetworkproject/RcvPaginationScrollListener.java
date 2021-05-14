package com.nampt.socialnetworkproject;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RcvPaginationScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager linearLayoutManager;

    public RcvPaginationScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
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

    public abstract void loadMoreItems();

    public abstract boolean isLoading();

    public abstract boolean isLastPage();


}
