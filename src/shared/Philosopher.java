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
public interface Philosopher extends Remote {

	public void stop() throws RemoteException;
	public void setTable(Table table) throws RemoteException; 
}
