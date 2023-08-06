package scheduling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import taskData.Task;
import taskData.TaskType;
import taskIO.TaskSetFileReader;

abstract public class Schedulor {

	protected static TaskSet ts;
	protected static TaskSet timeTS;
	protected static TaskSet eventTS;

	private static ArrayList<Integer> ttHyperPeriodDivisors;

	public static int lcm(Set<Task> taskSet) {
		ArrayList<Integer> periods = new ArrayList<Integer>();
		taskSet.forEach(t -> periods.add(t.getPeriod()));

		long lcm_of_array_elements = 1;
		int divisor = 2;

		while (true) {
			int counter = 0;
			boolean divisible = false;

			for (int i = 0; i < periods.size(); i++) {

				// lcm_of_array_elements (n1, n2, ... 0) = 0.
				// For negative number we convert into
				// positive and calculate lcm_of_array_elements.

				if (periods.get(i) == 0) {
					return 0;
				} else if (periods.get(i) < 0) {
					periods.set(i, periods.get(i) * (-1));
				}
				if (periods.get(i) == 1) {
					counter++;
				}

				// Divide element_array by divisor if complete
				// division i.e. without remainder then replace
				// number with quotient; used for find next factor
				if (periods.get(i) % divisor == 0) {
					divisible = true;
					periods.set(i, periods.get(i) / divisor);
				}
			}

			// If divisor able to completely divide any number
			// from array multiply with lcm_of_array_elements
			// and store into lcm_of_array_elements and continue
			// to same divisor for next factor finding.
			// else increment divisor
			if (divisible) {
				lcm_of_array_elements = lcm_of_array_elements * divisor;
			} else {
				divisor++;
			}

			// Check if all element_array is 1 indicate
			// we found all factors and terminate while loop.
			if (counter == periods.size()) {
				if (lcm_of_array_elements > (long) Integer.MAX_VALUE) {
					throw new IllegalArgumentException("lcm overflowed");
				}
				return (int) lcm_of_array_elements;
			}
		}

	}

	public static HashMap<Integer, TaskSet> partitionETTasks(TaskSet etTaskSet) {

		/*
		 * Partition the Event-Triggered tasks, such that they fulfill the separation
		 * requirement: All tasks with separation value > 0, must be contained in
		 * polling servers such that task with separation value s is handled by a
		 * polling server that serves all ET tasks with separation value of s. ET Tasks
		 * with separation value == 0 can be partitioned in whatever way optimizes the
		 * problem, but may not be contained in a polling server that handles tasks with
		 * separation value != 0. Initially, we separate the tasks with s == 0 similarly
		 * to all other tasks.
		 */
		HashMap<Integer, TaskSet> partitionedTasks = new HashMap<Integer, TaskSet>();
		for (Task task : etTaskSet.getTaskCollection()) {
			task.getSeparationRequirement();
			int separationValue = task.getSeparationRequirement();
			if (partitionedTasks.containsKey(separationValue)) {
				partitionedTasks.get(separationValue).appendTask(task);
			} else {
				// Create a new TaskSet for the specified separation value
				TaskSet newTaskSet = new TaskSet();
				newTaskSet.appendTask(task);
				partitionedTasks.put(separationValue, newTaskSet);
			}
		}

		return partitionedTasks;
	}

	public static void LoadInputData(String inputDataPath) throws IOException {
		ts = TaskSetFileReader.createTaskSet(inputDataPath);
		timeTS = new TaskSet();
		eventTS = new TaskSet();

		for (Task task : ts.taskSet) { // Add the time triggered tasks to tts
			if (task.getTaskType().equals(TaskType.TimeTriggered)) {
				timeTS.appendTask(task);
			} else if (task.getTaskType().equals(TaskType.EventTriggered)) {
				eventTS.appendTask(task);
			}
		}
	}

	public static float calculatePollingServerCollectionWCRTs(PollingServerCollection pServerCollection,
			EdfOutput edfOutput) {
		int numViableServers = 0;
		final ArrayList<PollingServer> pServerArray = pServerCollection.getPollingServerArray();
		final int numberOfServers = pServerArray.size();
		float avg_wcrt = 0;
		EdpOutput edpResult;

		// find % of tasks handled per server, then weigh the penalty by that
		int totalNumberOfETTasks = eventTS.getSetSize();
		final float penalty_value = 20.0f;
		float penalty_sum = 0;

		for (PollingServer pServer : pServerArray) {
			/* 
			if (edfOutput.isSchedulable()) {
				edpResult = Edp.algo2Extension3Old(pServer, edfOutput.getSchedule());
			} else {
				edpResult = Edp.algo2(pServer);
			}
			*/
			
			edpResult = Edp.algo2(pServer);
			avg_wcrt += edpResult.getResponseTime();
			if (!edpResult.getSchedulable()) {
				float taskRatio = pServer.getETSubset().getSetSize() / (float) totalNumberOfETTasks;
				penalty_sum += Math.max(penalty_value * 100, penalty_value * avg_wcrt);
			} else {
				numViableServers++;
			}
		}

		if (numViableServers < numberOfServers) {
			System.out
					.println(String.format("EDP Penalty applied for %d servers", (numberOfServers - numViableServers)));
		}
		avg_wcrt += penalty_sum;
		avg_wcrt /= numberOfServers;
		return avg_wcrt;
	}

	public static void setHyperDivisors(Set<Task> ttTasks) {
		int lcm = lcm(ttTasks);
		ttHyperPeriodDivisors = Utilities.getDivisors(lcm);
		ttHyperPeriodDivisors.removeIf(x -> x < 2); // remove the divisors which are too small to be useful.
	}

	public static ArrayList<Integer> getHyperDivisors() {
		if (ttHyperPeriodDivisors == null) {
			System.out.println("Hyper period divisors not set");
			return null;
		} else {
			return ttHyperPeriodDivisors;
		}
	}

	public static float objectiveFunction(PollingServerCollection solution) {
		// TODO: Do delta evaluation by only calculating for the polling servers changed
		// by neighbor solution.
		float result = 0;
	
		EdfOutput EdfResult = EdfSimulator.algorithm1(timeTS, solution.getConvertedTaskSet()); // solution.getConvertedTaskSet()
	
		// penalty for incorrect EDF
		if (!EdfResult.isSchedulable()) {
			System.out.println("EDF Penalty Applied.");
			final float penalty = 0.25f;
			result += (float) penalty * EdfResult.getMissedComputation(); // timeTS.getSetSize();
		}
		result += EdfResult.getAvgWCRT();
		// System.out.println("Schedule : \n" +
		// Utilities.scheduleToString(EdfResult.getSchedule()));
		// System.out.println("objective value of EDF: " + result);
		float ETtaskAvg = calculatePollingServerCollectionWCRTs(solution, EdfResult);
		result += ETtaskAvg;
		System.out.println("objective value of EDF+EDP: " + result);
		return (result); // // TODO: consider doing some kind of weighting of the 2 WCRT
		// results
	}
}
