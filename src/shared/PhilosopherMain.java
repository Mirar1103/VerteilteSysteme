/**
 * 
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Dominik
 *
 * 27.05.2016
 */
public class PhilosopherMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws RemoteException{
		
		PhilosopherImpl phil = new PhilosopherImpl();
		String objName = "philosopher";
		
		Registry reg = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
		boolean bound = false;
		for (int i = 0; ! bound && i < 2; i++)
		{
			try
			{
				reg.rebind(objName, phil);
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
		System.out.println("Philosopher is running!!");

	}

}
