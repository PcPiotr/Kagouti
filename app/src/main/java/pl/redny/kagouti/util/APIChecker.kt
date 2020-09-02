package pl.redny.kagouti.util

import android.os.Build
import android.util.Log
import eu.chainfire.libsuperuser.Shell
import java.io.File
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Class responsible for checking API version of Android.
 */
object APIChecker {
    /**
     * TAG for logcat logging.
     */
    private const val TAG = "APIChecker"

    /**
     * [String] array consisting of potential su folder paths.
     * Required for Root access check.
     */
    private val rootPaths = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    /**
     * Method checks if API level is 23 or newer.
     *
     * @return [Boolean] true if API level is high enough.
     */
    fun isMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * Method checks if API level is 24 or newer.
     *
     * @return [Boolean] true if API level is high enough.
     */
    fun isNougat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    /**
     * Method checks if API level is 26 or newer.
     *
     * @return [Boolean] true if API level is high enough.
     */
    fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /**
     * Method checks if API level is 28 or newer.
     *
     * @return [Boolean] true if API level is high enough.
     */
    fun isPie() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /**
     * Method checks if API level is 29 or newer.
     *
     * @return [Boolean] true if API level is high enough.
     */
    fun isQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * Method checks for Root access.
     * Uses three different methods, might give false positives.
     * @return [Boolean] true if system has unlocked Root access.
     */
    fun isRooted(): Boolean {
//        if (Shell.SU.available()) {
//            return true
//        }

        if (Build.TAGS != null && Build.TAGS.contains("test-keys")) {
            Log.i(TAG, "First check positive")
            return true
        }

        rootPaths.forEach {
            if (File(it).exists()) {
                Log.i(TAG, "Second check positive")
                return true
            }
        }

        var process: Process? = null
        return try {
            Log.i(TAG, "executiong /system/xbin/which, su")
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val input = BufferedReader(InputStreamReader(process!!.inputStream))

            input.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }
}