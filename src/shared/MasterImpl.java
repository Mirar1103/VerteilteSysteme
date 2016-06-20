package shared;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Maximilian on 10.06.2016.
 */
public class MasterImpl extends UnicastRemoteObject implements Master, Runnable{
	private List<Table> tableList = new ArrayList<Table>();
	private Map<Table, Long> tableLastUpdate = new HashMap<>();
	private Map<Table, Integer> tableSeats = new HashMap<>();
	private Map<Table, Integer> tableSemaphores = new HashMap<>();
	private Map<Table, Table> tableNextTable = new HashMap<>();
	private List<String> philIds  = new ArrayList<>();
	private Map<String, Integer> philEaten = new HashMap<>();
	private Map<String, Integer> philHunger = new HashMap<>();
	private Map<String, Boolean> philBanned = new HashMap<>();
	private Map<String, Philosopher> philosophers = new HashMap<>();
	private Map<String, Long> philLastupdate = new HashMap<>();
	private SeatHelper seatHelper = new SeatHelper(null);
	private int lastTestedTable;

	private final static long TIMEOUT = 2000;
	private final static int MAX_EAT_MORE = 10;
	/**
	 * creates anew Master
	 * @throws RemoteException
	 */
	public MasterImpl() throws RemoteException {
		super();
	}

	/**
	 * starts the Mater.
	 */
	public void run(){
		while(true) {
			checkTables();
			checkPhils();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
    public void registerTable(Table table) throws RemoteException {
        if(tableList.size()>0){
	        Table currentLastTable = tableList.get(tableList.size()-1);
	        currentLastTable.setNextTable( table);
	        table.setNextTable( tableList.get(0));
	        tableList.add(table);
			updateTable(table);
        }
        else
        {
            tableList.add(table);
			updateTable(table);
        }

    }

/*
	@Override
	public void registerSeatHelper(SeatHelper seatHelper) throws RemoteException {
		this.seatHelper = seatHelper;
	}*/

	@Override
	public void updatePhilosopher(Philosopher phil)throws RemoteException{
		if(philIds.contains(phil.getID())){
			philEaten.replace(phil.getID(), phil.getTotalEatenRounds());
			philHunger.replace(phil.getID(), phil.getHunger());
			philBanned.replace(phil.getID(), phil.getBanned());
			philosophers.replace(phil.getID(), phil);
			philLastupdate.replace(phil.getID(), System.currentTimeMillis());
		} else {
			philIds.add(phil.getID());
			philEaten.put(phil.getID(), phil.getTotalEatenRounds());
			philHunger.put(phil.getID(), phil.getHunger());
			philBanned.put(phil.getID(), phil.getBanned());
			philosophers.put(phil.getID(), phil);
			philLastupdate.put(phil.getID(), System.currentTimeMillis());
		}
	}

	/**
	 * goes through the list of tracked philosophers and checks if they are upto date and/or reachable.
	 */
	private void checkPhils(){
		for (int i =0; i<philIds.size(); i++){
			while(philLastupdate.get(philIds.get(i))==null){
				//System.out.println("waiting for first update on Philosopher#"+philIds.get(i));
			}
			if(System.currentTimeMillis()-philLastupdate.get(philIds.get(i))>TIMEOUT){
				try {
					System.out.println("CHEEEEECKKK");
					updatePhilosopher(philosophers.get(philIds.get(i)));
				} catch (RemoteException e) {
					System.out.println("EXCEPTION RECREATE");
					restartPhil(philIds.get(i));
				}
			}
			for (int checked =0; checked<philEaten.size(); checked++){
				if(
						(philEaten.get(philIds.get(i))-MAX_EAT_MORE)>philEaten.get(philIds.get(checked))
						){
					try {
						philosophers.get(philIds.get(i)).ban();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	/**
	 * goes through the List of tables and checks if their data is uptodate and7or if they are still reachable.
	 */
	private void checkTables(){
		for (int i =0; i<tableList.size(); i++){
			lastTestedTable=i;
			while(tableLastUpdate.get(tableList.get(i))==null){
				System.out.println("waiting for first update on table#"+i);
			}

			if(System.currentTimeMillis()-tableLastUpdate.get(tableList.get(i))>TIMEOUT){
				try {
					updateTable(tableList.get(i));
					seatHelper.setTable(tableList.get(i));
				} catch (RemoteException e) {
					restartTable(tableList.get(i));
				}
			}

		}

	}

	@Override
	public void updateTable(Table table) throws RemoteException {
		if(tableLastUpdate.containsKey(table)) {
			tableSeats.replace(table, table.getNumberOfSeats());
			tableSemaphores.replace(table, table.getNumberOfSemaphores());
			tableNextTable.replace(table, table.getNextTable());
			tableLastUpdate.replace(table, System.currentTimeMillis());
		}else{
			tableSeats.put(table, table.getNumberOfSeats());
			tableSemaphores.put(table, table.getNumberOfSemaphores());
			tableNextTable.put(table, table.getNextTable());
			tableLastUpdate.put(table, System.currentTimeMillis());
		}
	}

	/**
	 * recreates a lost philosopher.
	 * @param philId
     */
	private  void restartPhil(String philId){
		try {
			tableList.get(0).recreatePhilosopher(philHunger.get(philId), philId, philEaten.get(philId), philBanned.get(philId));
		} catch (RemoteException e) {
			restartTable(tableList.get(0));
			restartPhil(philId);
		}
	}

	/**
	 * adds the seats of a lost table to another table.
	 * @param table
     */
	private void restartTable(Table table) {
		if(table==null){
			System.out.println("given table was null");
		}
		if(tableList.size()>1){
			try {
				seatHelper.addSeat(tableSeats.get(tableList.get(lastTestedTable)));
				removeTable(table);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("all tables are dead, restart System;");
		}
	}

	@Override
	public void removePhilosopher(Philosopher phil) throws RemoteException {
		philIds.remove(phil.getID());
		philHunger.remove(phil.getID());
		philBanned.remove(phil.getID());
		philEaten.remove(phil.getID());
		philosophers.remove(phil.getID());
		philLastupdate.remove(phil.getID());
	}

	@Override
	public void removeTable(Table table) throws RemoteException {
		tableLastUpdate.remove(table);
		tableNextTable.remove(table);
		tableSemaphores.remove(table);
		tableSeats.remove(table);
		tableList.remove(table);
	}
	
	public void printResult() throws RemoteException{
		int totalEaten = 0;
		System.out.println("---------FINISH----------");
		Iterator it = philEaten.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        totalEaten += (int) pair.getValue();
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		System.out.println("Total: " + totalEaten);
	}

}
