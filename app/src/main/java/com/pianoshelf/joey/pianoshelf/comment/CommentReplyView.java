package com.pianoshelf.joey.pianoshelf.comment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.rest_api.DetailMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;

import java.io.IOException;

import retrofit2.Call;

import static com.pianoshelf.joey.pianoshelf.comment.CommentFragment.COMMENT_INIT;

/**
 * Created by Me on 5/7/2017.
 */

public class CommentReplyView extends BaseActivity {
    private static final String LOG_TAG = "CommentReplyView";

    private EditText mReply;
    private View mSend;

    private Comment mOriginalComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply);

        mReply = (EditText) findViewById(R.id.comment_reply_edit);
        mSend = findViewById(R.id.comment_reply_submit);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReply(mReply.getText().toString());
            }
        });

        Intent intent = getIntent();
        String commentJson = intent.getStringExtra(COMMENT_INIT);
        Log.e(LOG_TAG, commentJson);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mOriginalComment = mapper.readValue(commentJson, Comment.class);
            // disable depth finding algorithm
            mOriginalComment.setRecipient(null);
            mOriginalComment.setReplies(null);
            commentJson = mapper.writeValueAsString(mOriginalComment);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Jackson failed while mangling comment json " + e.getLocalizedMessage());
            abortOnFailure();
            return;
        }

        CommentFragment commentFragment = CommentFragment.newInstance(commentJson);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.comment_reply_frame, commentFragment)
                .commitNow();
    }

    private void submitReply(String reply) {
        if (TextUtils.isEmpty(reply)) {
            return;
        }
        apiService.commentAdd(new CommentPost(reply,
                mOriginalComment.getSheetmusic(), mOriginalComment.getId())).enqueue(
                new RWCallback<RW<Comment[], DetailMeta>>() {
                    @Override
                    public void onFailure(Call<RW<Comment[], DetailMeta>> call, Throwable t) {
                        Log.e(C.NET, "Failed to submit reply " + t.getLocalizedMessage());
                        abortOnFailure();
                    }
                });
        finish();
    }

    private void abortOnFailure() {
        Toast.makeText(this, "Reply Failed", Toast.LENGTH_SHORT).show();
        finish();
    }

}
