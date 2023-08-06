package taskData;

public class TaskFactory {
	public static Task getTask(String taskName, int computationTime, int period, TaskType ttype, int taskPriority,
			int relativeDeadline, int separationRequirement) {
		if (ttype == TaskType.TimeTriggered) {
			return new TTask(taskName, taskPriority, computationTime, period, relativeDeadline, separationRequirement);
		}
		if (ttype == TaskType.EventTriggered) {
			return new ETask(taskName, taskPriority, computationTime, period, relativeDeadline, separationRequirement);
		}
		return null;

	}

}
