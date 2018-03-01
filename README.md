# VLCPlugin
a cordova plugin to use VLC for play video only for android  base on org.videolan.libvlc

 cordova.plugins.VLCPlugin.play(url,result =>{
      console.log("result:",result);
    },error =>console.log("error:",error));

attention:you need make sure android:targetSdkVersion="22",if you targetSdkVersion above 22, LibVLC: Can't load vlcjni library: java.lang.UnsatisfiedLinkError: dlopen failed: /lib/arm/libvlcjni.so: has text relocations
