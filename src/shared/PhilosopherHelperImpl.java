/**
 * 
 */
package shared;

import java.io.Serializable;
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
public class PhilosopherHelperImpl implements PhilosopherHelper, Serializable{
	private static final long serialVersionUID = 1L;
	private final Table table;
	private List<PhilosopherImpl> listPhilosophers = new ArrayList<PhilosopherImpl>();
	private final Master master;
	
	public PhilosopherHelperImpl(Table table, Master master) throws RemoteException {
		this.table = table;
		table.setPhilHelp(this);
		this.master = master;
	}
	
	public synchronized void addPhilosopher(int numberOfPhil, boolean debugging) throws RemoteException{
		if(numberOfPhil < 1)
			throw new IllegalArgumentException("Number has to be greater than zero.");
		
		for(int i = 0; i < numberOfPhil; i++){
			PhilosopherImpl phil = new PhilosopherImpl(-1);
			phil.setTable(table);
			phil.setShowOutput(debugging);
			phil.setMaster(master);
			new Thread(phil).start();
			listPhilosophers.add(phil);
		}
	}
	
	public synchronized void removePhilosopher(int numberOfPhil, Master master) throws RemoteException {
		if(numberOfPhil < 1 || numberOfPhil > listPhilosophers.size())
			throw new IllegalArgumentException("Wrong number of Philosopher for removing.");
		
		for(int i = 0; i < numberOfPhil; i++){
			Philosopher phil = listPhilosophers.remove(0);
			table.removePhilosopher(phil);
			System.out.println("Removed Philosopher total #"+listPhilosophers.size());
			System.out.println("NAMEEEEE #"+phil.getID());
			master.removePhilosopher(phil);
			phil.softKill();
		}
	}
	public void setDebugging(boolean isWanted) throws RemoteException {
		for(int i =0; i<listPhilosophers.size(); i++){
			listPhilosophers.get(i).setShowOutput(isWanted);
		}
	}
	public synchronized void addPhilosopher(PhilosopherImpl phil) throws RemoteException{
		listPhilosophers.add(phil);
	}
	public synchronized void removePhilosopher(Philosopher phil) throws RemoteException{
		for(int i = 0; i < listPhilosophers.size() ; i++){
			if(listPhilosophers.get(i).getID().equals(phil.getID())){
				listPhilosophers.remove(i);
				break;
			}
		}
	}
}
