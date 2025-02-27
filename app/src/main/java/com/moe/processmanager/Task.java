/**
 * @Author dorobonneko
 * @AIDE AIDE+
*/
package com.moe.processmanager;

public class Task{
	public int uid,taskId;
	public String packageName;

	public Task(int uid, int taskId, String packageName)
	{
		this.uid = uid;
		this.taskId = taskId;
		this.packageName = packageName;
	}
}
