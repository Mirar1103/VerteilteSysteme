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

	public int takeSeat(Philosopher owner) throws RemoteException;
	public void standUp(int seat)throws RemoteException;
	public boolean pickUpFork(int fork, Philosopher owner)throws RemoteException;
	public void dropFork(int fork)throws RemoteException;
	public int getNumberOfSeats()throws RemoteException;
	public int getNumberOfSemaphores()throws RemoteException;
	public Table getNextTable() throws RemoteException;
	public void setNextTable(Table table) throws RemoteException;
	public void registerNewForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException;
	public void removeForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException;
}
