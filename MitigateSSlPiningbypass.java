import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;

public class MitigateSSlPiningbypass {


    public static boolean CheckDevice(Context mainactivity , Class<?> cls){
        if(CheckWithSiginedKeyForvr(mainactivity , cls) && CheckwithModelForVr() || RootUtil.isDeviceRooted()){
            return true;
        }
        return false;
    }


    public static boolean CheckWithSiginedKeyForvr(Context context, Class<?> cls)
    {
        boolean result = false;
        try {
            ComponentName comp = new ComponentName(context, cls);
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature sigs[] = pinfo.signatures;
            for ( int i = 0; i < sigs.length;i++)
                Log.d(TAG,sigs[i].toCharsString());
            if (TAG.equals(sigs[0].toCharsString())) {
                result = true;
                Log.d(TAG,"package has been signed with the debug key");
            } else {
                Log.d(TAG,"package signed with a key other than the debug key");
            }

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        return result;

    }

    public static boolean CheckwithModelForVr() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }



    public static class RootUtil {
        public static boolean isDeviceRooted() {
            return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
        }

        private static boolean checkRootMethod1() {
            String buildTags = android.os.Build.TAGS;
            return buildTags != null && buildTags.contains("test-keys");
        }

        private static boolean checkRootMethod2() {
            String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
            for (String path : paths) {
                if (new File(path).exists()) return true;
            }
            return false;
        }

        private static boolean checkRootMethod3() {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if (in.readLine() != null) return true;
                return false;
            } catch (Throwable t) {
                return false;
            } finally {
                if (process != null) process.destroy();
            }
        }
    }

}
