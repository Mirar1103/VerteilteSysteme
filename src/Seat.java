import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 */

/**
 * @author Dominik Ernsberger
 *
 * 04.04.2016
 */
public class Seat <Philosopher> {

	/**
	 * Philosopher who owns this seat
	 */
	private Philosopher owner = null;
	
	/**
	 * Reentrant lock
	 */
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Initialize a seat.
	 * @param owner
	 */
	public Seat(Philosopher owner){
		this.owner = owner;
	}
	
	/**
	 * Checks if the seat has a owner.
	 * @return true if the seat is busy
	 */
	public boolean hasOwner(){
		if(this.owner != null)
			return true;
		return false;
	}
	
	/**
	 * Sit down.
	 * @param owner the philosopher who wants to sit down
	 * @return true if successfull
	 */
	public boolean sitDown(Philosopher owner){
		boolean sitting = false;
		
		try{
			lock.lock();
			if(!hasOwner()){
				this.owner = owner;
				sitting = true;
			}
			else
				sitting = false;
		} finally{
			lock.unlock();
		}
		
		return sitting;
	}
	
	/**
	 * Stand up.
	 */
	public void standUp(){
		try{
			lock.lock();
			owner = null;
		} finally{
			lock.unlock();
		}
	}
	
}
