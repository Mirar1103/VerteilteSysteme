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

/**
 * @author Dominik Ernsberger
 *
 * 27.05.2016
 */
public class TableImpl extends UnicastRemoteObject implements Table, Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<Fork> forkList;
	private List<Seat> seatList;
	private List<Semaphore> semaphoreList;
	private List<Philosopher> philosophers = new ArrayList<Philosopher>();
	private int seatsPerSemaphore;
	private int seatsLastSemaphore;
	private Table nextTable = this;
	private int id = -1;
	private boolean showOutput;
	private PhilosopherHelper philHelp;
	
	private final static int MAX_SEATS_SEMAPHORE = 10;
	private final static int MIN_SEMAPHORES = 1;
	
	/**
	 * Initialize the table.
	 *
	 */
	public TableImpl() throws RemoteException {
		forkList = Collections.synchronizedList(new ArrayList<Fork>());
		seatList = Collections.synchronizedList(new ArrayList<Seat>());
	}

	/**
	 * Calculates the number of Semaphores and the seats.
	 * @param numberSeats the number of seats for this table
	 * @throws RemoteException
	 */
	public void setSeats(int numberSeats) throws RemoteException {
		int numberSemaphore = numberSeats / MAX_SEATS_SEMAPHORE;

		if(numberSemaphore < MIN_SEMAPHORES ){
			numberSemaphore = MIN_SEMAPHORES;
			semaphoreList = new ArrayList<Semaphore>(numberSemaphore);
			seatsPerSemaphore = numberSeats / (numberSemaphore);

			seatsLastSemaphore = numberSeats;
			semaphoreList.add(new Semaphore(seatsLastSemaphore));
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
	public Seat takeSeat(Philosopher owner) throws RemoteException {
		boolean foundFreeSemaphore = false;
		boolean philInList = false;
		int seat;
		int acquiredSemaphore = -1;
		int possibleSeats;
		int offsetSeatSemaphore;
		int randomSemaphore = Math.abs(new Random().nextInt()% getNumberOfSemaphores());
		
		for(int i = 0 ; i < philosophers.size(); i++){
			if(philosophers.get(i).getID().equals(owner.getID()))
				philInList = true;
			if(!philInList)	
				philosophers.add(owner);
			break;
		}
		
			
		
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
				if((!seatList.get(seat).hasOwner()) && (!seatList.get((seat+1)%getNumberOfSeats()).hasOwner()) && (!seatList.get((seat+getNumberOfSeats()-1)%getNumberOfSeats()).hasOwner())){
					if(seatList.get(seat).sitDown(owner))
						return seatList.get(seat);
				}
			}
			//test for a free seat with max. one neighbor
			for(int index = 0; index < possibleSeats; index++){
				seat = getOffsetSeat(index, offsetSeatSemaphore);
				if((!seatList.get(seat).hasOwner()) && ((!seatList.get((seat+1)%getNumberOfSeats()).hasOwner()) || (!seatList.get((seat+getNumberOfSeats()-1)%getNumberOfSeats()).hasOwner()))){
					if(seatList.get(seat).sitDown(owner))
						return seatList.get(seat);
				}
			}
			//test for a free seat
			for(int index = 0; index < possibleSeats; index++){
				seat = getOffsetSeat(index, offsetSeatSemaphore);
				if(seatList.get(seat).sitDown(owner))
					return seatList.get(seat);
			}
		}
		return null;
	}
	
	/**
	 * Stands up and release the seat.
	 * @param seat the seat number
	 */
	public void standUp(Seat seat) throws RemoteException {
		seat.standUp();
		int releasedSemaphore = seatList.indexOf(seat) / seatsPerSemaphore;
		if(releasedSemaphore > (getNumberOfSemaphores()-1))
			semaphoreList.get(getNumberOfSemaphores()-1).release();
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
		return seatList.size();
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
	 * @throws RemoteException
	 */
	@Override
	public void registerNewForkAndSeat(Fork fork, Seat seat) throws RemoteException {
		boolean added = false;
		if(philosophers.size() > 0){
			while(!added){
				if(!forkList.get(forkList.size()-1).hasOwner() && !seatList.get(seatList.size()-1).hasOwner()){
					forkList.add(fork);
					seatList.add(seat);
					Semaphore tmp = semaphoreList.get(semaphoreList.size()-1);
					tmp.release();
					added = true;
				}
			}
			
		}
		else{
			forkList.add(fork);
			seatList.add(seat);
			setSeats(getNumberOfSeats());
		}
			
		seatsLastSemaphore++;
		System.out.println("seat and fork was added - total #" + getNumberOfSeats());
		
	}

	/**
	 * Remove a given fork and seat.
	 * @throws RemoteException
	 */
	@Override
	public void removeForkAndSeat() throws RemoteException {
		/*forkList.remove(fork);
		seatList.remove(seat);*/
		
		if(philosophers.size() > 0){
			Semaphore tmp = semaphoreList.get(semaphoreList.size()-1);
			try {
				tmp.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean seatRemoved=false;
			while(!seatRemoved){
				for(int lastSeat = getNumberOfSeats()-1; lastSeat>(getNumberOfSeats()-seatsLastSemaphore) && !seatRemoved;lastSeat--){
					if(!seatList.get(lastSeat).hasOwner() && !forkList.get(lastSeat).hasOwner()){
						seatList.remove(lastSeat);
						forkList.remove(lastSeat);
						seatRemoved=true;
						seatsLastSemaphore--;
					}
				}
			}
			if (seatsLastSemaphore == 0){
				semaphoreList.remove(semaphoreList.size()-1);
				seatsLastSemaphore=seatsPerSemaphore;
			}
		}
		else {
			if (seatsLastSemaphore == 0){
				System.out.println("Cant remove - no more chairs and forks");
			} else {
				forkList.remove(forkList.size()-1);
				seatList.remove(seatList.size()-1);
				setSeats(getNumberOfSeats() - 1);
				seatsLastSemaphore--;
				if(seatsLastSemaphore == 0){
					semaphoreList.remove(semaphoreList.size()-1);
					seatsLastSemaphore=seatsPerSemaphore;
				}
			}
		}
		System.out.println("seat and fork was removed- total #" + getNumberOfSeats());
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
		if(showOutput) {
			System.out.println("Moving philosopher "+phil.getID());
		}
		int hunger = phil.getHunger();
		String philID = phil.getID();
		int meals = phil.getTotalEatenRounds();
		boolean banned = phil.getBanned();
		nextTable.recreatePhilosopher(hunger, philID, meals, banned);
		philosophers.remove(phil);
		philHelp.removePhilosopher(phil);
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
	public void recreatePhilosopher(int hunger, String philID, int meals, boolean banned) throws RemoteException {
		PhilosopherImpl phil = new PhilosopherImpl(this, hunger, philID, meals, banned);
		philosophers.add(phil);
		new Thread(phil).start();
		philHelp.addPhilosopher(phil);
		if(showOutput) {
			System.out.println("TablePart #" + this.id + " received an existing philosopher " + philID);
		}
	}

	public void setShowOutput(boolean isWanted)throws RemoteException{
		showOutput = isWanted;
	}

	public int getSeatPosition(Seat seat) throws RemoteException{
		return seatList.indexOf(seat);
	}
	public void setPhilHelp(PhilosopherHelper philHelp) throws RemoteException{
		this.philHelp = philHelp;
	}
	
	public PhilosopherHelper getPhilHelp() throws RemoteException {
		return philHelp;
	}
	
	
	public void removePhilosopher(Philosopher phil) throws RemoteException {
		boolean philRemoved = false;
		while (!philRemoved) {
			if (phil.isAbleForRemoving()) {
				philosophers.remove(phil);
				philRemoved = true;
			}
		}
	}
	
}
