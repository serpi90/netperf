package netperf.protocol;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * A message indicating the receiver to start measuring the transfer speed
 * against the provided addresses
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class StartMessage implements Message {
	private static final long serialVersionUID = 5335590117711411806L;
	private List<InetSocketAddress> addresses;
	private TransferBytesCommand command;

	/**
	 * @param addresses
	 *            the addresses to measure against
	 * @param command
	 *            the command to use during measurement
	 */
	public StartMessage(List<InetSocketAddress> addresses, TransferBytesCommand command) {
		this.addresses = addresses;
		this.command = command;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.acceptStartMessage(this);
	}

	/**
	 * @return the addresses to measure speed against
	 */
	public List<InetSocketAddress> getAddresses() {
		return addresses;
	}

	/**
	 * @return the command to execute when measuring speed
	 */
	public TransferBytesCommand getCommand() {
		return command;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Start Message to measure against ");
		for (InetSocketAddress address : addresses) {
			builder.append(address.toString());
			builder.append(" ");
		}
		builder.append("requesting to ");
		builder.append(command);
		return builder.toString();
	}
}
