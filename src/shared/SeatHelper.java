package shared;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Maximilian on 01.06.2016.
 */
public class SeatHelper {
    private final Table table;
    private final String host;
    private List<Seat> listSeats = new LinkedList<Seat>();
    private List<Fork> listForks = new LinkedList<Fork>();

    public SeatHelper(Table table){
        this.table = table;
        this.host = System.getProperty("java.rmi.server.hostname");
    }

    public synchronized void addSeat(int numberOfSeats) throws RemoteException {
        if(numberOfSeats < 1)
            throw new IllegalArgumentException("Number has to be greater than zero.");

        for(int i = 0; i < numberOfSeats; i++){
            SeatImpl seat = new SeatImpl(null);
            ForkImpl fork = new ForkImpl(null);
            table.registerNewForkAndSeat(fork, seat, host);
            listSeats.add(seat);
            listForks.add(fork);
        }
    }

    public synchronized void removeSeat(int numberOfSeats) throws RemoteException {
        if(numberOfSeats < 1 || numberOfSeats > listSeats.size())
            throw new IllegalArgumentException("Wrong number of Seats for removing.");

        for(int i = 0; i < numberOfSeats; i++){
            Seat seat= listSeats.remove(0);
            Fork fork = listForks.remove(0);
            table.removeForkAndSeat(fork, seat, host);
        }
    }
}

