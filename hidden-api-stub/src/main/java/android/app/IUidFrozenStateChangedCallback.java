/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package android.app;

public interface IUidFrozenStateChangedCallback{
	void onUidFrozenStateChanged(int[] uids,int[] frozenStates);
	
}
