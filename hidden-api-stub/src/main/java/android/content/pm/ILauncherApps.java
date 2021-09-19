package android.content.pm;

import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import androidx.annotation.RequiresApi;

public interface ILauncherApps extends IInterface {

     void addOnAppsChangedListener(IOnAppsChangedListener listener)
            throws RemoteException
			
     void addOnAppsChangedListener(String callingPackage, IOnAppsChangedListener listener)
            throws RemoteException
			
     boolean startShortcut(String callingPackage, String packageName, String id,
                          Rect sourceBounds, Bundle startActivityOptions, int userId)
            throws RemoteException
			
     boolean startShortcut(String callingPackage, String packageName, String featureId, String id,
                          Rect sourceBounds, Bundle startActivityOptions, int userId)throws RemoteException

    abstract class Stub extends Binder implements ILauncherApps {

        public static ILauncherApps asInterface(IBinder obj) {
            throw new RuntimeException();
        }

        @Override
        public IBinder asBinder() {
            throw new RuntimeException();
        }
    }
}
