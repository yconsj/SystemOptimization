package taskIO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import scheduling.TaskSet;
import taskData.Task;
import taskData.TaskFactory;
import taskData.TaskType;

public class TaskSetFileReader {

	// multi-thread-safe singleton implementation
	private TaskSetFileReader() {
	}

	private static class SingletonHolder {
		public static final TaskSetFileReader instance = new TaskSetFileReader();
	}

	public static TaskSetFileReader getInstance() {
		return SingletonHolder.instance;
	}

	private static final int nameIndex = 1, durationIndex = 2, periodIndex = 3, typeIndex = 4, priorityIndex = 5,
			deadlineIndex = 6, separationIndex = 7;

	public static Task createTask(String line) {
		String attributeDelimiter = ";";
		List<String> splitLine = Arrays.asList(line.split(attributeDelimiter));
		String taskName = splitLine.get(nameIndex);

		final int computationTime = Integer.parseInt(splitLine.get(durationIndex));
		final int period = Integer.parseInt(splitLine.get(periodIndex));
		final TaskType ttype = TaskType.parseTaskType(splitLine.get(typeIndex));

		final int priority = Integer.parseInt(splitLine.get(priorityIndex));
		final int deadline = Integer.parseInt(splitLine.get(deadlineIndex));
		final int separation;

		if (splitLine.size() == (separationIndex + 1)) { // has a "separation" column.
			separation = Integer.parseInt(splitLine.get(separationIndex).stripTrailing());
		} else {
			separation = 0;
		}

		return TaskFactory.getTask(taskName, computationTime, period, ttype, priority, deadline, separation);
	}

	public static TaskSet createTaskSet(String filepath) throws IOException {
		TaskSet taskSet = new TaskSet();

		String lineDelimiter = "\n";
		Scanner sc = new Scanner(new File(filepath));
		sc.useDelimiter(lineDelimiter);

		// Skip first line of the data file, as it contains the column titles.
		sc.next();

		while (sc.hasNext()) {
			taskSet.appendTask(createTask(sc.next()));

		}
		return taskSet;

	}

}
