/**
 * 
 */
package shared;

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

	/**
	 * @param args
	 * @throws RemoteException  
	 */
	public static void main(String... args) throws RemoteException {
		LocateRegistry.createRegistry(Integer.parseInt(args[0]));
		System.out.println("Registry start after error");
		TableImpl table = new TableImpl();
		String objName = "table";
		Table tableStub = (Table) table;
		Registry reg = LocateRegistry.getRegistry(Integer.parseInt(args[0]));

				reg.rebind(objName, tableStub);
				System.out.println(objName + " bound to registry");


		System.out.println("Table is running!!");

	}

}
