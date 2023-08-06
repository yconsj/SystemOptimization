package taskData;

public enum TaskType {
	TimeTriggered("TT"), EventTriggered("ET"), PollingServer("PS");

	private String label;

	TaskType(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public static TaskType parseTaskType(String strTType) {
		if (strTType.equals("TT")) {
			return TaskType.TimeTriggered;
		} else if (strTType.equals("ET")) {
			return TaskType.EventTriggered;
		} else if (strTType.equals("PS")) {
			return TaskType.PollingServer;
		}
		return null;
	}
}