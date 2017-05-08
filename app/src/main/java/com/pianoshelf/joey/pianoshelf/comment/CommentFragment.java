package com.pianoshelf.joey.pianoshelf.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.authentication.AuthClickWrapper;
import com.pianoshelf.joey.pianoshelf.recycler.ListRecycler;
import com.pianoshelf.joey.pianoshelf.recycler.RecyclerFragment;
import com.pianoshelf.joey.pianoshelf.sheet.SheetFrameView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
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
    public static final String COMMENT_INIT = "CommentInit";
    private static final String LOG_TAG = "CommentFragment";
    // views
    private List<Comment> mCommentList = new ArrayList<>();

    public static CommentFragment newInstance() {
        CommentFragment sheet = new CommentFragment();
        Bundle args = new Bundle();

        sheet.setArguments(args);
        return sheet;
    }

    public static CommentFragment newInstance(String commentJson) {
        CommentFragment sheet = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(COMMENT_INIT, commentJson);

        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        try {
            String commentInit = args.getString(COMMENT_INIT, "");
            Log.e(LOG_TAG, "comment frag init " + commentInit);
            if (!"".equals(commentInit)) {
                Log.e(LOG_TAG, "in equals");
                Comment initComment = new ObjectMapper().readValue(commentInit, Comment.class);
                mCommentList.add(initComment);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to deserialize comment json " + e.getLocalizedMessage());
        }
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

        EventBus.getDefault().removeStickyEvent(Comment[].class);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public boolean mLeftPadInit = false;
        TextView username;
        TextView body;
        ImageView avatar;
        TextView date;
        View replyButton;
        View editButton;
        View deleteButton;
        private LinearLayout mRootView;

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
        }
    }

    class CommentRecycler extends ListRecycler<CommentViewHolder, Comment> {
        private int mLayout;
        private SparseIntArray mIdDepthMap = new SparseIntArray();

        public CommentRecycler(int layout, Collection<Comment> commentColl) {
            super(commentColl);
            mLayout = layout;
        }

        @Override
        public void setList(Collection<? extends Comment> coll) {
            // maps commentId -> Comment
            SparseArray<Comment> idCommentMap = new SparseArray<>();
            for (Comment comment : coll) {
                int id = comment.getId();
                idCommentMap.append(id, comment);
            }

            mIdDepthMap.clear();
            List<Comment> treeOrderedComments = new ArrayList<>();
            treeOrderedComments.clear();
            for (Comment comment : coll) {
                if (comment.getRecipient() == null) {
                    mIdDepthMap.put(comment.getId(), 0);
                    treeOrderedComments.add(comment);
                    collectReplies(treeOrderedComments, idCommentMap, mIdDepthMap, comment, 1);
                }
            }

            super.setList(treeOrderedComments);
        }

        private void collectReplies(List<Comment> commentList,
                                    SparseArray<Comment> idCommentMap,
                                    SparseIntArray idDepthMap,
                                    Comment comment, int depth) {
            for (int replyId : comment.getReplies()) {
                Comment replyComment = idCommentMap.get(replyId);
                if (replyComment != null) {
                    idDepthMap.put(replyId, depth);
                    commentList.add(replyComment);
                    if (replyComment.getReplies() != null) {
                        collectReplies(commentList, idCommentMap, idDepthMap, replyComment, depth + 1);
                    }
                } else {
                    Log.e(LOG_TAG, "Failed to find Comment object with reply id " + replyId);
                    throw new RuntimeException("Invalid reply id");
                }
            }
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, final int position) {
            Comment comment = mList.get(position);
            holder.bind(comment);

            if (!holder.mLeftPadInit) {
                // dynamically create and add padding to start of comment view
                // NOTE bind() gets called every time the root view is recycled.
                // However 2nd time and onwards the padding is already present
                int pixelPadMargin = holder.mRootView.getResources().getDimensionPixelSize(R.dimen.comment_start_depth_margin);
                int pixelPadWidth = holder.mRootView.getResources().getDimensionPixelSize(R.dimen.comment_start_depth_width);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        pixelPadWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setMarginStart(pixelPadMargin);
                for (int i = 0; i < mIdDepthMap.get(comment.getId()); i++) {
                    View leftPadding = new View(holder.mRootView.getContext());
                    leftPadding.setBackgroundResource(R.color.pianoshelf_grey_dark);
                    holder.mRootView.addView(leftPadding, 0, layoutParams);
                }
                holder.mLeftPadInit = true;
            }

            // no auth no reply
            holder.replyButton.setOnClickListener(
                    new AuthClickWrapper(getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Comment original = mList.get(position);
                        Intent intent = new Intent(getContext(), CommentReplyView.class);
                        String commentString = new ObjectMapper().writeValueAsString(original);
                        intent.putExtra(COMMENT_INIT, commentString);
                        startActivity(intent);
                    } catch (JsonProcessingException e) {
                        Log.e(LOG_TAG, "Failed while serializing comment json " + e.getLocalizedMessage());
                    }
                }
                    }));
            // TODO the authenticated user is only allowed to edit or delete their own posts
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new CommentDeleteEvent());
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

    public class CommentDeleteEvent {
    }
}
