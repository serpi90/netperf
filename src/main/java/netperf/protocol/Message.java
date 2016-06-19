package netperf.protocol;

import java.io.Serializable;

/**
 * A common interface for visitor pattern implementation
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public interface Message extends Serializable {
	/**
	 * @param visitor
	 *            see visitor pattern
	 */
	public void accept(MessageVisitor visitor);
}
