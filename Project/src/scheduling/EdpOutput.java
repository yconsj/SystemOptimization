package scheduling;

public class EdpOutput {
	/*
	 * TODO: Change the name to be more closely tied with algo2 (EDP), or possibly
	 * combine this with the Output class, so we just have a single tuple result
	 * class.
	 */

	boolean schedulable;
	int responseTime;

	EdpOutput(boolean schedulable, int responseTime) {
		this.schedulable = schedulable;
		this.responseTime = responseTime;
	}

	public boolean getSchedulable() {
		return schedulable;
	}

	public int getResponseTime() {
		return responseTime;
	}

	public String toString() {
		return "isSchedulable:" + this.schedulable + ";responseTime:" + this.responseTime;

	}
}