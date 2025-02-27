/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package com.moe.processmanager;

import android.app.*;
import android.os.*;
import java.util.*;

import android.app.usage.IUsageStatsManager;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.view.inputmethod.InputMethodInfo;
import com.android.internal.view.IInputMethodManager;

public class Tast
{
	private IActivityTaskManager iatm;
	private IActivityManager iam;
	private PowerManager power;
	private IUsageStatsManager iusm;
	private Map<String, Task> tasks = new HashMap<>();
	private Handler mHandler;
	private IPackageManager ipm;
	private ActivityManager am;
	public Tast() {
		System.out.println("enter");
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
		System.out.println("recent");
		List<ActivityManager.RecentTaskInfo> list = iam
			.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED, 0).getList();
		Iterator<ActivityManager.RecentTaskInfo> i = list.iterator();
		while (i.hasNext()) {
			ActivityManager.RecentTaskInfo info=i.next();
			System.out.println("rexcent"+info.toString());

		}
		Iterator<ActivityManager.RunningAppProcessInfo> ii=am.getRunningAppProcesses().iterator();
		while(ii.hasNext()){
			ActivityManager.RunningAppProcessInfo info=ii.next();
			System.out.println("running"+info.pkgList[0]);
		}
		Iterator<ActivityManager.AppTask> iii=am.getAppTasks().iterator();
		while(iii.hasNext()){
			ActivityManager.AppTask info=iii.next();
			System.out.println("app"+info);
		}
		System.exit(0);
	}
	public static void main(String[] args) {
		System.out.println("test");
		Looper.prepare();
		new Tast();
		Looper.loop();
		}
}
