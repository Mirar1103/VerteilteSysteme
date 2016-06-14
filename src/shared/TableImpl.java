/**
 * 
 */
package shared;


import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

/**
 * @author Dominik Ernsberger
 *
 * 27.05.2016
 */
public class TableImpl extends UnicastRemoteObject implements Table, Serializable{
	
	private static final long serialVersionUID = 1L;
	private int numberSeats;
	private List<Fork> forkList;
	private List<Seat> seatList;
	private List<String> seatHosts;
	private List<Semaphore> semaphoreList;
	private List<Thread> philosophers = new ArrayList<Thread>();
	private int seatsPerSemaphore;
	private int seatsLastSemaphore;
	private Table nextTable = this;
	private int id = -1;
	
	private final static int MAX_SEATS_SEMAPHORE = 10;
	private final static int MIN_SEMAPHORES = 1;
	
	/**
	 * Initialize the table.
	 *
	 */
	public TableImpl() throws RemoteException {
		forkList = Collections.synchronizedList(new ArrayList<Fork>());
		seatList = Collections.synchronizedList(new ArrayList<Seat>());
		seatHosts = Collections.synchronizedList(new ArrayList<String>());
	}

	/**
	 * Calculates the number of Semaphores and the seats.
	 * @param numberSeats the number of seats for this table
	 * @throws RemoteException
	 */
	public void setSeats(int numberSeats) throws RemoteException {
		this.numberSeats = numberSeats;
		int numberSemaphore = numberSeats / MAX_SEATS_SEMAPHORE;

		if(numberSemaphore < MIN_SEMAPHORES ){
			numberSemaphore = MIN_SEMAPHORES;
			semaphoreList = new ArrayList<Semaphore>(numberSemaphore);
			seatsPerSemaphore = numberSeats / (numberSemaphore);

			for(int index = 0; index < numberSemaphore; index++){
				semaphoreList.add(new Semaphore(seatsPerSemaphore));
			}
			seatsLastSemaphore = numberSeats;
		}
		else{
			seatsLastSemaphore = numberSeats % MAX_SEATS_SEMAPHORE;
			seatsPerSemaphore = MAX_SEATS_SEMAPHORE;
			semaphoreList = new ArrayList<Semaphore>(numberSemaphore);

			for(int index = 0; index < numberSemaphore; index++){
				semaphoreList.add(new Semaphore(seatsPerSemaphore));
			}
			semaphoreList.add(new Semaphore(seatsLastSemaphore));
		}
	}
	
	/**
	 * Takes a seat on this table.
	 * @param owner the Philosopher who wants to sit down
	 * @return the seat number or -1 if no seat was taken
	 */
	public int takeSeat(Philosopher owner) throws RemoteException {
		boolean foundFreeSemaphore = false;
		int seat;
		int acquiredSemaphore = -1;
		int possibleSeats;
		int offsetSeatSemaphore;
		int randomSemaphore = Math.abs(new Random().nextInt()% getNumberOfSemaphores());
		
		if(!philosophers.contains(owner.getThread()))
			philosophers.add(owner.getThread());
		
		for(int index = 0; index < getNumberOfSemaphores(); index++){
			acquiredSemaphore = (index+randomSemaphore)%getNumberOfSemaphores();
			if(semaphoreList.get(acquiredSemaphore).tryAcquire()){
				foundFreeSemaphore = true;
				break;
			}
		}
		if(foundFreeSemaphore){
			if(acquiredSemaphore == getNumberOfSemaphores()-1)
				possibleSeats = seatsLastSemaphore;
			else
				possibleSeats = seatsPerSemaphore;
			
			offsetSeatSemaphore = acquiredSemaphore * seatsPerSemaphore;
			
			//test for a free seat with no neighbors 
			for(int index = 0; index < possibleSeats; index++){
				seat = getOffsetSeat(index, offsetSeatSemaphore);
				if((!seatList.get(seat).hasOwner()) && (!seatList.get((seat+1)%numberSeats).hasOwner()) && (!seatList.get((seat+numberSeats-1)%numberSeats).hasOwner())){
					if(seatList.get(seat).sitDown(owner))
						return seat;
				}
			}
			//test for a free seat with max. one neighbor
			for(int index = 0; index < possibleSeats; index++){
				seat = getOffsetSeat(index, offsetSeatSemaphore);
				if((!seatList.get(seat).hasOwner()) && ((!seatList.get((seat+1)%numberSeats).hasOwner()) || (!seatList.get((seat+numberSeats-1)%numberSeats).hasOwner()))){
					if(seatList.get(seat).sitDown(owner))
						return seat;
				}
			}
			//test for a free seat
			for(int index = 0; index < possibleSeats; index++){
				seat = getOffsetSeat(index, offsetSeatSemaphore);
				if(seatList.get(seat).sitDown(owner))
					return seat;
			}
		}
		return -1;
	}
	
