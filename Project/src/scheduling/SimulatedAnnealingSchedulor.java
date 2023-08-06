package scheduling;

import java.io.IOException;
import java.util.Random;

public class SimulatedAnnealingSchedulor extends Schedulor {

	public static float calculateTemperature(float stepRatio) {
		float result = (1.0f * stepRatio);
		return result;
	}

	public static float acceptanceProbability(float sigma, float temperature) {
		return (float) (Math.exp(sigma / (temperature))); // flip the probability
	}

	public static float acceptanceProbability(float sigma, float currentValue, float temperature) {
		float result = (float) (Math.exp(-((sigma) / currentValue) * (sigma / (float) temperature)));
		return result;
	}

	public static float calculateStepRatio(int currentStep, int maxStep) {
		float result = 1.0f - ((float) (currentStep + 1.0f) / (float) maxStep);
		return result;
	}

	public static PollingServerCollection simulatedAnnealing(String inputDataPath, int numberOfZeroSplitServers,
			int maxSteps) throws IOException {

		/**
		 * 
		 * @param maxSteps the number of attempts to find a new solution.
		 * @param seed     the seed used to generate the initial solution & random
		 *                 values for comparison of probability, and to decide on which
		 *                 neighboring solution to choose.
		 * @return TODO: returns the last solution found, likely the best solution found
		 *         by the algorithm.
		 */

		/*
		 * Note: Following a combination of wikipedia's pseudo code
		 * (https://en.wikipedia.org/wiki/Simulated_annealing#Pseudocode) and the pseudo
		 * code from the lecture slides:
		 */
		/*
		 * Select initial (random) solution s0 iterate through maxSteps number of
		 * attempts: Calculate current temperature depending on attempt pick a random,
		 * neighboring solution. Calculate the difference in objective value between old
		 * and new solution. If better, choose the new solution. If worse, choose the
		 * new solution with probability depending on temperature and magnitude of
		 * degradation.
		 */

		Random random = new Random();

		Schedulor.LoadInputData(inputDataPath); // loads the data into the static class variables ts, timeTS, eventTS.
		System.out.println("Data has now been loaded.");
		setHyperDivisors(timeTS.getTaskCollection());
		System.out.println("TTasks are : " + timeTS);
		System.out.println("ETasks are : " + eventTS);

		PollingServerCollection currentSolution = PollingServerCollection.generateRandomSolution(eventTS, timeTS,
				numberOfZeroSplitServers);
		System.out.println("Initial solution generated:");
		System.out.println(currentSolution);

		PollingServerCollection bestSolution = currentSolution;
		// Initialize:
		PollingServerCollection neighborSolution;

		float objectiveValueOriginal = Schedulor.objectiveFunction(currentSolution);
		float objectiveValueCurrent = objectiveValueOriginal;
		float objectiveValueBest = objectiveValueOriginal;
		int bestServerIdx = 0;

		float objectiveValueNew;
		float objectiveValueDifference;
		float probability;

		System.out.println("Original objective value: " + objectiveValueOriginal);
		for (int k = 0; k < maxSteps; k++) {
			System.out.println("Generating new solution #" + k);

			neighborSolution = currentSolution.generateNeighbor();

			objectiveValueNew = Schedulor.objectiveFunction(neighborSolution);
			objectiveValueDifference = objectiveValueCurrent - objectiveValueNew;
			if (objectiveValueDifference >= 0) {
				// System.out
				// .println("New solution is an improvement of current solution, by " +
				// objectiveValueDifference);
				// case of new solution is an improvement over current.
				currentSolution = neighborSolution;
				objectiveValueCurrent = objectiveValueNew;
				if (objectiveValueNew < objectiveValueBest) {
					System.out.println("New optimal solution found: " + objectiveValueNew);
					bestSolution = neighborSolution;
					objectiveValueBest = objectiveValueNew;
					bestServerIdx = k;
				}

			} else if (objectiveValueDifference < 0) { // case of objectiveValueDifference =< 0
				System.out.println("Objective value difference: " + objectiveValueDifference);
				float stepRatio = calculateStepRatio(k, maxSteps);
				float temperature = calculateTemperature(stepRatio);
				probability = acceptanceProbability(objectiveValueDifference, objectiveValueCurrent, temperature);
				System.out.println("Probability: " + probability);
				if (probability > random.nextDouble()) {
					System.out.println("New solution is chosen by probability");
					currentSolution = neighborSolution;
					objectiveValueCurrent = objectiveValueNew;
				}
			}
		}
		System.out.println("final, best Objective Value is: " + objectiveValueBest);
		System.out.println("Improvement from original solution is: " + (objectiveValueBest - objectiveValueOriginal));
		System.out.println("best server iteration: " + (bestServerIdx));
		return bestSolution;

	}

}
