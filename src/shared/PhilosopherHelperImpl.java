/**
 * 
 */
package shared;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public class PhilosopherHelperImpl extends UnicastRemoteObject implements PhilosopherHelper{
	private final Table table;
	private List<Philosopher> listPhilosophers = new ArrayList<Philosopher>();
	
	public PhilosopherHelperImpl(Table table) throws RemoteException {
		this.table = table;
		table.setPhilHelp(this);
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
			table.removePhilosopher(phil);
				phil.softKill();
				System.out.println("Removed Philosopher total #"+listPhilosophers.size());

		}
	}
	public void setDebugging(boolean isWanted) throws RemoteException {
		for(int i =0; i<listPhilosophers.size(); i++){
			listPhilosophers.get(i).setShowOutput(isWanted);
		}
	}
	public void addPhilosopher(Philosopher phil) throws RemoteException{
		listPhilosophers.add(phil);
	}
	public void removePhilosopher(Philosopher phil) throws RemoteException{
		listPhilosophers.remove(phil);
	}
}
