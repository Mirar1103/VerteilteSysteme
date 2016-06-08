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

	public boolean hasOwner()throws RemoteException;
	public boolean sitDown(Philosopher owner)throws RemoteException;
	public void standUp()throws RemoteException;
}
