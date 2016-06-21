/**
 * 
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public interface Table extends Remote{

	/**
	 * seraches for a free seat for the given philosopher, returns null if no free one is available.
	 * @param owner
	 * @return
	 * @throws RemoteException
     */
	public Seat takeSeat(Philosopher owner) throws RemoteException;

	/**
	 * used to relaese the lock on a given seat.
	 * @param seat
	 * @throws RemoteException
     */
	public void standUp(Seat seat)throws RemoteException;

	/**
	 * used to take hold of the given fork by the given Philosopher, returns true if successful.
	 * @param fork
	 * @param owner
	 * @return
	 * @throws RemoteException
     */
	public boolean pickUpFork(int fork, Philosopher owner)throws RemoteException;

	/**
	 * releases the given Fork.
	 * @param fork
	 * @throws RemoteException
     */
	public void dropFork(int fork)throws RemoteException;

	/**
	 * returns the number of seats at this table.
	 * @return
	 * @throws RemoteException
     */
	public int getNumberOfSeats()throws RemoteException;

	/**
	 * returns the numebr of semaphres used to divide seatgroups at this table.
	 * @return
	 * @throws RemoteException
     */
	public int getNumberOfSemaphores()throws RemoteException;

	/**
	 * returns the table to the right of this one.
	 * @return
	 * @throws RemoteException
     */
	public Table getNextTable() throws RemoteException;

	/**
	 * used to change the table, this one considers following itself.
	 * @param table
	 * @throws RemoteException
     */
	public void setNextTable(Table table) throws RemoteException;

	/**
	 * adds a new Fokr and a new Seat to thsi table.
	 * @param fork
	 * @param seat
	 * @throws RemoteException
     */
	public void registerNewForkAndSeat(Fork fork, Seat seat) throws RemoteException;

	/**
	 * removes one fokr and one seat from this table.
	 * @throws RemoteException
     */
	public void removeForkAndSeat() throws RemoteException;

	/**
	 * returne the ID of thsi table.
	 * @return
	 * @throws RemoteException
     */
	public int getID() throws RemoteException;

	/**
	 * sets the ID of this table
	 * @param id
	 * @throws RemoteException
     */
	public void setID(int id) throws RemoteException;

	/**
	 * moves the given Philosopher away from this table.
	 * @param phil
	 * @throws RemoteException
	 * @throws InterruptedException
     */
	public void movePhilosopher(Philosopher phil) throws RemoteException, InterruptedException;

	/**
	 * recreates a moved Philosopher at this table.
	 * @param hunger
	 * @param id
	 * @param totalEatenRounds
	 * @param banned
	 * @param debug
	 * @throws RemoteException
     */
	public void recreatePhilosopher(int hunger, String id, int totalEatenRounds, boolean banned, boolean debug) throws RemoteException;

	/**
	 * sets if messages should be shown
	 * @param isWanted
	 * @throws RemoteException
     */
	public void setShowOutput(boolean isWanted) throws RemoteException;

	/**
	 * gets the position of the given seat in this tables list of seats.
	 * @param seat
	 * @return
	 * @throws RemoteException
     */
	public int getSeatPosition(Seat seat) throws RemoteException;

	/**
	 * sets the PhilosopherHelper, this table works with when moving philosophers.
	 * @param philHelp
	 * @throws RemoteException
     */
	public void setPhilHelp(PhilosopherHelperImpl philHelp) throws RemoteException;

	/**
	 * reutrns the PhilosopherHelper this table works with.
	 * @return
	 * @throws RemoteException
     */
	public PhilosopherHelperImpl getPhilHelp() throws RemoteException;

	/**
	 * removes a Philosopher form this table.
	 * @throws RemoteException
     */
	public void removePhilosopher() throws RemoteException;
	
	public void createPhilosopher(boolean hunger, boolean debugging, String ip) throws RemoteException;

	public void setMaster(Master master) throws RemoteException;
	public List<Philosopher> getPhilosophers() throws RemoteException;


	public boolean checkFirstFork(Philosopher phil) throws RemoteException;

	void reLeaseFirstFork(Philosopher phil) throws RemoteException;
}
