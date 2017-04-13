package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.ui.adapters.SuperStarAdapter;
import com.knigego.nimo.demoregister.ui.model.SuperStarWrapper;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;
import com.knigego.nimo.demoregister.util.DeviceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/3/29.
 */

public class StageSuperStarTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {

    private SuperStarAdapter mAdapter;

    public StageSuperStarTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new SuperStarViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        SuperStarViewHolder superStarViewHolder = (SuperStarViewHolder) viewHolder;
        SuperStarWrapper superStarWrapper = (SuperStarWrapper) data;
        mAdapter = new SuperStarAdapter(mContext, superStarWrapper.getSuperStarItems());
        superStarViewHolder.mSuperStarRecyclerView.setAdapter(mAdapter);
    }

    public class SuperStarViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.recyclerView_super_star)
        RecyclerView mSuperStarRecyclerView;

        public SuperStarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mSuperStarRecyclerView.setLayoutManager(layoutManager);
            mSuperStarRecyclerView.addItemDecoration(new SpaceItemDecoration(DeviceUtils.dipToPX(mContext, 8),
                    mContext.getResources().getDrawable(R.drawable.icon_star_line)));

        }

    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int px;
        private Drawable mDrawable;

        public SpaceItemDecoration(int px, Drawable drawable) {
            super();
            this.px = px;
            mDrawable = drawable;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            drawHorizontal(c, parent);
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            int bottom = parent.getHeight() - parent.getPaddingBottom();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (i == (childCount - 1)) {
                    return;
                }

                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int left = child.getRight() + params.rightMargin;
                int right = left + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left - (mDrawable.getIntrinsicWidth() - px) / 2,
                        (bottom / 2) - (mDrawable.getIntrinsicHeight() / 2),
                        left - (mDrawable.getIntrinsicWidth() - px) / 2 + mDrawable.getIntrinsicWidth(),
                        (bottom / 2) + (mDrawable.getIntrinsicHeight() / 2));
                mDrawable.draw(c);

            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, px, 0);
        }
    }
}
