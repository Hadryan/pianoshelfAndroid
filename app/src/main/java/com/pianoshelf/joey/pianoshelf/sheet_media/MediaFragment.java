package com.pianoshelf.joey.pianoshelf.sheet_media;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pianoshelf.joey.pianoshelf.BaseFragment;
import com.pianoshelf.joey.pianoshelf.R;
import com.pianoshelf.joey.pianoshelf.composition.FullComposition;
import com.pianoshelf.joey.pianoshelf.profile.ProfileView;
import com.pianoshelf.joey.pianoshelf.rest_api.SubmittedBy;
import com.pianoshelf.joey.pianoshelf.sheet.SheetFrameView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.pianoshelf.joey.pianoshelf.profile.ProfileView.USERNAME_INTENT;

/**
 * Created by Me on 3/13/2017.
 * responsible for displaying detailed information about a sheet
 */

public class MediaFragment extends BaseFragment {
    public static final SheetFrameView.SheetFrameState mState = SheetFrameView.SheetFrameState.INFO;

    private TextView mTitle;
    private TextView mComposer;
    private TextView mDescription;

    private View mSubmitterDetails;
    private TextView mSubmitterUsername;
    private ImageView mSubmitterAvatar;

    private FullComposition mComposition;


    public static MediaFragment newInstance() {
        MediaFragment sheet = new MediaFragment();
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
        mTitle = (TextView) view.findViewById(R.id.media_title);
        mComposer = (TextView) view.findViewById(R.id.media_composer);
        mDescription = (TextView) view.findViewById(R.id.media_description);

        mSubmitterDetails = view.findViewById(R.id.media_submitter_details);
        mSubmitterAvatar = (ImageView) view.findViewById(R.id.media_submitter_avatar);
        mSubmitterUsername = (TextView) view.findViewById(R.id.media_submitter_username);


    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCompositionEvent(FullComposition sheetInfo) {
        mComposition = sheetInfo;

        mTitle.setText(sheetInfo.getTitle());
        mComposer.setText(sheetInfo.getComposer_name());
        mDescription.setText(sheetInfo.getLong_description());

        SubmittedBy submitter_info = sheetInfo.getSubmitted_by();
        final String submitterUsername = submitter_info.getUsername();
        mSubmitterUsername.setText(submitterUsername);
        Glide.with(getContext())
                .load(submitter_info.getSmall_profile_picture())
                .into(mSubmitterAvatar);
        mSubmitterDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileView.class);
                intent.putExtra(USERNAME_INTENT, submitterUsername);
                startActivity(intent);
            }
        });
    }

}
