package scheduling;

import taskData.Task;

class TaskCondition {
	private int remainingCompDuration;
	private int deadline;
	private int releaseTime = 0;
	private int WCRT = 0;

	public TaskCondition(Task task) {
		this.remainingCompDuration = task.getComputationTime();
		this.deadline = task.getDeadline();
	}

	public void subtractComputationDuration(int workTime) {
		this.remainingCompDuration -= workTime;
	}

	public void setComputationDuration(int computationDuration) {
		this.remainingCompDuration = computationDuration;
	}

	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}

	public void setReleaseTime(int releaseTime) {
		this.releaseTime = releaseTime;
	}

	public void setWCRT(int WCRT) {
		this.WCRT = WCRT;
	}

	public int getComputationDuration() {
		return remainingCompDuration;
	}

	public int getReleaseTime() {
		return releaseTime;
	}

	public int getWCRT() {
		return WCRT;
	}

	public int getDeadline() {
		return deadline;
	}
}