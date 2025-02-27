package com.moe.processmanager;
import android.os.Looper;
import android.app.IActivityManager;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.Build;
import android.app.ActivityManagerNative;
import android.app.IProcessObserver;
import android.os.RemoteException;
import android.app.IUidObserver;
import android.app.ActivityManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import android.util.ArraySet;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.view.IInputMethodManager;
import android.content.Context;
import android.view.inputmethod.InputMethodInfo;
import android.app.usage.IUsageStatsManager;
import android.system.Os;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.util.Iterator;
import java.io.PrintStream;
import java.io.IOException;
import android.text.TextUtils;
import android.app.*;
import java.util.*;
import android.os.*;
import android.app.ActivityManager.*;
import android.content.*;
import android.window.*;

public class Main implements Handler.Callback {
	public static final int UID_OBSERVER_PROCSTATE = 1 << 0;

	public static final int UID_OBSERVER_GONE = 1 << 1;

	public static final int UID_OBSERVER_IDLE = 1 << 2;

	public static final int UID_OBSERVER_ACTIVE = 1 << 3;

	public static final int UID_OBSERVER_CACHED = 1 << 4;
	private IActivityTaskManager iatm;
	private IActivityManager iam;
	private IPackageManager ipm;
	private IUsageStatsManager iusm;
	private Set<String> whiteList = new ArraySet<>(), blackList = new ArraySet<>(), recents = new ArraySet<>();
	private HashMap<String, Boolean> apps = new HashMap<>();
	private HashMap<String,String> tasks=new HashMap<>();
	//private PrintStream shell;
	private Handler mHandler = new Handler(this);
	//private static final String RENICE="renice +%d -u u0_a%d&";
	public static void main(String[] args) {
		if (Os.getuid() == 2000 || Os.getuid() == 0) {
			System.out.println("uid:" + Os.getuid());
			Looper.prepare();
			try {
				new Main(args);
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
	public <T> T cast(Object o) {
		return (T) o;
	}
	public Main(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			switch (args[i]) {
				case "-b" :
					try {
						for (String pkg : args[i + 1].replaceAll(" ", "").split(",|\\||:|\n")) {
							if (TextUtils.isEmpty(pkg))
								continue;
							blackList.add(pkg.trim());
							System.out.println("黑名单：" + pkg);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					break;
				case "-w" :
					try {
						for (String pkg : args[i + 1].replaceAll(" ", "").split(",|\\||:|\n")) {
							if (TextUtils.isEmpty(pkg))
								continue;
							whiteList.add(pkg.trim());
							System.out.println("白名单:" + pkg);
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
					break;
			}

		}

		/*IInputMethodManager imm=IInputMethodManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_METHOD_SERVICE));
		for (InputMethodInfo info:imm.getEnabledInputMethodList(0))
		{
			System.out.println("输入法：" + info.getPackageName());
			whiteList.add(info.getPackageName());
		}*/
		iusm = IUsageStatsManager.Stub.asInterface(ServiceManager.getService(Context.USAGE_STATS_SERVICE));
		System.out.println("应用待机" + iusm.isAppStandbyEnabled());
		IBinder binder = ServiceManager.getService("activity");
		if (Build.VERSION.SDK_INT > 26)
			iam = ActivityManagerNative.asInterface(binder);
		else
			iam = IActivityManager.Stub.asInterface(binder);
		System.out.println("freezer"+iam.isAppFreezerSupported()+"|"+iam.isAppFreezerEnabled());
		
		iatm=IActivityTaskManager.Stub.asInterface(ServiceManager.getService("activity_task"));
		ipm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
		//onRecentTaskListUpdated();
		//iam.registerTaskStackListener(this);
		/*try {
			iam.registerUidObserver(new UidObserver(), UID_OBSERVER_CACHED | UID_OBSERVER_GONE, -1, null);
		} catch (RemoteException e) {
			e.printStackTrace();
		}*/

	}

	
	@Override
	public boolean handleMessage(Message param1Message) {
		String packageName = (String) param1Message.obj;
		if (iusm.isAppInactive(packageName, 0, null))
			System.out.println("绿色应用：" + packageName);
		else {
			try {
				iam.forceStopPackage(packageName, 0);
				System.out.println("激进：" + packageName);
			} catch (RemoteException e) {
			}
		}
		return true;
	}

	class UidObserver extends IUidObserver.Stub {
		private void kill(String packageName) throws RemoteException {
			if (blackList.contains(packageName)) {
				iam.forceStopPackage(packageName, 0);
				System.out.println("blacklist:kill:" + packageName);
				return;
			}
			if (whiteList.contains(packageName))
				return;
			if (packageName.startsWith("com.google.android."))
				return;
			if (!apps.containsKey(packageName)) {
				ApplicationInfo ai = ipm.getApplicationInfo(packageName, PackageManager.MATCH_UNINSTALLED_PACKAGES, 0);
				apps.put(packageName,
						((ai.flags & ai.FLAG_SYSTEM) == 0 || (ai.flags & ai.FLAG_UPDATED_SYSTEM_APP) != 0));
			}
			if (apps.get(packageName)) {
				iam.forceStopPackage(packageName, 0);
				System.out.println("kill:" + packageName);
			}
		}
		private void onChanged() {

		}
		@Override
		public void onUidGone(int uid, boolean disabled) throws RemoteException {
			if (uid > 10000 && uid < 65535 && !disabled) {
				for (String packageName : ipm.getPackagesForUid(uid)) {
					kill(packageName);
				}
			}
		}

		@Override
		public void onUidActive(int uid) throws RemoteException {
			/*if (uid < 10000 && uid < 65535)
			 {
			 //renice +0 -u u0_axxx
			 shell.println("renice +0 -u u0_a" + uid % 10000+"&");
			 shell.flush();
			 }*/
		}

		@Override
		public void onUidIdle(int uid, boolean disabled) throws RemoteException {
			/*if (!disabled)
				check(uid);
			else if(!iam.isUidActive(uid,null))
				check(uid);*/
		}

		@Override
		public void onUidStateChanged(int uid, int procState, long procStateSeq) throws RemoteException {
			System.out.println(procStateSeq);
		}

		@Override
		public void onUidCachedChanged(int uid, boolean cached) throws RemoteException {

			//System.out.println("cached:"+uid+cached);
			check(uid, cached);
		}

		private void check(int uid, boolean cached) throws RemoteException {
			if (cached && uid > 10000 && uid < 65535) {
				for (String packageName : ipm.getPackagesForUid(uid)) {
					iusm.setAppInactive(packageName, true, 0);
					System.out.println(packageName + iusm.isAppInactive(packageName, 0, "com.android.shell"));
					/*
					if (whiteList.contains(packageName))continue;
					if (packageName.startsWith("com.google.android."))return;
					if(packageName.startsWith("com.android."))return;
					if(packageName.startsWith("com.qualcomm."))return;
					onChanged();
					iusm.setAppInactive(packageName, true, 0);
					if (!recents.contains(packageName))
					{
						if (blackList.contains(packageName))
						{
							iam.forceStopPackage(packageName, 0);
							System.out.println("blacklist:kill:" + packageName);
						}
						else if(cached){
							mHandler.removeMessages(packageName.hashCode());
						mHandler.sendMessageDelayed(mHandler.obtainMessage(packageName.hashCode(),packageName),2000);
						}else{
							iam.forceStopPackage(packageName, 0);
							System.out.println("active no Recent:kill:" + packageName);
						}
					}*/
				}
			}
		}

	}
}

