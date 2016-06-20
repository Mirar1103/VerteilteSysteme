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
	/**
	 * the table this Helper deposits Philosophers at.
	 */
	private final Table table;
	/**
	 * the ist of tracked Philosophers
	 */
	private List<PhilosopherImpl> listPhilosophers = new ArrayList<PhilosopherImpl>();
	/**
	 * this constellations master.
	 */
	private final Master master;

	/**
	 * creates a new PhilosopherHelperImpl.
	 * @param table
	 * @param master
	 * @throws RemoteException
     */
	public PhilosopherHelperImpl(Table table, Master master) throws RemoteException {
		this.table = table;
		table.setPhilHelp(this);
		this.master = master;
	}

	@Override
	public synchronized void addPhilosopher(int numberOfPhil, boolean debugging, String ip) throws RemoteException{
		if(numberOfPhil < 1)
			throw new IllegalArgumentException("Number has to be greater than zero.");
		
		for(int i = 0; i < numberOfPhil; i++){
			PhilosopherImpl phil = new PhilosopherImpl(-1, ip);
			phil.setTable(table);
			phil.setShowOutput(debugging);
			phil.setMaster(master);
			new Thread(phil).start();
			listPhilosophers.add(phil);
		}
	}
	@Override
	public synchronized void removePhilosopher(int numberOfPhil, Master master) throws RemoteException {
		if(numberOfPhil < 1 || numberOfPhil > listPhilosophers.size())
			throw new IllegalArgumentException("Wrong number of Philosopher for removing.");
		
		for(int i = 0; i < numberOfPhil; i++){
			Philosopher phil = listPhilosophers.remove(0);
			table.removePhilosopher(phil);
			System.out.println("Removed Philosopher total #"+listPhilosophers.size());
			System.out.println("NAMEEEEE #"+phil.getID());
			System.out.println("TABLE #" + table);
			master.removePhilosopher(phil);
			phil.softKill();
			for(int j = 0; j< listPhilosophers.size(); j++){
				if (listPhilosophers.get(j).getID().equals(phil.getID()))
						phil.softKill();
			}
			
		}
	}
	@Override
	public void setDebugging(boolean isWanted) throws RemoteException {
		for(int i =0; i<listPhilosophers.size(); i++){
			listPhilosophers.get(i).setShowOutput(isWanted);
		}
	}
	@Override
	public synchronized void addPhilosopher(PhilosopherImpl phil) throws RemoteException{
		phil.setMaster(master);
		new Thread(phil).start();
		listPhilosophers.add(phil);
	}
	@Override
	public synchronized void removePhilosopher(Philosopher phil) throws RemoteException{
		for(int i = 0; i < listPhilosophers.size() ; i++){
			if(listPhilosophers.get(i).getID().equals(phil.getID())){
				listPhilosophers.remove(i);
				break;
			}
		}
	}
}
