package scheduling;

import java.util.Set;

import taskData.Task;

public class EdfSimulator {

	public static EdfOutput algorithm1(TaskSet TT, TaskSet Tpoll) {
		TaskSet UnionTSet = TT.unionTaskSet(Tpoll);
		int hyperPeriod = Schedulor.lcm(UnionTSet.getTaskCollection());

		TaskStatusContainer taskStatMap = new TaskStatusContainer(UnionTSet);
		int currentTime = 0;

		Task[] sigma = new Task[hyperPeriod]; 
		final Set<Task> keysetTasks = taskStatMap.getKeyset();
		int missedComputation = 0;

		while (currentTime < hyperPeriod) {
			int nextEventTime = Integer.MAX_VALUE;
			for (Task t : keysetTasks) {
				TaskCondition values = taskStatMap.get(t);
				if ((values.getComputationDuration() > 0) && (values.getDeadline() <= currentTime)) {
					missedComputation += values.getComputationDuration();
					values.setWCRT(values.getComputationDuration());
					values.setComputationDuration(0);
				}
				// find time until next release of current task:
				int taskTimeUntilRelease = t.getPeriod() - (currentTime % t.getPeriod());
				nextEventTime = Math.min(taskTimeUntilRelease, nextEventTime);
				nextEventTime = Math.min(values.getDeadline(), nextEventTime);

				// If task's period has cycled back, reset it (except WCRT):
				if (currentTime % t.getPeriod() == 0) { 
					values.setReleaseTime(currentTime);

					values.setComputationDuration(t.getComputationTime());
					values.setDeadline(currentTime + t.getDeadline());
				}
			}
			if (!taskStatMap.hasTaskWithCompDuration()) {
				for (int time = currentTime; time < nextEventTime; time++) {
					sigma[time] = null; // Idle, when we have no tasks do work on at current time.
				}
			} else {
				Task t = taskStatMap.EDF(currentTime);

				TaskCondition values = taskStatMap.get(t);

				nextEventTime = Math.min(values.getComputationDuration(), nextEventTime);
				
				for (int time = currentTime; time < (currentTime + nextEventTime); time++) {
					sigma[time] = t;
				}
				// And respectively decrease the computation duration remaining on the
				// corresponding task
				values.subtractComputationDuration(nextEventTime);
				if (values.getComputationDuration() == 0) {
					int currentWCRT = (currentTime + nextEventTime) - values.getReleaseTime();
					if (currentWCRT >= values.getWCRT()) {
						values.setWCRT(currentWCRT);
					}
				}
			}
			currentTime += nextEventTime;
		}

		for (Task t : keysetTasks) {
			TaskCondition values = taskStatMap.get(t);
			if (values.getComputationDuration() > 0) {
				// Case of tasks still having remaining computation
				missedComputation += values.getComputationDuration();
				values.setComputationDuration(0);
			}
		}
		System.out.println("Missed computation: " + missedComputation);
		return new EdfOutput(sigma, taskStatMap.getWCRT(), missedComputation);
	}

}
