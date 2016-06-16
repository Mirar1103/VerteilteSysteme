package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Maximilian on 10.06.2016.
 */
public interface Master extends Remote{



	void registerTable(Table table) throws RemoteException;

	void registerSeatHelper(SeatHelper seatHelper) throws RemoteException;
	void removePhilosopher(Philosopher phil) throws RemoteException;
	void removeTable(Table table) throws RemoteException;
	void updatePhilosopher(Philosopher phil)throws RemoteException;
	void updateTable(Table table) throws RemoteException;

}
