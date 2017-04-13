package com.knigego.nimo.demoregister.ui.typehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.knigego.nimo.demoregister.R;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class CommentCountItemTypeHelper extends ItemViewTypeHelperManager.ItemViewTypeHelper {
    public CommentCountItemTypeHelper(Context context, int layout) {
        super(context, layout);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new CommentCountViewHolder(view);
    }

    @Override
    public void updateData(RecyclerView.ViewHolder viewHolder, ItemViewTypeHelperManager.ItemViewData data, int position) {
        CommentCountViewHolder holder = (CommentCountViewHolder) viewHolder;
        CommentCountData commentCountData = (CommentCountData) data;
        if (commentCountData.count <= 0) {
            holder.mTextCommentCount.setText(R.string.not_comment);
        } else {
            holder.mTextCommentCount.setText(String.format(mContext.getString(R.string.comment_count),commentCountData.count));
        }
    }

    static class CommentCountViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.text_comment_count)
        TextView mTextCommentCount;

        CommentCountViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class CommentCountData extends ItemViewTypeHelperManager.ItemViewData{
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
