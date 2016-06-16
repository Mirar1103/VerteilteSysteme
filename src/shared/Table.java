/**
 * 
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public interface Table extends Remote{

	public Seat takeSeat(Philosopher owner) throws RemoteException;
	public void standUp(Seat seat)throws RemoteException;
	public boolean pickUpFork(int fork, Philosopher owner)throws RemoteException;
	public void dropFork(int fork)throws RemoteException;
	public int getNumberOfSeats()throws RemoteException;
	public int getNumberOfSemaphores()throws RemoteException;
	public Table getNextTable() throws RemoteException;
	public void setNextTable(Table table) throws RemoteException;
	public void registerNewForkAndSeat(Fork fork, Seat seat) throws RemoteException;
	public void removeForkAndSeat() throws RemoteException;
	public int getID() throws RemoteException;
	public void setID(int id) throws RemoteException;

	public void movePhilosopher(Philosopher phil) throws RemoteException, InterruptedException;
	public void recreatePhilosopher(int hunger, String id, int totalEatenRounds, boolean banned) throws RemoteException;
	public void setShowOutput(boolean isWanted) throws RemoteException;
	public int getSeatPosition(Seat seat) throws RemoteException;
	public void setPhilHelp(PhilosopherHelperImpl philHelp) throws RemoteException;
	public PhilosopherHelperImpl getPhilHelp() throws RemoteException;
	public void removePhilosopher(Philosopher phil) throws RemoteException;
}
