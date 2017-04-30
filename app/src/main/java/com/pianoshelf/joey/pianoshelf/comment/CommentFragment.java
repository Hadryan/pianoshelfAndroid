package com.pianoshelf.joey.pianoshelf.comment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.recycler.ListRecycler;
import com.pianoshelf.joey.pianoshelf.recycler.RecyclerFragment;
import com.pianoshelf.joey.pianoshelf.sheet.SheetFrameView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Me on 3/13/2017.
 */

public class CommentFragment extends RecyclerFragment {
    public static final SheetFrameView.SheetFrameState mState =
            SheetFrameView.SheetFrameState.COMMENT;
    private static final String LOG_TAG = "CommentFragment";
    // views
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new CommentRecycler(R.layout.adapter_comment_list_item, mCommentList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EventBus.getDefault().post(mState);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCommentListEvent(Comment[] commentList) {
        mCommentList.clear();
        mCommentList.addAll(Arrays.asList(commentList));
        mAdapter.setList(mCommentList);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView body;
        ImageView avatar;
        TextView date;
        View replyButton;
        View editButton;
        View deleteButton;
        private LinearLayout mRootView;

        private boolean mLeftPadInit = false;

        public CommentViewHolder(View view) {
            super(view);

            mRootView = (LinearLayout) view;
            username = (TextView) view.findViewById(R.id.comment_username);
            body = (TextView) view.findViewById(R.id.comment_body);
            avatar = (ImageView) view.findViewById(R.id.comment_avatar);
            date = (TextView) view.findViewById(R.id.comment_date);
            replyButton = view.findViewById(R.id.comment_reply);
            editButton = view.findViewById(R.id.comment_edit);
            deleteButton = view.findViewById(R.id.comment_delete);
        }

        public void bind(Comment comment) {
            username.setText(comment.getSubmitted_by().getUsername());
            body.setText(comment.getMessage());
            Glide.with(mRootView.getContext())
                    .load(comment.getSubmitted_by().getSmall_profile_picture())
                    .into(avatar);
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(comment.getDate().getTime());
            date.setText(timeAgo);

            if (!mLeftPadInit) {
                // dynamically create and add padding to start of comment view
                int pixelPadMargin = mRootView.getResources().getDimensionPixelSize(R.dimen.comment_start_depth_margin);
                int pixelPadWidth = mRootView.getResources().getDimensionPixelSize(R.dimen.comment_start_depth_width);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        pixelPadWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMarginStart(pixelPadMargin);
                for (int i = 0; i < comment.getDepth(); i++) {
                    View leftPadding = new View(mRootView.getContext());
                    leftPadding.setBackgroundResource(R.color.pianoshelf_grey_dark);
                    mRootView.addView(leftPadding, 0, layoutParams);
                }
                mLeftPadInit = true;
            }
        }
    }

    class CommentRecycler extends ListRecycler<CommentViewHolder, Comment> {
        private int mLayout;

        public CommentRecycler(int layout, Collection<Comment> commentColl) {
            super(commentColl);
            mLayout = layout;
        }

        @Override
        public void setList(Collection<? extends Comment> coll) {
            // maps commentId -> Comment
            SparseArray<Comment> idCommentMap = new SparseArray<>();
            for (Comment comment : coll) {
                idCommentMap.append(comment.getId(), comment);
            }

            List<Comment> treeOrderedComments = new ArrayList<>();
            for (Comment comment : coll) {
                if (comment.getRecipient() == null) {
                    comment.setDepth(0);
                    treeOrderedComments.add(comment);
                    collectReplies(treeOrderedComments, idCommentMap, comment, 1);
                }
            }

            super.setList(treeOrderedComments);
        }

        private void collectReplies(List<Comment> commentList,
                                    SparseArray<Comment> idCommentMap,
                                    Comment comment, int depth) {
            for (int replyId : comment.getReplies()) {
                Comment replyComment = idCommentMap.get(replyId);
                if (replyComment != null) {
                    replyComment.setDepth(depth);
                    commentList.add(replyComment);
                    if (replyComment.getReplies() != null) {
                        collectReplies(commentList, idCommentMap, replyComment, depth + 1);
                    }
                } else {
                    Log.e(LOG_TAG, "Failed to find Comment object with reply id " + replyId);
                    throw new RuntimeException("Invalid reply id");
                }
            }
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            holder.bind(mList.get(position));
            holder.replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO
                }
            });
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(mLayout, parent, false);
            return new CommentViewHolder(view);
        }
    }
}
