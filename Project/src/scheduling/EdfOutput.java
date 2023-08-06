package scheduling;

import java.util.Collection;
import java.util.HashMap;

import taskData.Task;

public class EdfOutput {
	private Task[] schedule;
	private HashMap<Task, Integer> WCRTs;
	private int missedComputation;

	public EdfOutput(Task[] schedule, HashMap<Task, Integer> WCRTs, int missedComputation) {
		this.schedule = schedule;
		this.WCRTs = WCRTs;
		this.missedComputation = missedComputation;
	}

	public Collection<Integer> getWCRTSet() {
		return this.WCRTs.values();
	}

	public Task[] getSchedule() {
		return schedule;
	}

	public float getAvgWCRT() {
		int sum = 0;
		for (int wcrt : this.WCRTs.values()) {
			sum += wcrt;
		}
		float avg = (float) sum / (float) this.WCRTs.size();
		return avg;
	}

	public int getMissedComputation() {
		return missedComputation;
	}

	public boolean isSchedulable() {
		return missedComputation == 0; 
	}

	public String toString() {
		String outputString = "";
		for (int i = 0; i < schedule.length; i++) {
			Task t = schedule[i];
			if (t == null) {
			} else {
				outputString += "time:" + i + "; " + t.toString() + "; WCRT:" + WCRTs.get(t).toString() + "\n";
			}
		}

		return outputString;
	}

}