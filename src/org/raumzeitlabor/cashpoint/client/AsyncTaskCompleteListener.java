package org.raumzeitlabor.cashpoint.client;

public interface AsyncTaskCompleteListener {
	public void onTaskStart();
	public void onTaskError(Exception error);
	public <T> void onTaskComplete(T result);
}
