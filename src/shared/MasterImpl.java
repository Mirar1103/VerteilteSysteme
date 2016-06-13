package shared;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maximilian on 10.06.2016.
 */
public class MasterImpl extends UnicastRemoteObject implements Master{
	private List<Table> tableList = new ArrayList<Table>();
    /**
	 * @throws RemoteException
	 */
	public MasterImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	

    public void registerTable(Table table) throws RemoteException {
        if(tableList.size()>0){
	        Table currentLastTable = tableList.get(tableList.size()-1);
	        currentLastTable.setNextTable( table);
	        table.setNextTable( tableList.get(0));
	        tableList.add(table);
        }
        else
        {
            tableList.add(table);
        }

    }

}
