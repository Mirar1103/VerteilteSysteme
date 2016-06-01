/**
 * 
 */
package shared;

import java.rmi.RemoteException;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public interface Table {

	public int takeSeat(Philosopher owner);
	public void standUp(int seat);
	public boolean pickUpFork(int fork, Philosopher owner);
	public void dropFork(int fork);
	public int getNumberOfSeats();
	public int getNumberOfSemaphores();
	
	public void registerNewForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException;
	public void removeForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException;
}
