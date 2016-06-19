package netperf.protocol;

/**
 * A common interface for visitor pattern implementation
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public interface MessageVisitor {

	/**
	 * See visitor pattern
	 * 
	 * @param dataMessage
	 */
	void acceptMeasurementMessage(MeasurementMessage dataMessage);

	/**
	 * See visitor pattern
	 * 
	 * @param startMessage
	 */
	void acceptStartMessage(StartMessage startMessage);

	/**
	 * See visitor pattern
	 * 
	 * @param stopMessage
	 */
	void acceptStopMessage(StopMessage stopMessage);

}
