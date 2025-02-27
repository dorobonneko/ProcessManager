/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package com.moe.processmanager;


import android.app.*;
import android.content.*;
import android.os.*;
import java.io.*;
import java.util.*;

import android.app.usage.IUsageStatsManager;
import android.os.Process;
import android.system.Os;
import android.view.inputmethod.InputMethodInfo;
import android.window.TaskSnapshot;
import com.android.internal.view.IInputMethodManager;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;

public class Grave extends ITaskStackListener.Stub implements Handler.Callback
{
	public static final int UID_OBSERVER_PROCSTATE = 1 << 0;

	public static final int UID_OBSERVER_GONE = 1 << 1;

	public static final int UID_OBSERVER_IDLE = 1 << 2;

	public static final int UID_OBSERVER_ACTIVE = 1 << 3;

	public static final int UID_OBSERVER_CACHED = 1 << 4;
	private IActivityTaskManager iatm;
	private IActivityManager iam;
	private PowerManager power;
	private IUsageStatsManager iusm;
	private Map<String, Task> tasks = new HashMap<>();
	private Handler mHandler;
	private IPackageManager ipm;
	private ActivityManager am;
	@Override
	public void onActivityPinned(String packageName, int userId, int taskId, int stackId) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityUnpinned() {
		// TODO: Implement this method
	}

	@Override
	public void onActivityRestartAttempt(ActivityManager.RunningTaskInfo task, boolean homeTaskVisible,
			boolean clearedTask, boolean wasVisible) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityForcedResizable(String packageName, int taskId, int reason) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityDismissingDockedTask() {
		// TODO: Implement this method
	}

	@Override
	public void onActivityLaunchOnSecondaryDisplayFailed(ActivityManager.RunningTaskInfo taskInfo,
			int requestedDisplayId) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityLaunchOnSecondaryDisplayRerouted(ActivityManager.RunningTaskInfo taskInfo,
			int requestedDisplayId) {
		// TODO: Implement this method
	}

