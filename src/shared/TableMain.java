/**
 * 
 */
package shared;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

/**
 * @author Dominik Ernsberger
 *
 * 29.05.2016
 */
public class TableMain {
	/**
	 * registers a new Table at the registry.
	 * @param master
	 * @param port
	 * @param debugging
	 * @throws RemoteException
	 * @throws UnknownHostException
     */
	public void registerTableToMaster(Master master, int port, String ip, boolean debugging) throws RemoteException, UnknownHostException{
		
		System.setProperty("java.rmi.server.hostname", ip);
		
		LocateRegistry.createRegistry(port);
		System.out.println("Registry start after error");
		TableImpl table = new TableImpl();
		table.setID(ip.hashCode());
		table.setShowOutput(debugging);
		master.registerTable(table);
		String objName = "table";
		Table tableStub = (Table) table;
		Registry reg = LocateRegistry.getRegistry(port);

				reg.rebind(objName, tableStub);
				System.out.println(objName + " bound to registry");


		System.out.println("Table " + table.getID() + " is running!!");
		
	}

}
