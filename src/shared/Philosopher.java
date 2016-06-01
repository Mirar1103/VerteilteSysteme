/**
 * 
 */
package shared;

import java.rmi.RemoteException;

/**
 * @author Dominik
 *
 * 29.05.2016
 */
public interface Philosopher {

	public void stop();
	public void setTable(Table table) throws RemoteException; 
}
