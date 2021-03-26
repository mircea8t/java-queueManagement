import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Scheduler {
	private List<Server> servers = new ArrayList<Server>();
	
	public Scheduler (int maxServers, int maxTasks) {
		int i = 0;
		while (maxServers > i) {
			BlockingQueue<Task> qTask = new ArrayBlockingQueue<Task>(maxTasks);
			Server serv = new Server(qTask);
			servers.add(serv);
			Thread t = new Thread(serv);
			t.start();
			i++;
		}
	}
	
	public void dispatchTask(Task t) {
		int min = 9999;
		for(Server serv:servers)
			if(serv.getWaitingPeriod() < min)
				min = serv.getWaitingPeriod();
		boolean ok = true;
		int i = 0;
		while (ok && (i <= servers.size())) {
			if (servers.get(i).getWaitingPeriod() == min) {
				ok = false;
				servers.get(i).addTask(t);
			}
			i++;
		}
	}
	
	public List<Server> getServers() {
		return servers;
	}
}
