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
public interface Fork extends Remote {
	/**
	 * true if this fork is owned.
	 * @return
	 * @throws RemoteException
     */
	public boolean hasOwner()throws RemoteException;

	/**
	 * this methode is used to pick up this fork by a Philosopher.
	 * @param owner the philosopher trying to claim this fork
	 * @return
	 * @throws RemoteException
     */
	public boolean pickUpFork(Philosopher owner)throws RemoteException;

	/**
	 * this methode is called to release this fork.
	 * @throws RemoteException
     */
	public void drop()throws RemoteException;
}
