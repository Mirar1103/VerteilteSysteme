package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Maximilian on 15.06.2016.
 */
public interface PhilosopherHelper extends Remote {

    public void addPhilosopher(PhilosopherImpl phil) throws RemoteException;
    public void removePhilosopher(Philosopher phil)throws RemoteException;
    
    public void addPhilosopher(int numberOfPhil, boolean debugging) throws RemoteException;
    public void removePhilosopher(int numberOfPhil) throws RemoteException;
    public void setDebugging(boolean isWanted) throws RemoteException;
}
