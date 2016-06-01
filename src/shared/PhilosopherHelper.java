/**
 * 
 */
package shared;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public class PhilosopherHelper {
	private final Table table;
	private List<Philosopher> listPhilosophers = new LinkedList<Philosopher>();
	
	public PhilosopherHelper(Table table){
		this.table = table;
	}
	
	public synchronized void addPhilosopher(int numberOfPhil) throws RemoteException{
		if(numberOfPhil < 1)
			throw new IllegalArgumentException("Number has to be greater than zero.");
		
		for(int i = 0; i < numberOfPhil; i++){
			PhilosopherImpl phil = new PhilosopherImpl();
			phil.setTable(table);
			new Thread(phil).start();
			listPhilosophers.add(phil);
		}
	}
	
	public synchronized void removePhilosopher(int numberOfPhil){
		if(numberOfPhil < 1 || numberOfPhil > listPhilosophers.size())
			throw new IllegalArgumentException("Wrong number of Philosopher for removing.");
		
		for(int i = 0; i < numberOfPhil; i++){
			Philosopher phil = listPhilosophers.remove(0);
			phil.stop();
		}
	}
}
