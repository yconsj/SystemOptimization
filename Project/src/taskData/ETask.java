package taskData;

public class ETask extends Task {

	public ETask(String taskName, int taskPriority, int computationTime, int period, int relativeDeadline,
			int separationRequirement) {
		super(taskName, taskPriority, computationTime, period, relativeDeadline, separationRequirement);
		this.setTaskType(TaskType.EventTriggered);
	}

}
