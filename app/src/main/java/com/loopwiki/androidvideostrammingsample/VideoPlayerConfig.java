package com.loopwiki.androidvideostrammingsample;

/**
 * Created by amardeep on 12/24/2017.
 */

public class VideoPlayerConfig {
    //Minimum Video you want to buffer while Playing
    public static final int MIN_BUFFER_DURATION = 25000;
    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 30000;
    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 10000;
    //Min video You want to buffer when user resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 10000;
    //Video Url
    public static final String VIDEO_URL = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4";
}
