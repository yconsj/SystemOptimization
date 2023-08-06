package main_interface;

import java.io.IOException;

import scheduling.LinearSearchSchedulor;
import scheduling.PollingServerCollection;
import scheduling.Schedulor;
import scheduling.SimulatedAnnealingSchedulor;

public class MainInterface {
	static String file_test_0 = "inf_10_10/taskset__1643188013-a_0.1-b_0.1-n_30-m_20-d_unif-p_2000-q_4000-g_1000-t_5__0__tsk.csv";
	static String file_test_1 = "test case with separation.csv";
	static String file_test_2 = "taskset__1643188013-a_0.1-b_0.1-n_30-m_20-d_unif-p_2000-q_4000-g_1000-t_5__0__tsk.csv";
	static String sep_base_path = "testcases_seperation_VFINAL/testcases_orig2/inf_10_10/";
	static String base_path = "test_cases/";
	static String file_path = base_path + sep_base_path + file_test_2;

	public static void main(String[] args) throws IOException {

		// TODO: Move number-of-zerosplitservers argument to the parameters of
		// simulatedAnnealing.
		// run the 2 scheduling heuristics.
		final int nZeroSplitServers = 3;
		System.out.println("Starting linear schedulor...");
		PollingServerCollection hillClimbSolution = LinearSearchSchedulor.linearSearch(file_path, nZeroSplitServers);
		System.out.println("----------------------------------");
		System.out.println("Starting Simulated Annealing schedulor...");
		final int nSASteps = 2000;
		PollingServerCollection simAnnealSolution = SimulatedAnnealingSchedulor.simulatedAnnealing(file_path,
				nZeroSplitServers, nSASteps);

		System.out.println("SA Scheduling complete!: " + simAnnealSolution);
		System.out.println("SA objective value: " + Schedulor.objectiveFunction(simAnnealSolution));

		System.out.println("best HC solution: " + hillClimbSolution);
		System.out.println("linear search scheduling complete! objective value: "
				+ Schedulor.objectiveFunction(hillClimbSolution));

	}
}