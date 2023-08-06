package scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import taskData.Task;

public class PollingServerCollection {
	int ttHyperPeriod;
	public ArrayList<PollingServer> pollingServerArray;
	private static Random random = new Random(); 

	public ArrayList<PollingServer> getPollingServerArray() {
		return pollingServerArray;
	}

	public TaskSet getConvertedTaskSet() {
		// Returns the PollingServerCollection converted to a TaskSet of TTasks.
		TaskSet PServerCollectionAsTSet = new TaskSet();
		PServerCollectionAsTSet.setTaskCollection(new HashSet<Task>(this.getPollingServerArray()));

		return PServerCollectionAsTSet;
	}

	public PollingServerCollection() {
		this.pollingServerArray = new ArrayList<PollingServer>();
	}

	public PollingServerCollection generateNeighbor() {
		final int maxMutationRange = 5;
		int mutationSteps = random.nextInt(maxMutationRange);

		double probability = random.nextDouble();
		if (probability >= 0.975) { 
			return mutatePeriod(1); 
		} else if (probability >= 0.5) {
			return mutateComputationTime(mutationSteps);
		} else if (probability >= 0.25) {
			return mutateDeadline(mutationSteps);
		} else {
			return mutateTaskMove();
		}
	}

	public PollingServerCollection mutatePeriod(int steps) {
		int pServerIdx = random.nextInt(pollingServerArray.size());
		// Check period doesn't become greater than hyper period of ET tasks
		PollingServerCollection collectionCopy = this.deepcopy();
		PollingServer randPServer = collectionCopy.pollingServerArray.get(pServerIdx);
		int randPServerDivisorIndex = randPServer.getDivisorIndex();

		final int oldPeriod = randPServer.getPeriod();
		int newPeriod = 0;
		final int originalComputationTime = randPServer.getComputationTime();
		final int originalDeadline = randPServer.getDeadline();

		if (random.nextDouble() >= 0.5 && (randPServerDivisorIndex + steps) < Schedulor.getHyperDivisors().size()) {
			int newDivisorIndex = randPServerDivisorIndex + steps;
			randPServer.setDivisorIndex(newDivisorIndex);
			randPServer.setPeriod(Schedulor.getHyperDivisors().get(newDivisorIndex));

		} else if ((randPServerDivisorIndex - steps) >= 0) {
			int newDivisorIndex = randPServerDivisorIndex - steps;
			randPServer.setDivisorIndex(newDivisorIndex);
			randPServer.setPeriod(Schedulor.getHyperDivisors().get(newDivisorIndex));
		}
		newPeriod = randPServer.getPeriod();
		float periodRatio = (float) newPeriod / (float) oldPeriod;
		int ratioComputationTime = (int) Math.max(1, originalComputationTime * periodRatio);
		randPServer.setComputationTime(ratioComputationTime);
		int ratioDeadline = (int) Math.max(1, originalDeadline * periodRatio);
		randPServer.setDeadline(ratioDeadline);
		return collectionCopy;
	}

	public PollingServerCollection mutateComputationTime(int steps) {
		final int pServerIdx = random.nextInt(pollingServerArray.size());
		PollingServerCollection collection = this.deepcopy();
		PollingServer pServer = collection.pollingServerArray.get(pServerIdx);
		int originalComputationTime = pServer.getComputationTime();
		boolean isIncreaseValid = steps + originalComputationTime <= pServer.getDeadline();
		boolean isDecreaseValid = originalComputationTime - steps > 0;
		int newComputationTime = originalComputationTime;

		if (isIncreaseValid && random.nextDouble() >= 0.5) {
			newComputationTime = originalComputationTime + steps;
		} else if (isDecreaseValid) {
			newComputationTime = originalComputationTime - steps;
		}

		pServer.setComputationTime(newComputationTime);
		return collection;
	}

	public PollingServerCollection mutateDeadline(int steps) {
		final int pServerIdx = random.nextInt(pollingServerArray.size());
		PollingServerCollection collection = this.deepcopy();
		PollingServer pServer = collection.pollingServerArray.get(pServerIdx);

		int originalDeadline = pServer.getDeadline();
		boolean isIncreaseValid = steps + originalDeadline <= pServer.getPeriod();
		boolean isDecreaseValid = originalDeadline - steps > pServer.getComputationTime();
		int newDeadline = originalDeadline;
		if (isIncreaseValid && random.nextDouble() >= 0.5) {
			newDeadline += steps;
		} else if (isDecreaseValid) {
			newDeadline = Math.max(newDeadline - steps, 1);
		}
		pServer.setDeadline(newDeadline);
		return collection;
	}

