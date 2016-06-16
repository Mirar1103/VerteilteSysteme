package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Maximilian on 10.06.2016.
 */
public interface Master extends Remote{


	/**
	 * registers a table to this Master for tracking.
	 * @param table
	 * @throws RemoteException
     */
	void registerTable(Table table) throws RemoteException;

	/**
	 * registers a seathelper to this Master to enable him to recreate a lost tables seats.
	 * @param seatHelper
	 * @throws RemoteException
     */
	void registerSeatHelper(SeatHelper seatHelper) throws RemoteException;

	/**
	 * removes a philosopher from the list of tracked Philosophers.
	 * @param phil
	 * @throws RemoteException
     */
	void removePhilosopher(Philosopher phil) throws RemoteException;

	/**
	 * removes a table from tracking.
	 * @param table
	 * @throws RemoteException
     */
	void removeTable(Table table) throws RemoteException;

	/**
	 * updates the backup-data on the given Philosopher.
	 * @param phil
	 * @throws RemoteException
     */
	void updatePhilosopher(Philosopher phil)throws RemoteException;

	/**
	 * updates the backup-data on the given table.
	 * @param table
	 * @throws RemoteException
     */
	void updateTable(Table table) throws RemoteException;

}
