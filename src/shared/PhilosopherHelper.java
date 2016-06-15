package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Maximilian on 15.06.2016.
 */
public interface PhilosopherHelper extends Remote {

    public void addPhilosopher(Philosopher phil) throws RemoteException;
    public void removePhilosopher(Philosopher phil)throws RemoteException;
}
