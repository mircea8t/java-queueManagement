
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.*;

public class Server implements Runnable {
	
	private BlockingQueue<Task> tasks;
	private AtomicInteger waitingPeriod;
	private volatile boolean runn = true;
	
	
	public Server(BlockingQueue<Task> lTask) {
		waitingPeriod = new AtomicInteger();
		waitingPeriod.set(0);
		tasks = lTask;
	}
	
	public void addTask (Task newT) {
		int wait = waitingPeriod.get();
		waitingPeriod.set(waitingPeriod.addAndGet(newT.getProcessTime()));
		newT.addProcessTime(wait);
		tasks.add(newT);
	}
	
	public void run() {
		while(runn) {
			while(tasks.size() > 0) {
				waitingPeriod.set(waitingPeriod.decrementAndGet());
				Task[] aux = getTasks();
				for(int i =0; i<aux.length; i++)
					System.out.print(aux[i].toString() + " ");
				System.out.println();
				decrementList();
				deleteTask();
				try {
					Thread.sleep(100);
				}catch(InterruptedException e) {}
			}
		}
	}
	
	public int getWaitingPeriod() {
		return waitingPeriod.get();
	}
	
	public Task[] getTasks() {
		BlockingQueue<Task> aux = new ArrayBlockingQueue<Task>(20);
		Task[] taskss = new Task[tasks.size()];
		for(int i=0; i<taskss.length; i++)
			taskss[i] = new Task(0,0);
		for(int i=0; i < taskss.length; i++) {
			try {
			Task t  =tasks.take();
			aux.add(t);
			taskss[i] = t;
			}catch(Exception e) {}
		}
		tasks = aux;
		return taskss;
	}
	
	public boolean isFinished() {
		boolean ok = false;
		BlockingQueue<Task> aux = new ArrayBlockingQueue<Task>(20);
		Task t = new Task(0,1);
		try {
			t = tasks.take();
		}catch(Exception e) {
		}
		aux.add(t);
		if(t.getProcessTime() == 0)
			ok = true;
		while(tasks.size() > 0) {
			try {
				t = tasks.take();
				aux.add(t);
			}catch(Exception e) {
			}
		}
		tasks = aux;
		return ok;
	}
	
	public void decrementList() {
		BlockingQueue<Task> aux = new ArrayBlockingQueue<Task>(20);
		Task[] tt = getTasks();
		for(int i = 0; i<tt.length; i++) {
			tt[i].decProcessTime();
			aux.add(tt[i]);
		}
		tasks = aux;
	}
	
	public void deleteTask() {
		if (isFinished())
			try {
				Task t = tasks.take();
			}catch(InterruptedException e) {
				System.out.print("nu a fost eliminat elementul");
			}
	}
	
	public static void main(String[] args) {
		Task t1 = new Task(2,3); Task t2 = new Task(2,7); 
		Task t3 = new Task(2,6); Task t4 = new Task(3,6);
		Task t5 = new Task(5,6);
		BlockingQueue<Task> q1 = new ArrayBlockingQueue<Task>(20);
		Server s = new Server(q1);
		Thread tt = new Thread(s);
		tt.start();
		s.addTask(t1);
		s.addTask(t2);
		s.addTask(t3);
		s.addTask(t4);
		s.addTask(t5);
	}
}

