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
 */
public class Table <Philosopher> {

	private final int numberSeats;
	private final List<Fork<Philosopher>> forkList;
	private final List<Seat<Philosopher>> seatList;
	private final Semaphore seatSemaphore;
	
	/**
	 * Initialize the table.
	 * @param numberSeats number of the seats
	 */
	public Table(int numberSeats){
		this.numberSeats = numberSeats;
		forkList = Collections.synchronizedList(new ArrayList<Fork<Philosopher>>(numberSeats));
		seatList = Collections.synchronizedList(new ArrayList<Seat<Philosopher>>(numberSeats));
		seatSemaphore = new Semaphore(numberSeats, true);
		
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
		try{
			seatSemaphore.acquire(); 
			int offset = Math.abs(new Random().nextInt()% getNumberOfSeats());
			//test for a free seat with no neighbors 
			for(int index = 0; index < numberSeats; index++){
				seat = getOffsetSeat(index, offset);
				if((!seatList.get(seat).hasOwner()) && (!seatList.get((seat+1)%numberSeats).hasOwner()) && (!seatList.get((seat+numberSeats-1)%numberSeats).hasOwner())){
					if(seatList.get(seat).sitDown(owner))
						return seat;
				}
			}
			//test for a free seat with max. one neighbor
			for(int index = 0; index < numberSeats; index++){
				seat = getOffsetSeat(index, offset);
				if((!seatList.get(seat).hasOwner()) && ((!seatList.get((seat+1)%numberSeats).hasOwner()) || (!seatList.get((seat+numberSeats-1)%numberSeats).hasOwner()))){
					if(seatList.get(seat).sitDown(owner))
						return seat;
				}
			}
			//test for a free seat
			for(int index = 0; index < numberSeats; index++){
				seat = getOffsetSeat(index, offset);
				if(seatList.get(seat).sitDown(owner))
					return seat;
			} 
			
		}catch (InterruptedException e){
			System.out.println(e.getMessage());
		}
		
		seatSemaphore.release();
		return -1;
	}
	
	/**
	 * Stands up and release the seat.
	 * @param seat the seat number
	 */
	public void standUp(int seat){
		seatList.get(seat).standUp();
		seatSemaphore.release();
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
	
	private int getOffsetSeat(int seat, int offset){
		return ((seat+offset)%getNumberOfSeats());
	}
}
