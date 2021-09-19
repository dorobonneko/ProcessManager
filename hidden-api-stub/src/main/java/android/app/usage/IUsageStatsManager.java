package android.app.usage;
import android.os.Binder;
import android.os.IBinder;

public interface IUsageStatsManager
{
	void setAppInactive(String packageName, boolean inactive, int userId);
    boolean isAppInactive(String packageName, int userId, String callingPackage);
	
	public static abstract class Stub extends Binder implements IUsageStatsManager{
		public static IUsageStatsManager asInterface(IBinder binder){
			throw new RuntimeException("STUB");
		}
	}
}
