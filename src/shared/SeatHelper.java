package shared;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maximilian on 01.06.2016.
 */
public class SeatHelper {
    private final Table table;
    private int createdSeats = 0;

    public SeatHelper(Table table){
        this.table = table;
    }

    public synchronized void addSeat(int numberOfSeats) throws RemoteException {
        if(numberOfSeats < 1)
            throw new IllegalArgumentException("Number has to be greater than zero.");

        for(int i = 0; i < numberOfSeats; i++){
            SeatImpl seat = new SeatImpl(null);
            ForkImpl fork = new ForkImpl(null);
            table.registerNewForkAndSeat(fork, seat);
            createdSeats++;
        }
    }

    public synchronized void removeSeat(int numberOfSeats) throws RemoteException {
        if(numberOfSeats < 1 || numberOfSeats > createdSeats)
            throw new IllegalArgumentException("Wrong number of Seats for removing.");

        for(int i = 0; i < numberOfSeats; i++){
            table.removeForkAndSeat();
            createdSeats--;
        }
    }
}

