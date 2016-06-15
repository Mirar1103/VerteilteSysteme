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

	public void setTable(Table table) throws RemoteException;
	
	public int getTotalEatenRounds() throws RemoteException;
	public int getID() throws RemoteException;
	public Thread getThread()throws RemoteException;
	public void kill()throws RemoteException, InterruptedException;
	public int getHunger()throws RemoteException;
	public boolean getBanned()throws RemoteException;
	public void setShowOutput(boolean isWanted) throws RemoteException;
	public void softKill() throws RemoteException;
}
