import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class SimulationManager implements Runnable{
	public int timeLimit;
	public int maxProcessingTime;
	public int minProcessingTime;
	public int maxArrivalTime;
	public int minArrivalTime;
	public int nrServers;
	public int nrClients;
	
	private Scheduler schedule;
	private String outFile;
	public SimulationManager(String in, String out) {
		getInfo(in);
		schedule = new Scheduler(nrServers, nrClients);
		generateRandomTask();
		outFile = out;
	}
	
	private List<Task> generatedTasks = new ArrayList<Task>();
	public void generateRandomTask() {
		int n =nrClients;
		Random rnd = new Random();
		while(n > 0) {
			int arvTime = rnd.nextInt(maxArrivalTime + 1 - minArrivalTime) + minArrivalTime;
			int prcTime = rnd.nextInt(maxProcessingTime + 1 - minProcessingTime) + minProcessingTime;
			Task t = new Task(arvTime, prcTime);
			generatedTasks.add(t);
			n--;
		}
		Collections.sort(generatedTasks, new CompByArrival());
		int i=1;
		for(Task iTask: generatedTasks) {
			iTask.setID(i);
			generatedTasks.set(i-1, iTask);
			i++;
		}
	}
	//it writes in a file from working space
	public void run(){
		int crtTime = 0;
		String path  =System.getProperty("user.dir");
		path = path + "\\" + outFile;
		File file = new File(path); 
		PrintWriter pw = new PrintWriter(System.out);
		try {
			pw = new PrintWriter(file);
		}catch(Exception e) {
		}
		
		while ((crtTime <= timeLimit)) {
			List<Task> auxTasks = new ArrayList<Task>();
			for(Task iTask:generatedTasks) {
				if(iTask.getArrivalTime() == crtTime)
					schedule.dispatchTask(iTask);
				else
					auxTasks.add(iTask);
			}
			generatedTasks = auxTasks;
			try {
				Thread.sleep(73);
			}catch(InterruptedException e) {}
			updateFile(pw,crtTime);
			crtTime++;
		}
		pw.close();
	}
	//look for file in working space and take info from it
	public void getInfo(String file){
		String path  =System.getProperty("user.dir");
		path = path + "\\" + file;
		try {
			File f = new File(path);
			Scanner s = new Scanner(f);
			nrClients = s.nextInt();
			nrServers = s.nextInt();
			timeLimit = s.nextInt();
			String numere = s.next();
			setArrivalTime(numere);
			numere  =s.next();
			setProcessTime(numere);
			s.close();
		}catch(FileNotFoundException e) {	
			System.out.print("The file is incorrect");
		}
	}
	//take string and transform it in min/max ArrivalTime
	public void setArrivalTime(String s) {
		String nou = "";
		int i = 0;
		while (s.charAt(i) != ',') {
			nou = nou + s.charAt(i);
			i++;
		}
		minArrivalTime = Integer.parseInt(nou);
		i++;
		nou="";
		while (i < s.length()) {
			nou = nou + s.charAt(i);
			i++;
		}
		maxArrivalTime = Integer.parseInt(nou);
	}
	//take string and transform it in min/max ProcessTime
	public void setProcessTime(String s) {
		String nou = "";
		int i = 0;
		while (s.charAt(i) != ',') {
			nou = nou + s.charAt(i);
			i++;
		}
		minProcessingTime = Integer.parseInt(nou);
		i++;
		nou="";
		while (i < s.length()) {
			nou = nou + s.charAt(i);
			i++;
		}
		maxProcessingTime = Integer.parseInt(nou);
	}
	
	public void updateFile(PrintWriter pw, int crtTime){
		try {
			pw.print("Time ");
			pw.println(crtTime);
			pw.print("Waiting clients: ");
			for(Task it:generatedTasks) {
				pw.print(it.toString());
			}pw.println();
			List<Server> servers = schedule.getServers();
			int i = 1;
			for(Server iServ: servers) {
				String s = "Queue " + String.valueOf(i) + ": ";
				pw.print(s);
				i++;
				Task[] taskss = iServ.getTasks();
				if (taskss.length > 0)
					for(int j=0; j<taskss.length; j++){
						pw.print(taskss[j].toString());
						pw.print("; ");
					}
				else pw.print("closed");
				pw.println();
			}
			pw.println();
		}catch(Exception e) {		
		}
	}
	
	public static void main(String[] args) {
		SimulationManager manager = new SimulationManager(args[0],args[1]);
		Thread t = new Thread(manager);
		t.start();
	}
}
