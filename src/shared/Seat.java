/**
 * 
 */
package shared;

/**
 * @author Dominik
 *
 * 29.05.2016
 */
public interface Seat {

	public boolean hasOwner();
	public boolean sitDown(Philosopher owner);
	public void standUp();
}
