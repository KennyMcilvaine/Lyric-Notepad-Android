package kmcilvai.perfectpoet;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
/**
 * Created by kenny on 2/27/2018.
 */

public class MyPrefsBackupAgent extends BackupAgentHelper {
    // The name of the SharedPreferences file
    static final String PREFS = "lyricPref";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "backupForLyricApp";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}