package com.pianoshelf.joey.pianoshelf.sheet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pianoshelf.joey.pianoshelf.BaseActivity;
import com.pianoshelf.joey.pianoshelf.C;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.comment.Comment;
import com.pianoshelf.joey.pianoshelf.comment.CommentFragment;
import com.pianoshelf.joey.pianoshelf.composition.FullComposition;
import com.pianoshelf.joey.pianoshelf.composition.SimpleComposition;
import com.pianoshelf.joey.pianoshelf.rest_api.DetailMeta;
import com.pianoshelf.joey.pianoshelf.rest_api.MetaData;
import com.pianoshelf.joey.pianoshelf.rest_api.RW;
import com.pianoshelf.joey.pianoshelf.rest_api.RWCallback;
import com.pianoshelf.joey.pianoshelf.rest_api.ShelfSheetMusic;
import com.pianoshelf.joey.pianoshelf.sheet_media.MediaFragment;
import com.pianoshelf.joey.pianoshelf.shelf.Shelf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.pianoshelf.joey.pianoshelf.sheet.SheetFragment.SHEET_ID_INTENT;

/**
 * Created by Me on 3/13/2017.
 * Holds important fragments
 * Sheetmusic fragment
 * Comment fragment
 * Details fragment
 *
 */

public class SheetFrameView extends BaseActivity {
    public static final String LOG_TAG = "SheetFrameView";

    // Views ( from the six )
    private ImageView mSheet;
    private ImageView mShelfStatus;
    private ImageView mComment;
    private ImageView mInfo;
    private SheetFrameState mState = SheetFrameState.UNKNOWN;

    // Fragments
    private SheetFragment mSheetFragment;
    private CommentFragment mCommentFragment;
    private MediaFragment mInfoFragment;
    private long mSheetId;

    // States
    private boolean mSheetInShelf = false;

