/**
 * 
 */
package shared;


import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Dominik Ernsberger
 *
 * 27.05.2016
 */
public class PhilosopherImpl implements Runnable, Philosopher, Serializable{
	private static final long serialVersionUID = -1794431714881772273L;
	static AtomicInteger nextId = new AtomicInteger();
	private Table table;
	private int totalEaten;
	private int hunger;
	private String philosopherID;
	private boolean banned;
	private Seat currentSeat;
	private boolean showOutput;
	private boolean isntStopped = true;
	private boolean ableForRemoving = true;
	private Thread currentThread;

	private final static int DEFAULT_HUNGER = 80;
	private final static int THINK_TIME = 3000;
	private final static int BANNED_TIME = 10000;
	private final static int MAX_WAIT_FORK = 25;
	private final static int WAIT_TIME_FORK = 1;
	private final static int EAT_TIME = 2000;
	private final static int SLEEPING_TIME = 10000;
	private final static int MAX_EATEN =3;
	
	private final String MONITOR = new String();
	
	
	/**
	 * @throws RemoteException
	 */
	protected PhilosopherImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Initialize a single Philosopher.
	 * @param hunger the value of the hunger percentage
	 * @throws RemoteException 
	 */
	public PhilosopherImpl(int hunger) throws RemoteException{
		super();
		InetAddress me;
		try {
			me = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			me = null;
		}
		this.philosopherID = String.valueOf(nextId.incrementAndGet())+ "#"+me.getHostAddress();
		if(hunger == -1)
			this.hunger = DEFAULT_HUNGER;
		else
			this.hunger = hunger;
		banned = false;
		currentSeat = null;
		totalEaten = 0;
		
	}
	
	public PhilosopherImpl(Table table, int hunger, String philosopherID, int totalEaten, boolean banned) throws RemoteException{
		super();
		this.table = table;
		this.totalEaten = totalEaten;
		this.hunger = hunger;
		this.philosopherID = philosopherID;
		this.banned = banned;
		showOutput = true;
		currentSeat = null;
	}

