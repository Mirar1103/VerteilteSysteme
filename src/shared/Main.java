/**
 * 
 */
package shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;

/**
 * @author Dominik
 *
 * 29.05.2016
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws IOException, NotBoundException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		Table table = null;
		PhilosopherHelper phil = null;
		System.out.println("Welcome to the shared dining philosophers! Possible commands are:");
		System.out.println("create Table, create/remove Philosopher, create/remove Seat, quit");
		
		while(!(input = br.readLine()).equals("quit")){
			//create Table
			if(input.equals("create Table")){
				String port;
				System.out.println("Which Port?");
				port = br.readLine();
				TableMain.main(port);
				table = (Table) LocateRegistry.getRegistry(Integer.parseInt(port)).lookup("table");
			}
			//create Philosopher
			else if(input.equals("create Philosopher")){
				if(table == null)
					table = searchTable(br);
				if(phil == null)
					phil = new PhilosopherHelper(table);
				int numberOfPhil;
				System.out.println("Number of Philosophers to add: ");
				numberOfPhil = Integer.parseInt(br.readLine());
				phil.addPhilosopher(numberOfPhil);
			}
			//remove Philosopher
			else if(input.equals("remove Philosopher")){
				if(phil == null)
					System.out.println("No Philosophers to remove on this Server.");
				else{
					int numberOfPhil;
					System.out.println("Number of Philosopher to remove: ");
					numberOfPhil = Integer.parseInt(br.readLine());
					phil.removePhilosopher(numberOfPhil);
				}
			}
		}
		br.close();

	}

	/**
	 * @param br
	 * @return
	 * @throws IOException 
	 * @throws NotBoundException 
	 */
	private static Table searchTable(BufferedReader br) throws IOException, NotBoundException {
		System.out.println("No Table found on this Server, please enter an adress for the Table.");
		System.out.println("Host Address: ");
		String host = br.readLine();
		System.out.println("Port: ");
		String port = br.readLine();
		return (Table) Naming.lookup("//"+host+":"+port+"/table");
	}

}
