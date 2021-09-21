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

public class Main
{
	public static final int UID_OBSERVER_PROCSTATE = 1<<0;

    public static final int UID_OBSERVER_GONE = 1<<1;

    public static final int UID_OBSERVER_IDLE = 1<<2;

    public static final int UID_OBSERVER_ACTIVE = 1<<3;

    public static final int UID_OBSERVER_CACHED = 1<<4;
	
	private IActivityManager iam;
	private IPackageManager ipm;
	private IUsageStatsManager iusm;
	private List<String> whiteList=new ArrayList<>();
	public static void main(String[] args){
		if(Os.getuid()==2000||Os.getuid()==0){
			System.out.println("uid"+Os.getuid());
		Looper.prepare();
		try{
		new Main();
		}catch(Throwable e){
			e.printStackTrace();
		}
		Looper.loop();
		}
		System.exit(1);
	}
	public Main(){
		IInputMethodManager imm=IInputMethodManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_METHOD_SERVICE));
		for(InputMethodInfo info:imm.getEnabledInputMethodList(0)){
			whiteList.add(info.getPackageName());
		}
		iusm=IUsageStatsManager.Stub.asInterface(ServiceManager.getService(Context.USAGE_STATS_SERVICE));
		IBinder binder=ServiceManager.getService("activity");
		if(Build.VERSION.SDK_INT>26)
			iam=ActivityManagerNative.asInterface(binder);
			else
		    iam=IActivityManager.Stub.asInterface(binder);
		ipm=IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
	    
		try
		{
			iam.registerUidObserver(new UidObserver(), UID_OBSERVER_CACHED|UID_OBSERVER_GONE|UID_OBSERVER_IDLE, -1, null);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
	
	class UidObserver extends IUidObserver.Stub
	{
		@Override
		public void onUidGone(int uid, boolean disabled) throws RemoteException
		{
			if(uid>10000&&uid<65535){
				for(String packageName:ipm.getPackagesForUid(uid)){
					if(whiteList.contains(packageName))continue;
					ApplicationInfo ai=ipm.getApplicationInfo(packageName,PackageManager.MATCH_UNINSTALLED_PACKAGES,0);
					if((ai.flags&ai.FLAG_SYSTEM)==0||(ai.flags&ai.FLAG_UPDATED_SYSTEM_APP)!=0){
						iam.forceStopPackage(packageName,0);
						System.out.println("gone:kill:"+packageName);
					}
				}
			}
		}

		@Override
		public void onUidActive(int uid) throws RemoteException
		{
			
		}

		@Override
		public void onUidIdle(int uid, boolean disabled) throws RemoteException
		{
			onUidCachedChanged(uid,disabled);
		}

		@Override
		public void onUidStateChanged(int uid, int procState, long procStateSeq) throws RemoteException
		{
			
		}

		@Override
		public void onUidCachedChanged(int uid, boolean cached) throws RemoteException
		{
			
			if(uid>10000&&uid<65535){
				for(String packageName:ipm.getPackagesForUid(uid)){
					iusm.setAppInactive(packageName,true,0);
				}
			}
		}
		
		
	}
}
