package scheduling;

import taskData.Task;
import taskData.TaskType;

public class PollingServer extends Task {
	static int pollingServerID = 0;
	private int divisorIndex = 0;
	TaskSet ETSubset;

	public PollingServer(int computationTime, int serverPeriod, int serverDeadline, TaskSet etSubset,
			int separationRequirement, int divisorIndex) {
		super("serverPoll_" + pollingServerID, 7, computationTime, serverPeriod, serverDeadline, separationRequirement);
		ETSubset = etSubset;
		pollingServerID++;
		this.divisorIndex = divisorIndex;
		this.setTaskType(TaskType.PollingServer);
	}

	public void setDeadline(int deadline) {
		this.relativeDeadline = deadline;
	}

	public void setComputationTime(int computationTime) {
		this.computationTime = computationTime;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	/*
	 * public int getComputationTime() { return ComputationTime; }
	 * 
	 * public int getServerPeriod() { return ServerPeriod; }
	 * 
	 * public int getServerDeadline() { return ServerDeadline; }
	 */

	public TaskSet getETSubset() {
		return ETSubset;
	}

	public int getDivisorIndex() {
		return divisorIndex;
	}

	public void setDivisorIndex(int divisorIndex) {
		this.divisorIndex = divisorIndex;
	}

	public PollingServer createCopy() {
		return new PollingServer(this.computationTime, this.period, this.relativeDeadline, this.ETSubset.createCopy(),
				this.separationRequirement, this.divisorIndex);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + this.ETSubset.toString();
	}
}
