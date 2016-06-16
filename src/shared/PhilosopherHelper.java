package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Maximilian on 15.06.2016.
 */
public interface PhilosopherHelper extends Remote {

    /**
     * adds a Philosopher to the tracked list of Philosophers.
     * @param phil
     * @throws RemoteException
     */
    public void addPhilosopher(PhilosopherImpl phil) throws RemoteException;

    /**
     * removes a certain Philosopher from the list of tracked Philosophers.
     * @param phil
     * @throws RemoteException
     */
    public void removePhilosopher(Philosopher phil)throws RemoteException;

    /**
     * creates a number of new Philosophers with their textoutpu showing or not based on the debugging booolean.
     * @param numberOfPhil
     * @param debugging (true == messages are displayed)
     * @throws RemoteException
     */
    public void addPhilosopher(int numberOfPhil, boolean debugging) throws RemoteException;

    /**
     * removes a number of Philosophers.
     * @param numberOfPhil
     * @param master
     * @throws RemoteException
     */
    public void removePhilosopher(int numberOfPhil, Master master) throws RemoteException;

    /**
     * changes the message display setting for all philosophers on this Objects list.
     * @param isWanted
     * @throws RemoteException
     */
    public void setDebugging(boolean isWanted) throws RemoteException;
}
