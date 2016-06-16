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

	/**
	 * sets the table this Philosopher tries to eat at.
	 * @param table
	 * @throws RemoteException
     */
	public void setTable(Table table) throws RemoteException;

	/**
	 * returns ho often this philosopher has eaten
	 * @return
	 * @throws RemoteException
     */
	public int getTotalEatenRounds() throws RemoteException;

	/**
	 * returns the Id of this Philosopher, consisting of its number and the Ip of its crateing system.
	 * @return
	 * @throws RemoteException
     */
	public String getID() throws RemoteException;

	/**
	 * returns the thread of this Philosopher.
	 * @return
	 * @throws RemoteException
     */
	public Thread getThread()throws RemoteException;

	/**
	 * this methode is used to stop the Philosopher in methods called by himself.
	 * @throws RemoteException
	 * @throws InterruptedException
     */
	public void kill()throws RemoteException, InterruptedException;

	/**
	 * returns an integer representing chance in percent how likely he is to go eat after thinking.
	 * @return
	 * @throws RemoteException
     */
	public int getHunger()throws RemoteException;

	/**
	 * returns true if the Philosopher is banned.
	 * @return
	 * @throws RemoteException
     */
	public boolean getBanned()throws RemoteException;

	/**
	 * enables or disables textoutput based on the given boolean.
	 * @param isWanted (true=0 messages are shown, false == no non-error messages are shown)
	 * @throws RemoteException
     */
	public void setShowOutput(boolean isWanted) throws RemoteException;

	/**
	 * used by others to stop this thread.
	 * @throws RemoteException
     */
	public void softKill() throws RemoteException;

	/**
	 * banns the Philosopher from eating.
	 * @throws RemoteException
     */
	public void ban() throws RemoteException;

	/**
	 * returns true if this Philosopher isn't holding any locks and can be deleted safely.
	 * @return
	 * @throws RemoteException
     */
	public boolean isAbleForRemoving() throws RemoteException;

	/**
	 * sets the Master this Philosopher should report to.
	 * @param master
     */
	public void setMaster(Master master);

}
