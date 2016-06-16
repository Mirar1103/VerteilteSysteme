package shared;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Maximilian on 10.06.2016.
 */
public class MasterImpl extends UnicastRemoteObject implements Master, Runnable{
	private List<Table> tableList = new ArrayList<Table>();
	private Map<Table, Long> tableLastUpdate;
	private Map<Table, Integer> tableSeats;
	private Map<Table, Integer> tableSemaphores;
	private Map<Table, Table> tableNextTable;
	private List<String> philIds;
	private Map<String, Integer> philEaten;
	private Map<String, Integer> philHunger;
	private Map<String, Boolean> philBanned;
	private Map<String, Philosopher> philosophers;
	private Map<String, Long> philLastupdate;

	private final static long TIMEOUT = 200000;
    /**
	 * @throws RemoteException
	 */
	public MasterImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public void run(){
		while(true) {
			checkTables();
			checkPhils();
			//checkSeatsandForks
			//checkEaten
			
		}
	}

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
        }

    }

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

	private void checkPhils(){
		for (int i =0; i<philIds.size(); i++){
			if(System.currentTimeMillis()-philLastupdate.get(philIds.get(i))>TIMEOUT){
				try {
					updatePhilosopher(philosophers.get(philIds.get(i)));
				} catch (RemoteException e) {
					restartPhil(philIds.get(i));
				}
			}

		}

	}

	private void checkTables(){
		for (int i =0; i<tableList.size(); i++){
			if(System.currentTimeMillis()-tableLastUpdate.get(tableList.get(i))>TIMEOUT){
				try {
					updateTable(tableList.get(i));
				} catch (RemoteException e) {
					restartPhil(philIds.get(i));
				}
			}

		}

	}

	private void updateTable(Table table) throws RemoteException {
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

	private  void restartPhil(String philId){
		try {
			tableList.get(0).recreatePhilosopher(philHunger.get(philId), philId, philEaten.get(philId), philBanned.get(philId));
		} catch (RemoteException e) {
			restartTable(tableList.get(0));
			restartPhil(philId);
		}
	}

	private void restartTable(Table table) {
		//create table and restart, but where??
	}

}