    // Variables
    private FullComposition mComposition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheetview);

        mSheet = (ImageView) findViewById(R.id.sheet_bottom_shelf_sheet);
        mShelfStatus = (ImageView) findViewById(R.id.sheet_bottom_shelf_status);
        mComment = (ImageView) findViewById(R.id.sheet_bottom_comment);
        mInfo = (ImageView) findViewById(R.id.sheet_bottom_info);

        mSheetId = getIntent().getLongExtra(SHEET_ID_INTENT, -1);
        mSheetFragment = SheetFragment.newInstance();
        mCommentFragment = CommentFragment.newInstance();
        mInfoFragment = MediaFragment.newInstance();

        replaceMainView(mSheetFragment);

        // register listeners
        mSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceMainView(mSheetFragment);
            }
        });
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceMainView(mCommentFragment);
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceMainView(mInfoFragment);
            }
        });
        mShelfStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSPHelper.isLoggedIn()) {
                    if (mSheetInShelf) {
                        removeFromShelf(SheetFrameView.this);
                    } else {
                        addToShelf(SheetFrameView.this);
                    }
                } else {
                    Toast.makeText(SheetFrameView.this,
                            "Signup to favourite this sheet music.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        requestShelf();
        requestSheet();
        requestCommentList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Kill sticky event regardless
        EventBus.getDefault().removeStickyEvent(FullComposition.class);
    }

    private void replaceMainView(Fragment frag) {
        if (frag instanceof SheetState) {
            if (((SheetState) frag).getState() == mState) {
                return;
            } else {
                // Assign mState here to prevent repeated state changes by double tapping the button
                mState = ((SheetState) frag).getState();
            }
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sheet_frame, frag)
                .commitNow();
    }

    @Subscribe
    public void setState(SheetFrameState state) {
        mState = state;
        Log.i(LOG_TAG, "State change " + mState);
    }

    private void requestSheet() {
        apiService.getSheet((int) mSheetId)
                .enqueue(new RWCallback<RW<FullComposition, MetaData>>(200, true) {
                    @Override
                    public void onFailure(Call<RW<FullComposition, MetaData>> call, Throwable t) {
                        setTitle("Error while loading sheet");
                        t.printStackTrace();
                        Log.e(C.NET, "Sheet music request failed. " + t.getLocalizedMessage());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCompositionEvent(FullComposition sheetInfo) {
        mComposition = sheetInfo;
        // Set actionbar title
        setTitle(sheetInfo.getTitle());
    }

    public void requestShelf() {
        if (mSPHelper.isLoggedIn()) {
            apiService.getShelf(mSPHelper.getUser())
                    .enqueue(new RWCallback<RW<Shelf, MetaData>>() {
                        @Override
                        public void onFailure(Call<RW<Shelf, MetaData>> call, Throwable t) {
                            Log.e(C.NET, "Shelf api request failure " + t.getLocalizedMessage());
                            logout();
                        }
                    });
        } else {
            Log.e(LOG_TAG, "Triggered shelf request without authentication");
        }
    }

    // Handle requestShelf Response here
    @Subscribe
    public void handleShelf(Shelf shelf) {
        Log.e(LOG_TAG, "GOT a shelf " + shelf.getClass().toString());
        int index = 0;
        List<SimpleComposition> compositionList = shelf.getSheetmusic();
        for (; index < compositionList.size(); ++index) {
            if (compositionList.get(index).getId() == mSheetId) {
                Log.d(C.AUTH, "Sheet found in shelf");
                mSheetInShelf = true;
                break;
            }
        }
        // sheet not found
        if (index == compositionList.size()) {
            mSheetInShelf = false;
        }

        // update shelf status
        shelfStatusUpdate();
    }

    public void addToShelf(final Context context) {
        apiService.shelfAddSheet(new ShelfSheetMusic((int) mSheetId))
                .enqueue(new RWCallback<RW<Shelf, MetaData>>(new Integer[]{200, 201}) {
                    @Override
                    public void onResponse(Call<RW<Shelf, MetaData>> call, Response<RW<Shelf, MetaData>> response) {
                        super.onResponse(call, response);
                        Toast.makeText(context,
                                R.string.add_sheet_shelf_success,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<RW<Shelf, MetaData>> call, Throwable t) {
                        Toast.makeText(context,
                                "Failed to add sheet to shelf.",
                                Toast.LENGTH_LONG).show();
                        Log.e(C.NET, "Shelf add request failed. " + t.getLocalizedMessage());
                    }
                });
    }

    public void removeFromShelf(final Context context) {
        apiService.shelfRemoveSheet(new ShelfSheetMusic((int) mSheetId))
                .enqueue(new RWCallback<RW<Shelf, MetaData>>(200) {
                    @Override
                    public void onResponse(Call<RW<Shelf, MetaData>> call, Response<RW<Shelf, MetaData>> response) {
                        super.onResponse(call, response);
                        Toast.makeText(context,
                                R.string.remove_sheet_shelf_success,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<RW<Shelf, MetaData>> call, Throwable t) {
                        Toast.makeText(context,
                                "Failed to remove sheet to shelf.",
                                Toast.LENGTH_LONG).show();
                        Log.e(C.NET, "Shelf add request failed. " + t.getLocalizedMessage());
                    }
                });
    }

    public void shelfStatusUpdate() {
        if (mSheetInShelf) {
            mShelfStatus.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            mShelfStatus.setImageResource(R.drawable.ic_star_border_black_24dp);
        }
    }

    private void requestCommentList() {
        apiService.getComment((int) mSheetId)
                .enqueue(new RWCallback<RW<Comment[], DetailMeta>>(true) {
                    @Override
                    public void onFailure(Call<RW<Comment[], DetailMeta>> call, Throwable t) {
                        Log.e(C.NET, "Failed to retrieve comments " + t.getLocalizedMessage());
                    }
        });
    }

    // State
    public enum SheetFrameState {
        UNKNOWN,
        SHEET,
        COMMENT,
        INFO,
    }


    public interface SheetState {
        SheetFrameView.SheetFrameState getState();
    }
}
