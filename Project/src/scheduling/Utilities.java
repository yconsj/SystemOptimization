package scheduling;

import java.util.ArrayList;

import taskData.Task;

public class Utilities {

	static String scheduleToString(Task[] schedule) {
		StringBuilder strB = new StringBuilder();
		for (Task t : schedule) {
			if (t == null) {
				strB.append("idle \n ");
			} else {
				strB.append(t.toString() + " \n ");
			}
		}
		return strB.toString();
	}

	// https://www.geeksforgeeks.org/find-all-factors-of-a-natural-number/
	public static ArrayList<Integer> getDivisors(int n) {
		ArrayList<Integer> divisors = new ArrayList<Integer>();
		for (int i = 1; i <= n; i++) {
			if (n % i == 0)
				divisors.add(i);
		}
		return divisors;
	}

	public static ArrayList<TaskSet> divideTaskSet(TaskSet tset, int divisions) {
		ArrayList<Task> taskList = new ArrayList<Task>(tset.getTaskCollection());
		ArrayList<TaskSet> dividedSets = new ArrayList<TaskSet>();
		final int subsetSize = taskList.size() / divisions;
		for (int part = 0; part < divisions; part++) {
			TaskSet dTSet = new TaskSet();
			int maxIdx = (part + 1) * subsetSize;
			if (part == divisions - 1) {
				maxIdx = taskList.size();
			}
			for (int idx = part * subsetSize; idx < maxIdx; idx++) {
				dTSet.appendTask(taskList.get(idx));
			}
			dividedSets.add(dTSet);
		}

		return dividedSets;

	}

}
