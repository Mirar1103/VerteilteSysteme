/**
 * 
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
		TableImpl tabel = new TableImpl(0);
		String objName = "table";
		
		Registry reg = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
		boolean bound = false;
		for (int i = 0; ! bound && i < 2; i++)
		{
			try
			{
				reg.rebind(objName, tabel);
				bound = true;
				System.out.println(objName + "bound to registry");
			}
			catch (RemoteException e)
			{
				System.out.println("ERROR rebind");
				reg = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
				System.out.println("Registry start after error");
			}
		}
		System.out.println("Table is running!!");

	}

}
