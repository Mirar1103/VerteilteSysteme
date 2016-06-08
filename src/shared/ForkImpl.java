/**
 * 
 */
package shared;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dominik
 *
 * 27.05.2016
 */
public class ForkImpl extends UnicastRemoteObject implements Fork{
	/**
	 * Philosopher who owns this fork
	 */
	
	private Philosopher owner = null;
	/**
	 * Reentrant lock
	 */
	private final ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Initialize a fork.
	 * @param owner possible owner or null
	 */
	public ForkImpl(Philosopher owner) throws RemoteException {
		this.owner = owner;
	}
	
	/**
	 * Checks if the fork has an owner.
	 * @return true if it has an owner
	 */
	public boolean hasOwner(){
		if(this.owner != null)
			return true;
		return false;
	}
	
	/**
	 * Specified Philosopher picks up this fork.
	 * @param owner the philosopher
	 * @return true if successfull
	 */
	public boolean pickUpFork(Philosopher owner){
		boolean picked = false;

		try{
			lock.lock();
			if(!hasOwner()){
				this.owner = owner;
				picked = true;
			}
			else
				picked = false;
		} finally{
			lock.unlock();
		}
		return picked;
	}
	
	/**
	 * Drops this fork.
	 */
	public void drop(){
		try{
			lock.lock();
			owner = null;
		} finally{
			lock.unlock();
		}
	}
}
