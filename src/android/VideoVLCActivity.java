package VLCPlugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import java.io.Console;
import org.apache.cordova.*; 
public class VideoVLCActivity extends Activity implements IVideoPlayer {
    private static final String TAG = VideoVLCActivity.class.getSimpleName();

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    private SurfaceView mSurfaceView;
    private FrameLayout mSurfaceFrame;
    private SurfaceHolder mSurfaceHolder;
    private Surface mSurface = null;

    private LibVLC mLibVLC;

    private String mMediaUrl;

    private int layoutId;
    private int mSurfaceViewId;
    private int menuId;
    private int mSurfaceFrameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "VideoVLC -- onCreate -- START ------------");
        //setContentView(R.layout.activity_video_vlc);
        //setContentView(cordova.getActivity().getResources().getIdentifier("layout.activity_video_vlc", "layout", cordova.getActivity().getPackageName()));
        layoutId = getResources().getIdentifier("layout.activity_video_vlc", "layout", getPackageName());
        setContentView(layoutId);
        Log.v(TAG, "layoutId:"+layoutId);
        //console.Log("layoutId:"+layoutId);
        mSurfaceViewId=getResources().getIdentifier("layout.activity_video_vlc", "id.player_surface", getPackageName());
        ///mSurfaceView = (SurfaceView)findViewById(R.id.player_surface);
        Log.v(TAG, "mSurfaceViewId:"+mSurfaceViewId);
        mSurfaceView = (SurfaceView)findViewById(mSurfaceViewId);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceFrameId = getResources().getIdentifier("layout.activity_video_vlc", "id.player_surface_frame", getPackageName());
        //mSurfaceFrame = (FrameLayout)findViewById(R.id.player_surface_frame);
        Log.v(TAG, "mSurfaceFrameId:"+mSurfaceFrameId);
        mSurfaceFrame = (FrameLayout)findViewById(mSurfaceFrameId);
        mMediaUrl = getIntent().getExtras().getString("videoUrl");

        try {
            mLibVLC = new LibVLC();
            mLibVLC.setAout(mLibVLC.AOUT_AUDIOTRACK);
            mLibVLC.setVout(mLibVLC.VOUT_ANDROID_SURFACE);
            mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_FULL);

            mLibVLC.init(getApplicationContext());
        } catch (LibVlcException e){
            Log.e(TAG, e.toString());
        }

        mSurface = mSurfaceHolder.getSurface();

        mLibVLC.attachSurface(mSurface, VideoVLCActivity.this);
        mLibVLC.playMRL(mMediaUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // MediaCodec opaque direct rendering should not be used anymore since there is no surface to attach.
        mLibVLC.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuId= getResources().getIdentifier("menu.menu.menu_video_vlc", "menu.menu_video_vlc", getPackageName());
        Log.v(TAG, "menuId:"+menuId);
        getMenuInflater().inflate(menuId, menu);
        //getMenuInflater().inflate(R.menu.menu_video_vlc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    public void eventHardwareAccelerationError() {
        Log.e(TAG, "eventHardwareAccelerationError()!");
        return;
    }

    @Override
    public void setSurfaceLayout(final int width, final int height, int visible_width, int visible_height, final int sar_num, int sar_den){
        Log.d(TAG, "setSurfaceSize -- START");
        if (width * height == 0)
            return;

        // store video size
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;

        Log.d(TAG, "setSurfaceSize -- mMediaUrl: " + mMediaUrl + " mVideoHeight: " + mVideoHeight + " mVideoWidth: " + mVideoWidth + " mVideoVisibleHeight: " + mVideoVisibleHeight + " mVideoVisibleWidth: " + mVideoVisibleWidth + " mSarNum: " + mSarNum + " mSarDen: " + mSarDen);
    }

    @Override
    public int configureSurface(android.view.Surface surface, int i, int i1, int i2){
        return -1;
    }
}