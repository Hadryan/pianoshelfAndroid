package com.pianoshelf.joey.pianoshelf.comment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pianoshelf.joey.pianoshelf.BaseFragment;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.sheet.SheetFrameView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Me on 3/13/2017.
 */

public class CommentFragment extends BaseFragment {
    public static final SheetFrameView.SheetFrameState mState =
            SheetFrameView.SheetFrameState.COMMENT;

    // views
    private RecyclerView mRecyclerView;


    private List<Comment> mCommentList = new ArrayList<>();

    public static CommentFragment newInstance() {
        CommentFragment sheet = new CommentFragment();
        Bundle args = new Bundle();

        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sheet_media, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EventBus.getDefault().post(mState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.sheet_recycler);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCommentListEvent(List<Comment> commentList) {

    }
}