	public PollingServerCollection mutateTaskMove() {
		PollingServerCollection collection = this.deepcopy();
		ArrayList<PollingServer> zeroSeparationServers = new ArrayList<PollingServer>();

		/*
		 * Can only swap tasks between polling servers handling tasks with separation
		 * requirement == 0.
		 */
		collection.getPollingServerArray().forEach((pServer) -> {
			if (((PollingServer) pServer).getSeparationRequirement() == 0) {
				zeroSeparationServers.add(pServer);
			}
		});

		if (zeroSeparationServers.size() > 1) {
			// Get two random (different) indices of the number of ZeroSeparationServers
			final int firstIndex = random.nextInt(zeroSeparationServers.size());
			int secondIndex = random.nextInt(zeroSeparationServers.size());
			while (firstIndex == secondIndex) {
				secondIndex = random.nextInt(zeroSeparationServers.size());
			}
			PollingServer SourceServer = zeroSeparationServers.get(firstIndex);
			PollingServer DestinationServer = zeroSeparationServers.get(secondIndex);
			// find a random ET task to move
			final int numberOfETTasks = SourceServer.getETSubset().getTaskCollection().size();
			if (numberOfETTasks == 1) {
				return collection;
			}
			final int randomETIndex = random.nextInt(numberOfETTasks);
			int counter = 0;
			for (Task etask : SourceServer.getETSubset().getTaskCollection()) {
				if (counter == randomETIndex) {
					SourceServer.getETSubset().getTaskCollection().remove(etask);
					DestinationServer.getETSubset().getTaskCollection().add(etask);
					break;
				}
				counter++;
			}
		}
		return collection;

	}

	public static PollingServerCollection generateRandomSolution(TaskSet etTaskSet, TaskSet tTaskSet,
			int numberOfServers) {
		// TODO Add generation of random number of 0-separation requirement servers?
		HashMap<Integer, TaskSet> sepReqMapTS = Schedulor.partitionETTasks(etTaskSet);
		PollingServerCollection pServerCollection = new PollingServerCollection();

		PollingServer pServer;
		TaskSet eTaskSet;
		int computationTime = 0;
		int serverPeriod = 0;
		int serverDeadline = 0;
		int hyperPeriod;
		int divisorIndex = 0;
		int ttHyperPeriod = Schedulor.lcm(tTaskSet.getTaskCollection());
		// For each separation-requirement, create a polling server & set the values to
		// a random value in the 'legal' range.
		// This does not promise that the random solution is successful for the sake of
		// EDP and EDF however.
		for (int sepRequirement : sepReqMapTS.keySet()) {
			if (sepRequirement == 0) {
				continue;
			}
			eTaskSet = sepReqMapTS.get(sepRequirement);
			divisorIndex = random.nextInt(Schedulor.getHyperDivisors().size() - 1);
			serverPeriod = Schedulor.getHyperDivisors().get(divisorIndex);
			computationTime = Math.max(random.nextInt(serverPeriod), eTaskSet.getSumComputationTime()); // TODO:
			serverDeadline = Math.min(serverPeriod ,Math.max(random.nextInt(serverPeriod), computationTime*2));

			pServer = new PollingServer(computationTime, serverPeriod, serverDeadline, eTaskSet, sepRequirement,
					divisorIndex);
			pServerCollection.appendPollingServer(pServer);
			pServerCollection.ttHyperPeriod = ttHyperPeriod;
		}

		// initialize 0 separation servers
		int sepRequirement = 0;
		ArrayList<TaskSet> zeroServerSplit = Utilities.divideTaskSet(sepReqMapTS.get(sepRequirement), numberOfServers);

		for (int serverI = 0; serverI < numberOfServers; serverI++) {
			eTaskSet = zeroServerSplit.get(serverI);

			divisorIndex = random.nextInt(Schedulor.getHyperDivisors().size() - 1);
			serverPeriod = Schedulor.getHyperDivisors().get(divisorIndex);

			serverDeadline = serverPeriod;
			computationTime = serverPeriod / 5; 
			computationTime = Math.max(random.nextInt(serverPeriod), eTaskSet.getSumComputationTime()); // TODO:
			serverDeadline = Math.min(serverPeriod ,Math.max(random.nextInt(serverPeriod), computationTime*2));

			pServer = new PollingServer(computationTime, serverPeriod, serverDeadline, eTaskSet, sepRequirement,
					divisorIndex);
			pServerCollection.appendPollingServer(pServer);
			pServerCollection.ttHyperPeriod = ttHyperPeriod;
		}

		return pServerCollection;
	}

	public PollingServerCollection deepcopy() {
		PollingServerCollection newPollingServerCollection = new PollingServerCollection();
		for (PollingServer ps : this.pollingServerArray) {
			newPollingServerCollection.appendPollingServer(ps.createCopy());
		}
		return newPollingServerCollection;
	}

	public void appendPollingServer(PollingServer newPollServer) {
		this.pollingServerArray.add(newPollServer);
	}

	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (PollingServer pServer : getPollingServerArray()) {
			strBuilder.append(pServer.toString() + "\n");
		}
		return strBuilder.toString();
	}

	public PollingServerCollection generateNeighborSignificant(float objectiveValueCurrent, int maxSubSolutions) {
		// Generates an initial neighbor,
		// then creates neighbors based on previous neighbor until a new objective value
		// is found, or number of attempts exceeds maxSubSolutions
		PollingServerCollection neighborSolution = generateNeighbor();
		float objectiveValueNeighbor = Schedulor.objectiveFunction(neighborSolution);
		int attempts;
		for (attempts = 0; attempts < maxSubSolutions; attempts++) {
			if (objectiveValueNeighbor != objectiveValueCurrent) {
				break;
			}
			neighborSolution = neighborSolution.generateNeighbor();
			objectiveValueNeighbor = Schedulor.objectiveFunction(neighborSolution);
		}
		System.out.println(String.format("used %d attempts to generate neighbor", attempts));
		return neighborSolution;
	}

}
