package com.knigego.nimo.demoregister.ui.model;

import com.crooods.wd.dto.version.ActionCommentDto;
import com.knigego.nimo.demoregister.uimanager.ItemViewTypeHelperManager;

/**
 * Created by ThinkPad on 2017/4/5.
 */

public class CommentItem extends ItemViewTypeHelperManager.ItemViewData {
    public CommentItem(ActionCommentDto actionCommentDto , ItemViewTypeHelperManager.ItemViewTypeHelper itemViewTypeHelper) {
        mItemViewTypeHelper = itemViewTypeHelper;
        mActionCommentDto = actionCommentDto;
    }

    public ActionCommentDto getActionCommentDto() {
        return mActionCommentDto;
    }

    public void setActionCommentDto(ActionCommentDto actionCommentDto) {
        mActionCommentDto = actionCommentDto;
    }

    private ActionCommentDto mActionCommentDto;
}
