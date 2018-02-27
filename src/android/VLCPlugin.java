package VLCPlugin;
import org.apache.cordova.CordovaPlugin;
import java.io.Console;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetFileDescriptor;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.VideoView;
import android.widget.TextView;
import android.util.Log;
import android.content.Intent;
import android.content.Context;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import VLCPlugin.VideoVLCActivity;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
/**
 * This class echoes a string called from JavaScript.
 */
public class VLCPlugin extends CordovaPlugin implements IVideoPlayer {
    protected static final String LOG_TAG = "VideoPlayer";

    protected static final String ASSETS = "/android_asset/";

    private CallbackContext callbackContext = null;

    private Dialog dialog;

    public String mess;

    private static final String TAG = VLCPlugin.class.getSimpleName();
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
    
    

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("play")) {
            String message = args.getString(0);
            this.play(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void play(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
            mess = message;
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    //openTextDialog(mess);
                    openVLC(mess);
                    //open2VLC(mess);
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void openTextDialog(String path) {
        
        Context context = this.cordova.getActivity().getApplicationContext();
        // Let's create the main dialog
        dialog = new Dialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        //dialog.setOnDismissListener(this);
        //dialog.setOnDismissListener(context);

        // Main container layout
        LinearLayout main = new LinearLayout(cordova.getActivity());
        main.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
        main.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        main.setVerticalGravity(Gravity.CENTER_VERTICAL);
        //TextView tv=new TextView();
        TextView textView = new TextView(cordova.getActivity());
        textView.setText(path);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // videoView.setVideoURI(uri);
        // videoView.setVideoPath(path);
        main.addView(textView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(main);
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    protected void openVLC(String path) {
        Log.v("tta" ,path);
        Context context = this.cordova.getActivity().getApplicationContext();
        // Let's create the main dialog
        dialog = new Dialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        // Main container layout
        LinearLayout main = new LinearLayout(cordova.getActivity());
        main.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
        main.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        main.setVerticalGravity(Gravity.CENTER_VERTICAL);
        mSurfaceView = new SurfaceView(cordova.getActivity());
        mSurfaceView
                .setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mSurfaceHolder = mSurfaceView.getHolder();
        //mMediaUrl = getIntent().getExtras().getString(path);
        //mMediaUrl = cordova.getActivity().getIntent().getExtras().getString(path);
        mMediaUrl=path;
        try {
            this.mLibVLC = new LibVLC();
            mLibVLC.setAout(mLibVLC.AOUT_AUDIOTRACK);
            mLibVLC.setVout(mLibVLC.VOUT_ANDROID_SURFACE);
            mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_FULL);

            //mLibVLC.init(getApplicationContext());
            mLibVLC.init(context);
        } catch (LibVlcException e) {
            Log.e(TAG, e.toString());
        }
        mSurface = mSurfaceHolder.getSurface();

        //mLibVLC.attachSurface(mSurface, VideoVLCActivity.this);
        mLibVLC.attachSurface(mSurface, VLCPlugin.this);
        mLibVLC.playMRL(mMediaUrl);

        main.addView(mSurfaceView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(main);
        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }
    protected void open2VLC(String path) {
        Context context = this.cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, VideoVLCActivity.class);
        intent.putExtra("videoUrl", path);
        context.startActivity(intent);
    }
    public void eventHardwareAccelerationError() {
        Log.e(TAG, "eventHardwareAccelerationError()!");
        return;
    }

    @Override
    public void setSurfaceLayout(final int width, final int height, int visible_width, int visible_height,
            final int sar_num, int sar_den) {
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

        Log.d(TAG,
                "setSurfaceSize -- mMediaUrl: " + mMediaUrl + " mVideoHeight: " + mVideoHeight + " mVideoWidth: "
                        + mVideoWidth + " mVideoVisibleHeight: " + mVideoVisibleHeight + " mVideoVisibleWidth: "
                        + mVideoVisibleWidth + " mSarNum: " + mSarNum + " mSarDen: " + mSarDen);
    }

    @Override
    public int configureSurface(android.view.Surface surface, int i, int i1, int i2) {
        return -1;
    }
}
