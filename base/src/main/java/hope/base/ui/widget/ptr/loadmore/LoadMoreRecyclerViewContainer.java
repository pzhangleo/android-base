package hope.base.ui.widget.ptr.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Loadmore for RecyclerView
 */
public class LoadMoreRecyclerViewContainer extends LoadMoreContainerBase {

    private BaseQuickAdapter mBaseQuickAdapter;
    private RecyclerView mRecyclerView;

    public LoadMoreRecyclerViewContainer(Context context) {
        super(context);
    }

    public LoadMoreRecyclerViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void addFooterView(View view) {
        if (mBaseQuickAdapter == null) {
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter instanceof BaseQuickAdapter) {
                mBaseQuickAdapter = (BaseQuickAdapter) adapter;
            }
        }
        if (mBaseQuickAdapter != null) {
            mBaseQuickAdapter.addFooterView(view);
        } else {
            throw new NullPointerException("Adapter is null!");
        }
    }

    @Override
    protected void removeFooterView(View view) {
        try {
            if (mBaseQuickAdapter != null) {
                mBaseQuickAdapter.removeFooterView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected RecyclerView retrieveAbsView() {
        mRecyclerView = (RecyclerView) getChildAt(0);
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof BaseQuickAdapter) {
            mBaseQuickAdapter = (BaseQuickAdapter) adapter;
        }
        return mRecyclerView;
    }

}
