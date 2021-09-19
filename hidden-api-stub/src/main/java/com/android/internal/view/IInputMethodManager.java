package com.android.internal.view;
import java.util.List;
import android.view.inputmethod.InputMethodInfo;
import android.os.Binder;
import android.os.IBinder;

public interface IInputMethodManager
{
	List<InputMethodInfo> getEnabledInputMethodList(int userId)
	public static abstract class Stub extends Binder implements IInputMethodManager{
		public static IInputMethodManager asInterface(IBinder binder){
			throw new RuntimeException("STUB");
		}
	}
}
