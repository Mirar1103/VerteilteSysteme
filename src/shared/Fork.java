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

	public boolean hasOwner()throws RemoteException;
	public boolean pickUpFork(Philosopher owner)throws RemoteException;
	public void drop()throws RemoteException;
}
