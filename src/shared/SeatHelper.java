package shared;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maximilian on 01.06.2016.
 */
public class SeatHelper {
    /**
     * the table new seats are placed at.
     */
    private Table table;

    /**
     * creataes anew SeatHelper.
     * @param table
     */
    public SeatHelper(Table table){
        this.table = table;
    }

    /**
     * creates a number of new Seats.
     * @param numberOfSeats
     * @throws RemoteException
     */
    public synchronized void addSeat(int numberOfSeats) throws RemoteException {
        if(numberOfSeats < 1)
            throw new IllegalArgumentException("Number has to be greater than zero.");

        for(int i = 0; i < numberOfSeats; i++){
            SeatImpl seat = new SeatImpl(null);
            ForkImpl fork = new ForkImpl(null);
            table.registerNewForkAndSeat(fork, seat);
        }
    }

    /**
     * removes a number of seats from the table.
     * @param numberOfSeats
     * @throws RemoteException
     */
    public synchronized void removeSeat(int numberOfSeats) throws RemoteException {
        if(numberOfSeats < 1)
            throw new IllegalArgumentException("Wrong number of Seats for removing.");

        for(int i = 0; i < numberOfSeats; i++){
            table.removeForkAndSeat();
        }
    }
    
    public void setTable(Table table){
    	this.table = table;
    }
    
    public Table getTable(){
    	return this.table;
    }
}

