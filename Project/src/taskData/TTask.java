package taskData;

public class TTask extends Task {

	public TTask(String taskName, int taskPriority, int computationTime, int period, int relativeDeadline,
			int separationRequirement) {
		super(taskName, taskPriority, computationTime, period, relativeDeadline, separationRequirement);
		this.setTaskType(TaskType.TimeTriggered);
	}

}