	@Override
	public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) {
		// TODO: Implement this method
		//System.out.println("任务移到前端"+taskInfo.origActivity.getPackageName());
	}

	@Override
	public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityRequestedOrientationChanged(int taskId, int requestedOrientation) {
		// TODO: Implement this method
	}

	@Override
	public void onTaskRemovalStarted(ActivityManager.RunningTaskInfo taskInfo) {
		// TODO: Implement this method
		System.out.println("移除任务开始"+taskInfo.topActivity.getPackageName());
	}

	@Override
	public void onTaskProfileLocked(ActivityManager.RunningTaskInfo taskInfo, int userId) {
		// TODO: Implement this method
	}

	@Override
	public void onTaskSnapshotChanged(int taskId, TaskSnapshot snapshot) {
		// TODO: Implement this method
	}

	@Override
	public void onTaskSnapshotInvalidated(int taskId) {
		// TODO: Implement this method
	}

	@Override
	public void onBackPressedOnTaskRoot(ActivityManager.RunningTaskInfo taskInfo) {
		// TODO: Implement this method
	}

	@Override
	public void onTaskDisplayChanged(int taskId, int newDisplayId) {
		// TODO: Implement this method
	}

	@Override
	public void onTaskRequestedOrientationChanged(int taskId, int requestedOrientation) {
		// TODO: Implement this method
	}

	@Override
	public void onActivityRotation(int displayId) {
		// TODO: Implement this method
	}

	@Override
	public void onLockTaskModeChanged(int mode) {
		// TODO: Implement this method
	}
	@Override
	public void onRecentTaskListUpdated() {
		
	}

	@Override
	public void onTaskMovedToBack(ActivityManager.RunningTaskInfo taskInfo) {
		// TODO: Implement this method
		//System.out.println("任务移到后台"+taskInfo.topActivity.getPackageName());
	}

	@Override
	public void onTaskCreated(int taskId, ComponentName componentName) {
		// TODO: Implement this method
		if(power.isIgnoringBatteryOptimizations(componentName.getPackageName()))return;
		try
		{
			tasks.put(String.valueOf(taskId), new Task(taskId, ipm.getPackageUid(componentName.getPackageName(),0, 0), componentName.getPackageName()));
		}
		catch (RemoteException e)
		{}
		System.out.println("新任务:" + componentName.getPackageName());
	}

	@Override
	public void onRecentTaskListFrozenChanged(boolean frozen) {
		// TODO: Implement this method
		System.out.println("最近任务状态"+frozen);
	}

	@Override
	public void onTaskStackChanged() {
		// TODO: Implement this method
		//System.out.println("任务栈改变");
	}

	@Override
	public void onTaskRemoved(int taskId) {
		if(tasks.containsKey(String.valueOf(taskId))){
			Task task=tasks.remove(String.valueOf(taskId));
			if(tasks.containsKey(task.packageName))return;
			freezeCgroupUid(task.uid,true);
		}

	}

	@Override
	public void onTaskFocusChanged(int taskId, boolean focused) {
		if(tasks.containsKey(String.valueOf(taskId))){
			Task task=tasks.get(String.valueOf(taskId));
			System.out.println("focus"+task.packageName);
			freezeCgroupUid(task.uid,focused);
		}
	}

	public static void main(String[] args) {
		if (Os.getuid() == 2000 || Os.getuid() == 0) {
			System.out.println("uid:" + Os.getuid());
			Looper.prepare();
			try {
				new Grave();
			} catch (Throwable e) {
				e.printStackTrace();
				System.err.println("运行出错");
				System.exit(1);
			}
			
			System.out.println("正在后台运行");
			Looper.loop();
		}
		System.exit(1);
	}
	public Grave() {
		iatm = IActivityTaskManager.Stub.asInterface(ServiceManager.getService("activity_task"));
		IBinder binder = ServiceManager.getService("activity");
		if (Build.VERSION.SDK_INT > 26)
			iam = ActivityManagerNative.asInterface(binder);
		else
			iam = IActivityManager.Stub.asInterface(binder);
		power=(PowerManager) ActivityThread.systemMain().getApplication().getSystemService(Context.POWER_SERVICE);
		//System.out.println("最近任务列表更新");
		ipm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
		am=(ActivityManager) ActivityThread.systemMain().getApplication().getSystemService(Context.ACTIVITY_SERVICE);
		iusm = IUsageStatsManager.Stub.asInterface(ServiceManager.getService(Context.USAGE_STATS_SERVICE));
		IInputMethodManager imm=IInputMethodManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_METHOD_SERVICE));
		InputMethodInfo iinfo=imm.getCurrentInputMethodInfoAsUser(0);
		if(iinfo!=null)
		tasks.put(iinfo.getPackageName(),null);
		try {
			iam.registerUidObserver(new UidObserver(),UID_OBSERVER_ACTIVE| UID_OBSERVER_CACHED, -1, null);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		List<ActivityManager.RecentTaskInfo> list = iam
			.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED, 0).getList();
		Iterator<ActivityManager.RecentTaskInfo> i = list.iterator();
		while (i.hasNext()) {
			ActivityManager.RecentTaskInfo info=i.next();
			if(info.numActivities==0)continue;
			try
			{
				tasks.put(String.valueOf(info.taskId), new Task(info.taskId, ipm.getPackageUid(info.baseActivity.getPackageName(),0, 0), info.baseActivity.getPackageName()));
			}
			catch (RemoteException e)
			{}
			if (!info.isVisible())
{
				if(power.isIgnoringBatteryOptimizations(info.baseActivity.getPackageName()))continue;
				try
				{
					freezeCgroupUid(ipm.getPackageUid(info.baseActivity.getPackageName(),0, 0), true);
					System.out.println("墓碑"+info.baseActivity.getPackageName());
				}
				catch (RemoteException e)
				{}
			}
			
		}
		iatm.registerTaskStackListener(this);
	}
	class UidObserver extends IUidObserver.Stub {
		Set<String> history=new HashSet<>();
		File quit=new File("/sdcard/stopgrave");
		@Override
		public void onUidGone(int uid, boolean disabled) throws RemoteException {
			System.out.println("gone"+uid+disabled);
		}

		@Override
		public void onUidActive(int uid) throws RemoteException {
			System.out.println("active"+uid);
			Process1.freezeCgroupUid(uid,false);
			history.remove(String.valueOf(uid));
		}

		@Override
		public void onUidIdle(int uid, boolean disabled) throws RemoteException {
			System.out.println("idle"+uid+disabled);
		}

		@Override
		public void onUidStateChanged(int uid, int procState, long procStateSeq) throws RemoteException {
			System.out.println(procStateSeq);
		}

		@Override
		public void onUidCachedChanged(int uid, boolean cached) throws RemoteException {
			System.out.println("cache"+uid+cached);
			if(quit.exists()){
				Iterator<String> i=history.iterator();
				while(i.hasNext()){
					freezeCgroupUid(Integer.parseInt(i.next()),false);
				}
				System.out.println("程序已退出！！！");
				System.exit(0);
			}
			if(cached){
				history.add(String.valueOf(uid));
				mHandler.sendEmptyMessageDelayed(uid, 3000);
			}else{
				mHandler.removeMessages(uid);
				freezeCgroupUid(uid,cached);
				history.remove(String.valueOf(uid));
				//将非topapp的其它应用都暂停
				Iterator<ActivityManager.RunningAppProcessInfo> i=am.getRunningAppProcesses().iterator();
				while(i.hasNext()){
					ActivityManager.RunningAppProcessInfo info=i.next();
					if(power.isIgnoringBatteryOptimizations(info.pkgList[0]))continue;
					if(info.uid==uid)continue;
					freezeCgroupUid(info.uid,true);
					System.out.println(info.pkgList+"墓碑");
				}
				}
		}
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		freezeCgroupUid(msg.what,true);
		return true;
	}

	public void freezeCgroupUid(int uid,boolean freeze){
		Process1.freezeCgroupUid(uid,freeze);
	}
}

