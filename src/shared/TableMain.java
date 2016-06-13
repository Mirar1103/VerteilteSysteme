/**
 * 
 */
package shared;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public class TableMain {
	
	public void registerTableToMaster(Master master, int port) throws RemoteException, UnknownHostException{
		final InetAddress me = InetAddress.getLocalHost();
	    final String hostAddress = me.getHostAddress();
		System.setProperty("java.rmi.server.hostname", hostAddress);
		
		LocateRegistry.createRegistry(port);
		System.out.println("Registry start after error");
		TableImpl table = new TableImpl();
		table.setID(me.hashCode());
		master.registerTable(table);
		String objName = "table";
		Table tableStub = (Table) table;
		Registry reg = LocateRegistry.getRegistry(port);

				reg.rebind(objName, tableStub);
				System.out.println(objName + " bound to registry");


		System.out.println("Table " + table.getID() + " is running!!");
		
	}

}
