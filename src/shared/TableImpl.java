/**
 * 
 */
package shared;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * @author Dominik
 *
 * 27.05.2016
 */
public class TableImpl extends UnicastRemoteObject implements Table{
	private int numberSeats;
	private List<Fork> forkList;
	private List<Seat> seatList;
	private List<String> seatHosts;
	private List<Semaphore> semaphoreList;
	private int seatsPerSemaphore;
	private int seatsLastSemaphore;
	private Table nextTable = this;
	
	private final static int MAX_SEATS_SEMAPHORE = 10;
	private final static int MIN_SEMAPHORES = 4;
	
	/**
	 * Initialize the table.
	 *
	 */
	public TableImpl() throws RemoteException {
		forkList = Collections.synchronizedList(new ArrayList<Fork>());
		seatList = Collections.synchronizedList(new ArrayList<Seat>());
		seatHosts = Collections.synchronizedList(new ArrayList<String>());

	}

	public void setSeats(int numberSeats) throws RemoteException {
		this.numberSeats = numberSeats;
		int numberSemaphore = numberSeats / MAX_SEATS_SEMAPHORE;

		if(numberSemaphore < MIN_SEMAPHORES ){
			numberSemaphore = MIN_SEMAPHORES;
			semaphoreList = new ArrayList<Semaphore>(numberSemaphore);
			seatsPerSemaphore = numberSeats / (numberSemaphore-1);

			for(int index = 0; index < numberSemaphore-1; index++){
				semaphoreList.add(new Semaphore(seatsPerSemaphore));
			}
			seatsLastSemaphore = numberSeats % (numberSemaphore-1);
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

		/*for(int index = 0; index < numberSeats; index++){
			seatList.add(new SeatImpl(null));
			forkList.add(new ForkImpl(null));
		}*/
	}
	
	/**
	 * Takes a seat on this table.
	 * @param owner the Philosopher who wants to sit down
	 * @return the seat number or -1 if no seat was taken
	 */
	public int takeSeat(Philosopher owner) throws RemoteException {
		int seat;
		int acquiredSemaphore = -1;
		int possibleSeats;
		int offsetSeatSemaphore;
		int randomSemaphore = Math.abs(new Random().nextInt()% getNumberOfSemaphores());
		
		for(int index = 0; index < getNumberOfSemaphores(); index++){
			acquiredSemaphore = (index+randomSemaphore)%getNumberOfSemaphores();
			if(semaphoreList.get(acquiredSemaphore).tryAcquire())
				break;
			else if(index == getNumberOfSemaphores()-1)
				try {
					semaphoreList.get(acquiredSemaphore).acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
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
		if(fork<forkList.size()) {
			return forkList.get(fork).pickUpFork(owner);
		}
		else
		{
			return pickUpOtherTablesFork(owner);
		}
	}

	/**
	 * tries to take the first fork of the next table
	 * @param owner the philosopher taking the fork
	 * @return true if philosopher got the fork
	 * @throws RemoteException
     */
	private boolean pickUpOtherTablesFork( Philosopher owner) throws RemoteException {
		return nextTable.pickUpFork(0, owner);
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
	 * @return
     */
	public Table getNextTable(){
		return nextTable;
	}

	/**
	 * Drops a single fork back on the table.
	 * @param fork the fork number
	 */
	public void dropFork(int fork) throws RemoteException {
		forkList.get(fork).drop();
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

	/* (non-Javadoc)
	 * @see shared.Table#registerNewForkAndSeat(shared.Fork, shared.Seat, java.lang.String)
	 */
	@Override
	public void registerNewForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException {
		forkList.add(fork);
		seatList.add(seat);
		seatHosts.add(host);
		setSeats(numberSeats+1);
		System.out.println("seat was added");
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see shared.Table#removeForkAndSeat(shared.Fork, shared.Seat, java.lang.String)
	 */
	@Override
	public void removeForkAndSeat(Fork fork, Seat seat, String host) throws RemoteException {
		forkList.remove(fork);
		seatList.remove(seat);
		seatHosts.remove(host);
		setSeats(numberSeats-1);
		// TODO Auto-generated method stub
		System.out.println("seat was removed");
	}
}