	/**
	 * This is the main function for the philosopher.
	 * 
	 * Includes the logic behind the think, eat and sleep rythm.
	 */
	@Override
	public void run(){
		try{
			while(!Thread.currentThread().isInterrupted()&&isntStopped){
				while(table == null){
					if(showOutput) {
						System.out.println("Philosopher " + philosopherID + " waiting for table.");
					}
						MONITOR.wait();
				}
				
				System.out.println("INSIDE - " + isntStopped);
				think();
				int random  = Math.abs(new Random().nextInt()% 100);
				if(random < hunger){
					ableForRemoving = false;
					if(!eat()){
						if(showOutput) 
							System.out.println("Philosopher " + philosopherID + " gets hungry and will try to eat.");
						table.movePhilosopher(this);
					}else{
						ableForRemoving = true;
						if((totalEaten % MAX_EATEN) == 0)
						goToBed();
					}
				}
				else
					think();
			}
		} catch(InterruptedException e){
			Thread.currentThread().interrupt();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("END - " + isntStopped);
	}
	
	/**
	 * Philosopher thinks.
	 * 
	 * The Thread will sleep for a specific time interval.
	 */
	private void think(){
		try {
			if(showOutput) {
				System.out.println("Philosopher " + philosopherID + " is thinking.");
			}
			Thread.sleep(THINK_TIME);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Philosopher sleeps.
	 * 
	 * The Thread will sleep for a specific time interval.
	 */
	private void goToBed(){
		try {
			if(showOutput) {
				System.out.println("Philosopher " + philosopherID + " is going to bed.");
			}
			Thread.sleep(SLEEPING_TIME);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Tries to eat.
	 * 
	 * Try to get a seat and both forks. If the philosopher is banned from the master,
	 * he will wait a specified time and will try it after this again.
	 * If the philosopher was able to eat he will drop both forks and leave the seat. 
	 */
	private boolean eat() throws RemoteException {
		//first check if the philosopher is banned
		if(banned){
			try {
				if(showOutput) {
					System.out.println("Philosopher " + philosopherID + "is banned from eating.");
				}
				Thread.sleep(BANNED_TIME);
				banned = false;
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
		}
		//waiting for a seat to sit down
		if(showOutput) {
			System.out.println("Philosopher " + philosopherID + "is waiting for a seat.");
		}
		currentSeat = table.takeSeat(this);
		
		if (currentSeat == null)
			return false;

		if(showOutput) {
			System.out.println("Philosopher " + philosopherID + "sits down on seat " + table.getSeatPosition(currentSeat) + "on Table " + table.getID());
		}
		
		//pick up both forks
		int leftFork = table.getSeatPosition(currentSeat);
		int rightFork = leftFork+1;//)%table.getNumberOfSeats();
		
		if(getBothForks(leftFork, rightFork)){
			try {
				totalEaten++;
				if(showOutput) {
					System.out.println("Philosopher " + philosopherID + "is eating.");
				}
				Thread.sleep(EAT_TIME);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			//drop both forks
			table.dropFork(leftFork);
			table.dropFork(rightFork);
			if(showOutput) {
				System.out.println("Philosopher " + philosopherID + "dropped both forks.");
			}
		}
		else{
			if(showOutput) {
				System.out.println("Philosopher " + philosopherID + "is giving up. No forks available.");
			}
		}
		
		//standing up and leaving the dining room
		table.standUp(currentSeat);
		if(showOutput) {
			System.out.println("Philosopher " + philosopherID + " is leaving.");
		}
		return true;
	}
	
	/**
	 * Tries to get both forks.
	 * 
	 * Loops for a specified time over the left and right fork and checks the availability.
	 * @param leftFork
	 * @param rightFork
	 * @return true if the philosopher get both forks
	 */
	private boolean getBothForks(int leftFork, int rightFork) throws RemoteException {
		int waitCounter = 0;
		
			//randomly take one of both forks
			int firstFork = Math.abs(new Random().nextInt()%2);
			boolean gotFork = false;
			
			if(firstFork == 0){
				while (waitCounter < MAX_WAIT_FORK){
					if(getSingleFork(leftFork, "left", gotFork) || gotFork){
						gotFork= true;
						if(getSingleFork(rightFork, "right", !gotFork))
							return true;
						else waitCounter++;
					}
					else waitCounter++;
				}
				if(gotFork){
					//Drop fork to prevent a deadlock
					table.dropFork(leftFork);
					if(showOutput) {
						System.out.println("Philosopher " + philosopherID + "dropped left fork.");
					}
				}
			} 
			else {
				while (waitCounter < MAX_WAIT_FORK){
					if(getSingleFork(rightFork, "right", gotFork) || gotFork){
						gotFork= true;
						if(getSingleFork(leftFork, "left", !gotFork))
							return true;
						else waitCounter++;
					}
					else waitCounter++;
				}
				if(gotFork){
					//Drop fork to prevent a deadlock
					table.dropFork(rightFork);
					if(showOutput) {
						System.out.println("Philosopher " + philosopherID + "dropped right fork.");
					}
				}
			}
		return false;
	}
	
	/**
	 * Tries to get a single Fork.
	 * @param fork
	 * @param forkPosition
	 * @param gotFork if true, wait and try again later
	 * @return true if the philosopher got the fork
	 */
	private boolean getSingleFork(int fork, String forkPosition, boolean gotFork) throws RemoteException {
		
		if(table.pickUpFork(fork, this)){
			if(showOutput) {
				System.out.println("Philosopher " + philosopherID + "picked up " + forkPosition + " fork.");
			}
			return true;
		}
		
		//no fork, wait and try again later
		try {
			if(!gotFork)
				if(showOutput) {
					System.out.println("Philosopher " + philosopherID + " is waiting for " + forkPosition + " fork.");
				}
			Thread.sleep(WAIT_TIME_FORK);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		return false;
		
	}
	
	/**
	 * Returns the total eaten rounds.
	 * @return total eaten rounds.
	 */
	public int getTotalEatenRounds()throws RemoteException{
		return totalEaten;
	}
	
	/**
	 * Set the flag to ban this philosopher for the next meal.
	 * @param banned true, if the philosopher has to be banned
	 */
	public void setBanned(boolean banned)throws RemoteException{
		this.banned = banned;
	}
	
	public boolean getBanned()throws RemoteException{
		return banned;
	}

	/* (non-Javadoc)
	 * @see shared.Philosopher#setTable(shared.Table)
	 */
	@Override
	public void setTable(Table table) throws RemoteException {
		this.table = table;
		synchronized (MONITOR) {
			MONITOR.notify();
		}
	}
	
	public String getID()throws RemoteException{
		return this.philosopherID;
	}
	
	public Thread getThread()throws RemoteException{
		return currentThread;
	}
	
	public void kill()throws RemoteException, InterruptedException{
		throw new InterruptedException("Kill this Philosopher");
	}
	
	public int getHunger()throws RemoteException{
		return hunger;
	}
	public void setShowOutput(boolean isWanted) throws RemoteException{
		showOutput = isWanted;
	}
	public void softKill() throws RemoteException{
		isntStopped = false;
		System.out.println("SET THE FLAG!!!" + isntStopped + " # " +getID());
	}
	
	public boolean isAbleForRemoving() throws RemoteException{
		return ableForRemoving;
	}
	
	public void setNewThread(Thread newThread){
		this.currentThread = newThread;
	}
}
