package shared;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by Maximilian on 10.06.2016.
 */
public class MasterMain {

    /**
     * creates a new Master at the given Registry.
     * @param args
     * @throws RemoteException
     */
    public static void main(String... args) throws RemoteException {
		System.setProperty("java.rmi.server.hostname", args[1]);
        LocateRegistry.createRegistry(Integer.parseInt(args[0]));
        MasterImpl master = new MasterImpl();
        String objName = "master";
        Master masterStub = (Master) master;
        Registry reg = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
        reg.rebind(objName, masterStub);
        System.out.println(objName + " bound to registry");
        new Thread(master).start();
        System.out.println("Master is running!!");

    }
}
