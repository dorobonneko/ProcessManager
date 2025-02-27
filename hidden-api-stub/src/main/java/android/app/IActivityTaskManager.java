/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package android.app;
import android.os.*;
import android.graphics.*;
import java.util.*;

public interface IActivityTaskManager extends IInterface
{
	boolean removeTask(int taskId);
	void removeAllVisibleRecentTasks();
	Rect getTaskBounds(int taskId);
	void registerTaskStackListener(ITaskStackListener listener);
    void unregisterTaskStackListener(ITaskStackListener listener);
	android.window.TaskSnapshot getTaskSnapshot(
		int taskId, boolean isLowResolution);
	List<IBinder> getAppTasks(String callingPackage);
	
	abstract class Stub extends Binder implements IActivityTaskManager {

        public static IActivityTaskManager asInterface(IBinder obj) {
            throw new RuntimeException("STUB");
        }
    }
}
