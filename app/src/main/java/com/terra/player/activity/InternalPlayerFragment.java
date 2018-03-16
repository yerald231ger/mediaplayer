package com.terra.player.activity;

import com.terra.player.fragment.PlayerFragment;

/**
 * Created by Gerardo.Sanchez on 13/03/2018.
 */

public class InternalPlayerFragment extends PlayerFragment {

    static PlayerFragment newInternal(double aspectRatio, String videoUri, String imgUri, boolean fullScreenActivity, boolean fragmentInFullScreenActivity) {
        return PlayerFragment.newInstance(aspectRatio, videoUri, imgUri, fullScreenActivity, fragmentInFullScreenActivity, null);
    }
}
