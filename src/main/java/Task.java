import java.util.Comparator;

public class Task {
	private int id;
	private int arrivalTime;
	private int processTime;
	
	public Task(int arivalTime, int procesTime) {
		arrivalTime = arivalTime;
		processTime = procesTime;
	}
	
	public void setID(int i) {
		id = i;
	}
	public void addProcessTime(int p) {
		processTime = p + processTime;
	}
	public int getProcessTime() {
		return processTime;
	}
	public void decProcessTime() {
		processTime = processTime - 1;
	}
	
	public int getArrivalTime() {
		return arrivalTime;
	}
	
	public String toString() {
		String rez ='(' + String.valueOf(id) + ", " + String.valueOf(arrivalTime) + ", " + String.valueOf(processTime) + ") ";
		return rez;
	}
}

class CompByArrival implements Comparator<Task>{
	public int compare(Task t1, Task t2) {
		return t1.getArrivalTime() - t2.getArrivalTime();
	}
}