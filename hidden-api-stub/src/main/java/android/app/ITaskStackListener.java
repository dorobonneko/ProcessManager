package android.app;
import android.os.*;

public interface ITaskStackListener
{
	void onRecentTaskListUpdated();
	abstract class Stub extends Binder implements ITaskStackListener{
	}
}
