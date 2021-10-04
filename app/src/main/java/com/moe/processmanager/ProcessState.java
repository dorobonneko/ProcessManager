package com.moe.processmanager;

public abstract class  ProcessState
{
	public static final int PROCESS_STATE_UNKNOWN = -1;

    /** @hide Process is a persistent system process. */
    public static final int PROCESS_STATE_PERSISTENT = 0;

    /** @hide Process is a persistent system process and is doing UI. */
    public static final int PROCESS_STATE_PERSISTENT_UI = 1;

    /** @hide Process is hosting the current top activities.  Note that this covers
     * all activities that are visible to the user. */
    
    public static final int PROCESS_STATE_TOP = 2;

    /** @hide Process is bound to a TOP app. This is ranked below SERVICE_LOCATION so that
     * it doesn't get the capability of location access while-in-use. */
    public static final int PROCESS_STATE_BOUND_TOP = 3;

    /** @hide Process is hosting a foreground service. */
   public static final int PROCESS_STATE_FOREGROUND_SERVICE = 4;

    /** @hide Process is hosting a foreground service due to a system binding. */
    public static final int PROCESS_STATE_BOUND_FOREGROUND_SERVICE = 5;

    /** @hide Process is important to the user, and something they are aware of. */
    public static final int PROCESS_STATE_IMPORTANT_FOREGROUND = 6;

    /** @hide Process is important to the user, but not something they are aware of. */
    public static final int PROCESS_STATE_IMPORTANT_BACKGROUND = 7;

    /** @hide Process is in the background transient so we will try to keep running. */
    public static final int PROCESS_STATE_TRANSIENT_BACKGROUND = 8;

    /** @hide Process is in the background running a backup/restore operation. */
    public static final int PROCESS_STATE_BACKUP = 9;

    /** @hide Process is in the background running a service.  Unlike oom_adj, this level
     * is used for both the normal running in background state and the executing
     * operations state. */
    public static final int PROCESS_STATE_SERVICE = 10;

    /** @hide Process is in the background running a receiver.   Note that from the
     * perspective of oom_adj, receivers run at a higher foreground level, but for our
     * prioritization here that is not necessary and putting them below services means
     * many fewer changes in some process states as they receive broadcasts. */
    public static final int PROCESS_STATE_RECEIVER = 11;

    /** @hide Same as {@link #PROCESS_STATE_TOP} but while device is sleeping. */
    public static final int PROCESS_STATE_TOP_SLEEPING = 12;

    /** @hide Process is in the background, but it can't restore its state so we want
     * to try to avoid killing it. */
    public static final int PROCESS_STATE_HEAVY_WEIGHT = 13;

    /** @hide Process is in the background but hosts the home activity. */
    public static final int PROCESS_STATE_HOME = 14;

    /** @hide Process is in the background but hosts the last shown activity. */
    public static final int PROCESS_STATE_LAST_ACTIVITY = 15;

    /** @hide Process is being cached for later use and contains activities. */
    public static final int PROCESS_STATE_CACHED_ACTIVITY = 16;

    /** @hide Process is being cached for later use and is a client of another cached
     * process that contains activities. */
    public static final int PROCESS_STATE_CACHED_ACTIVITY_CLIENT = 17;

    /** @hide Process is being cached for later use and has an activity that corresponds
     * to an existing recent task. */
    public static final int PROCESS_STATE_CACHED_RECENT = 18;

    /** @hide Process is being cached for later use and is empty. */
    public static final int PROCESS_STATE_CACHED_EMPTY = 19;

    /** @hide Process does not exist. */
    public static final int PROCESS_STATE_NONEXISTENT = 20;
	
	public static final boolean isProcStateBackground(int procState) {
        return procState >= PROCESS_STATE_TRANSIENT_BACKGROUND;
    }

    /** @hide Is this a foreground service type? */
    public static boolean isForegroundService(int procState) {
        return procState == PROCESS_STATE_FOREGROUND_SERVICE;
    }
	
}
