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
	
	public synchronized void addPhilosopher(int numberOfPhil, boolean debugging) throws RemoteException{
		if(numberOfPhil < 1)
			throw new IllegalArgumentException("Number has to be greater than zero.");
		
		for(int i = 0; i < numberOfPhil; i++){
			PhilosopherImpl phil = new PhilosopherImpl(-1);
			phil.setTable(table);
			phil.setShowOutput(debugging);
			new Thread(phil).start();
			listPhilosophers.add(phil);
		}
	}
	
	public synchronized void removePhilosopher(int numberOfPhil) throws RemoteException {
		if(numberOfPhil < 1 || numberOfPhil > listPhilosophers.size())
			throw new IllegalArgumentException("Wrong number of Philosopher for removing.");
		
		for(int i = 0; i < numberOfPhil; i++){
			Philosopher phil = listPhilosophers.remove(0);
			try {
				phil.kill();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void setDebugging(boolean isWanted) throws RemoteException {
		for(int i =0; i<listPhilosophers.size(); i++){
			listPhilosophers.get(i).setShowOutput(isWanted);
		}
	}
}