	/**
	 * Stands up and release the seat.
	 * @param seat the seat number
	 */
	public void standUp(int seat) throws RemoteException {
		seatList.get(seat).standUp();
		int releasedSemaphore = seat / seatsPerSemaphore;
		if(releasedSemaphore > (getNumberOfSemaphores()-1))
			semaphoreList.get(releasedSemaphore-1).release();
		else
			semaphoreList.get(releasedSemaphore).release();
	}
	
	/**
	 * Picks up a single fork from the table.
	 * @param fork the fork number
	 * @param owner the philosopher which wants to pick up the fork
	 * @return true if the philosopher gots the fork
	 */
	public boolean pickUpFork(int fork, Philosopher owner) throws RemoteException {
		if(fork < forkList.size()) {
			return forkList.get(fork).pickUpFork(owner);
		}
		else
		{
			return getNextTable().pickUpFork(0, owner);
		}
	}

	/**
	 * allows to set the next Table when a new table is added.
	 * @param table the new following table
	 * @throws RemoteException
     */
	public void setNextTable(Table table) throws RemoteException{
		nextTable = table;
	}

	/**
	 * returns the Table after this one.
	 * @return the next table
     */
	public Table getNextTable(){
		return nextTable;
	}

	/**
	 * Drops a single fork back on the table.
	 * @param fork the fork number
	 * @throws RemoteException
	 */
	public void dropFork(int fork) throws RemoteException {
		if(fork < forkList.size())
			forkList.get(fork).drop();
		else
			getNextTable().dropFork(0);
	}
	
	/**
	 * Returns the number of seats on the table.
	 * @return the number of seats
	 */
	public int getNumberOfSeats(){
		return numberSeats;
	}
	
	/**
	 * Returns the number of Semaphores.
	 * @return the number of semaphores
	 */
	public int getNumberOfSemaphores(){
		return semaphoreList.size();
	}
	
	/**
	 * Returns a seat number.
	 * Calculates the seat number with a given seat an and given offset.
	 * @param seat a given seat
	 * @param offset a given offset
	 * @return a seat number 
	 */
	private int getOffsetSeat(int seat, int offset){
		return ((seat+offset)%getNumberOfSeats());
	}

	/**
	 * Register a new Fork and a new Seat to the Table.
	 * @param fork new fork
	 * @param seat new seat
	 * @param host
	 * @throws RemoteException
	 */
	@Override
	public void registerNewForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException {
		forkList.add(fork);
		seatList.add(seat);
		seatHosts.add(host);
		
		if(philosophers.size() > 0){
			Semaphore tmp = semaphoreList.get(semaphoreList.size()-1);
			tmp.release();
		}
		else
			setSeats(numberSeats+1);
			
		System.out.println("seat was added");
		
	}

	/**
	 * Remove a given fork and seat.
	 * @param fork the given fork
	 * @param seat the given seat
	 * @param host
	 * @throws RemoteException
	 */
	@Override
	public void removeForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException {
		forkList.remove(fork);
		seatList.remove(seat);
		seatHosts.remove(host);
		
		if(philosophers.size() > 0){
			Semaphore tmp = semaphoreList.get(semaphoreList.size()-1);
			//TODO - decrease Semaphore
		}
		else
			setSeats(numberSeats+1);
		// TODO Auto-generated method stub
		System.out.println("seat was removed");
	}
	
	/**
	 * Return the ID of the Table part.
	 * @return the ID of the table
	 * @throws RemoteException
	 */
	public int getID() throws RemoteException{
		return id;
	}
	
	/**
	 * Set the ID of the Table.
	 * Only possible once.
	 * @param id the ID
	 * @throws RemoteException
	 */
	public void setID(int id) throws RemoteException{
		if(this.id == -1)
			this.id = id;
	}
	
	/**
	 * Moves a Philosopher Thread to another table.
	 * Kills the current Thread and call the function on the next table to recreate the given thread.
	 * It recreates it with the hunger, ID, totalMeals and the banned status.
	 * @param phil the given Philosopher
	 * @throws RemoteException
	 * @throws InterruptedException 
	 */
	public void movePhilosopher(Philosopher phil) throws RemoteException, InterruptedException {
		System.out.println("Moving philosopher "+phil.getID());
		int hunger = phil.getHunger();
		int philID = phil.getID();
		int meals = phil.getTotalEatenRounds();
		boolean banned = phil.getBanned();
		nextTable.recreatePhilosopher(hunger, philID, meals, banned);
		philosophers.remove(phil.getThread());
		phil.kill();		
	}
	
	/**
	 * Recreates a Philosopher Thread on the current Table.
	 * Starts a new Thread with the old values like hunger, ID, totalMeals and the banned status of an moving Philosopher.
	 * @param hunger the hunger value
	 * @param philID the ID of the philosopher
	 * @param meals the total number of meals of this phil
	 * @param banned the banned status 
	 */
	public void recreatePhilosopher(int hunger, int philID, int meals, boolean banned) throws RemoteException {
		Thread phil = new Thread(new PhilosopherImpl(this, hunger, philID, meals, banned));
		philosophers.add(phil);
		phil.start();
		System.out.println("TablePart #" + this.id + " received an existing philosopher " + philID);
	}
}
