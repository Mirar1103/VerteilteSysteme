/**
 * 
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Dominik
 *
 * 29.05.2016
 */
public interface Seat extends Remote {

	/**
	 * returns true, if a Philosopher sits on this seat.
	 * @return
	 * @throws RemoteException
     */
	public boolean hasOwner()throws RemoteException;

	/**
	 * used b Philosophers to sit down on this seat, returns true if succesfull.
	 * @param owner
	 * @return
	 * @throws RemoteException
     */
	public boolean sitDown(Philosopher owner)throws RemoteException;

	/**
	 * used by philosophers to release this seat.
	 * @throws RemoteException
     */
	public void standUp()throws RemoteException;
}
