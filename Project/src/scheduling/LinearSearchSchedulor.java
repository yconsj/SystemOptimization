package scheduling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import taskData.Task;

public class LinearSearchSchedulor extends Schedulor {

	public static float assign_server_polls(int target_cp, int s_period, int s_deadline, int divisorIdx,
			ArrayList<TaskSet> splits) {
		PollingServerCollection solution = new PollingServerCollection();

		final int sepRequirement = 0;
		for (int i = 0; i < splits.size(); i++) {
			solution.appendPollingServer(
					new PollingServer(target_cp, s_period, s_deadline, splits.get(i), sepRequirement, divisorIdx));
		}

		return objectiveFunction(solution);
	}

	public static PollingServerCollection linearSearch(String inputDataPath, int nZeroSepServers) throws IOException {
		Schedulor.LoadInputData(inputDataPath);
		ArrayList<TaskSet> split_tasks = new ArrayList<TaskSet>();
		for (int i = 0; i < nZeroSepServers; i++) {
			// add some currently empty task sets to contain the splitted zero-separation
			// requirement tasks.
			split_tasks.add(new TaskSet());
		}

		HashMap<Integer, TaskSet> sepReqMapTS = Schedulor.partitionETTasks(eventTS);
		for (int sepRequirement : sepReqMapTS.keySet()) {
			if (sepRequirement == 0) {
				int zeroServerIdx = 0;
				for (Task event0sep : sepReqMapTS.get(sepRequirement).getTaskCollection()) {
					split_tasks.get(zeroServerIdx % nZeroSepServers).appendTask(event0sep);
					zeroServerIdx++;
				}
			} else {
				split_tasks.add(sepReqMapTS.get(sepRequirement));
			}
		}

		float bestResponse = Integer.MAX_VALUE; // TODO: replace
		int bestCp = 1;
		// int hyperperiod = lcm(timeTS.getTaskCollection());
		Schedulor.setHyperDivisors(timeTS.getTaskCollection());
		System.out.println("hyperdivisors:" + Schedulor.getHyperDivisors());
		int divisorIndex = 22;
		int server_period = Schedulor.getHyperDivisors().get(divisorIndex); // hyperperiod / (2 * 10); //
		// nZeroSepServers
		int server_deadline = server_period;
		int right_idx = server_period;
		final int initialValue = 1;
		System.out.println("period : " + server_period + ". initial value : " + initialValue);
		// linear searches:
		for (int i = initialValue; i <= right_idx; i++) {
			float currentResult = assign_server_polls(i, server_period, server_deadline, divisorIndex, split_tasks);
			if (currentResult < bestResponse) {
				bestCp = i;
				bestResponse = currentResult;
			}
		}
//int computationTime, int serverPeriod, int serverDeadline, TaskSet etSubset,
		// int separationRequirement, int divisorIndex
		// Create corresponding polling servers according to the best solution found:
		PollingServerCollection best_solution = new PollingServerCollection();
		// -1 because we also count a 0 separation taskset in sepreqmapTS
		for (int i = 0; i < split_tasks.size(); i++) {
			int separationRequirement = i < nZeroSepServers ? 0 : i - nZeroSepServers;
			PollingServer newPollServer = new PollingServer(bestCp, server_period, server_deadline, split_tasks.get(i),
					separationRequirement, divisorIndex);
			best_solution.appendPollingServer(newPollServer);
		}
		return best_solution;

	}

}
