import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author Dominik Ernsberger
 *
 * 05.04.2016
 */
public class Main {

	static Philosopher philosopher;
	Fork<Philosopher> fork = new Fork<Philosopher>(null);
	Seat<Philosopher> seat = new Seat<Philosopher>(null);
	static List<Philosopher> listPhilosopher;
	static Table<Philosopher> table;
	static Gui gui;
	static Master master;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numberPhilosopher = Integer.parseInt(args[0]);
		int hungry = Integer.parseInt(args[1]);
		int numberSeats = Integer.parseInt(args[2]);
		
		table = new Table<Philosopher>(numberSeats);
		listPhilosopher = new ArrayList<Philosopher>(numberPhilosopher);
		
		for(int index = 0; index < (numberPhilosopher-hungry); index++){
			philosopher = new Philosopher(table, index);
			listPhilosopher.add(philosopher);
			philosopher.start();
		}
		
		for(int index = numberPhilosopher-hungry; index < (numberPhilosopher); index++){
			philosopher = new Philosopher(table, index, 100);
			listPhilosopher.add(philosopher);
			philosopher.start();
		}
		master = new Master(listPhilosopher);
		master.start();
	
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int index = 0; index < listPhilosopher.size(); index++){
			listPhilosopher.get(index).stop(false);
		}
		master.stop(false);
		master.printResult();

	}

}
