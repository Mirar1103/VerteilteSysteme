import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * 
 */

/**
 * @author Dominik Ernsberger
 *
 * 04.04.2016
 * 
 * updated: 20.04.2016
 */
public class Table <Philosopher> {

	private final int numberSeats;
	private final List<Fork<Philosopher>> forkList;
	private final List<Seat<Philosopher>> seatList;
	private final List<Semaphore> semaphoreList;
	private final int seatsPerSemaphore;
	private final int seatsLastSemaphore;
	
	private final static int MAX_SEATS_SEMAPHORE = 10;
	private final static int MIN_SEMAPHORES = 4;
	
	/**
	 * Initialize the table.
	 * @param numberSeats number of the seats
	 */
	public Table(int numberSeats){
		this.numberSeats = numberSeats;
		forkList = Collections.synchronizedList(new ArrayList<Fork<Philosopher>>(numberSeats));
		seatList = Collections.synchronizedList(new ArrayList<Seat<Philosopher>>(numberSeats));
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
		
		for(int index = 0; index < numberSeats; index++){
			seatList.add(new Seat<Philosopher>(null));
			forkList.add(new Fork<Philosopher>(null));
		}
	}
	
	/**
	 * Takes a seat on this table.
	 * @param owner the Philosopher who wants to sit down
	 * @return the seat number or -1 if no seat was taken
	 */
	public int takeSeat(Philosopher owner){
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
				return -1;
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
	public void standUp(int seat){
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
	public boolean pickUpFork(int fork, Philosopher owner){
		return forkList.get(fork).pickUpFork(owner);
	}
	
	/**
	 * Drops a single fork back on the table.
	 * @param fork the fork number
	 */
	public void dropFork(int fork){
		forkList.get(fork).drop();
	}
	
	/**
	 * Returns the number of seats on the table.
	 * @return the number of seats
	 */
	public int getNumberOfSeats(){
		return numberSeats;
	}
	
	public int getNumberOfSemaphores(){
		return semaphoreList.size();
	}
	
	private int getOffsetSeat(int seat, int offset){
		return ((seat+offset)%getNumberOfSeats());
	}
}
