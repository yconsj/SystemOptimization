package scheduling;

import java.util.HashSet;
import java.util.Set;

import taskData.Task;

public class TaskSet {

	public Set<Task> taskSet;

	public Set<Task> getTaskCollection() {
		return taskSet;
	}

	public void setTaskCollection(Set<Task> newTaskSet) {
		this.taskSet = newTaskSet;
	}

	public TaskSet() {
		this.taskSet = new HashSet<Task>();
	}

	public TaskSet(Set<Task> taskSet) {
		this.taskSet = taskSet;
	}

	public void appendTask(Task task) {
		this.taskSet.add(task);
	}

	public int getSetSize() {
		return this.getTaskCollection().size();
	}

	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (Task task : (this.taskSet)) {
			strBuilder.append(task.toString() + "\n");
		}
		return strBuilder.toString();
	}

	public TaskSet unionTaskSet(TaskSet taskset2) {
		// Returns a new task set which is the union of this task set, and parameter
		// taskset2
		Set<Task> newTaskSet = new HashSet<Task>(taskset2.getTaskCollection());
		newTaskSet.addAll(this.taskSet);
		return new TaskSet(newTaskSet);
	}

	public int getSumComputationTime() {
		int sumTime = 0;
		for (Task task : getTaskCollection()) {
			sumTime += task.getComputationTime();
		}
		return sumTime;
	}

	public TaskSet createCopy() {

		HashSet<Task> clonedTaskSet = new HashSet<Task>();
		for (Task task : this.taskSet) {
			clonedTaskSet.add(task);
		}
		return new TaskSet(clonedTaskSet);
	}

}
