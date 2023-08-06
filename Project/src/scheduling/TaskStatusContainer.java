package scheduling;

import java.util.HashMap;
import java.util.Set;

import taskData.Task;

public class TaskStatusContainer {

	private HashMap<Task, TaskCondition> taskStatusMap;

	public TaskStatusContainer(TaskSet ts) {
		taskStatusMap = new HashMap<Task, TaskCondition>();
		for (Task ti : ts.getTaskCollection()) {
			taskStatusMap.put(ti, new TaskCondition(ti));
		}
	}

	public HashMap<Task, TaskCondition> getTaskStatusMap() {
		return taskStatusMap;
	}

	public Set<Task> getKeyset() {
		return taskStatusMap.keySet();
	}

	public TaskCondition get(Task taskKey) {
		return taskStatusMap.get(taskKey);
	}

	public HashMap<Task, Integer> getWCRT() {
		HashMap<Task, Integer> WCRTs = new HashMap<Task, Integer>();
		for (Task t : taskStatusMap.keySet()) {
			TaskCondition values = taskStatusMap.get(t);
			WCRTs.put(t, values.getWCRT());
		}
		return WCRTs;
	}

	public boolean hasTaskWithCompDuration() {
		// Returns true if a task still has remaining computation time.
		boolean hasRemainingComputationDuration = false;
		for (Task t : this.getKeyset()) {
			TaskCondition values = this.get(t);
			hasRemainingComputationDuration = hasRemainingComputationDuration || (values.getComputationDuration() > 0);
		}
		return hasRemainingComputationDuration;
	}

	public Task EDF(int time) {
		Task earliestTask = null;
		int earliestFinish = Integer.MAX_VALUE;
		for (Task t : this.getKeyset()) {
			TaskCondition values = this.get(t);
			int currentTaskDeadline = values.getDeadline();
			if (currentTaskDeadline < time) {
				continue;
			}
			if (currentTaskDeadline < earliestFinish && values.getComputationDuration() > 0) {
				earliestFinish = currentTaskDeadline;
				earliestTask = t;
			}
		}
		return earliestTask;
	}

}
