package netperf.protocol;

import java.io.Serializable;

/**
 * A message that indicates a speed measurement will start against the receiver.
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class MeasurementMessage implements Message, Serializable {

	private static final long serialVersionUID = 2747854783019757188L;
	private TransferBytesCommand command;

	/**
	 * @param command
	 *            the command to be executed to measure the speed
	 */
	public MeasurementMessage(TransferBytesCommand command) {
		this.command = command;
	}

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.acceptMeasurementMessage(this);
	}

	/**
	 * @return the command to be executed to measure the speed
	 */
	public TransferBytesCommand getCommand() {
		return command;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Measurement Message to ");
		builder.append(command);
		return builder.toString();
	}
}
