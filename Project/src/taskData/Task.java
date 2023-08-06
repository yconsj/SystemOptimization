package taskData;

public abstract class Task {

	private String taskName;

	protected TaskType ttype;
	protected int taskPriority, computationTime, period, relativeDeadline, separationRequirement;

	public Task(String taskName, int taskPriority, int computationTime, int period, int relativeDeadline,
			int separationRequirement) {
		this.taskName = taskName;
		this.taskPriority = taskPriority;
		this.computationTime = computationTime;
		this.period = period;
		this.relativeDeadline = relativeDeadline;
		this.separationRequirement = separationRequirement;
	}

	public int getSeparationRequirement() {
		return separationRequirement;
	}

	public String getTaskName() {
		return taskName;
	}

	public TaskType getTaskType() {
		return ttype;
	}

	protected void setTaskType(TaskType ttype) {
		this.ttype = ttype;
	}

	public int getTaskPriority() {
		return taskPriority;
	}

	public int getComputationTime() {
		return computationTime;
	}

	public int getPeriod() {
		return period;
	}

	public int getDeadline() {
		return relativeDeadline;
	}

	@Override
	public int hashCode() {
		return taskName.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (this.getClass() != o.getClass())
			return false;
		Task task2 = (Task) o;
		return taskName == task2.taskName && taskPriority == task2.taskPriority && period == task2.period
				&& relativeDeadline == task2.relativeDeadline;
	}

	@Override
	public String toString() {
		return String.format(";%s;%s;%s;%s;%s;%s;%s", taskName, computationTime, period, ttype.toString(), taskPriority,
				relativeDeadline, separationRequirement);
	}

}
