package netperf.protocol;

/**
 * A message indicating the receiver to terminate it's execution
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class StopMessage implements Message {

	private static final long serialVersionUID = -7165792681011300962L;

	@Override
	public void accept(MessageVisitor visitor) {
		visitor.acceptStopMessage(this);
	}
}
