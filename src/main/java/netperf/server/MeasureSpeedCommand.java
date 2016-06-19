package netperf.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;

import netperf.protocol.MeasurementMessage;
import netperf.protocol.SpeedMeasurement;
import netperf.protocol.TransferBytesCommand;

/**
 * A command encapsulating the protocol to measure a the speed between hosts
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class MeasureSpeedCommand {
	static final int HIS_READ_SPEED = 1;
	static final int HIS_WRITE_SPEED = 3;
	static final int MY_READ_SPEED = 2;
	static final int MY_WRITE_SPEED = 0;
	private Log log;
	private ObjectInputStream controlInputStream;
	private ObjectOutputStream controlOutputStream;
	private InputStream dataInputStream;
	private OutputStream dataOutputStream;

	/**
	 * @param controlInputStream
	 *            the stream to send messages to
	 * @param controlOutputStream
	 *            the stream to read messages from
	 * @param dataInputStream
	 *            the stream to send data to
	 * @param dataOutputStream
	 *            the stream to read data from
	 * @param log
	 *            the logger to use
	 */
	public MeasureSpeedCommand(ObjectInputStream controlInputStream, ObjectOutputStream controlOutputStream,
			InputStream dataInputStream, OutputStream dataOutputStream, Log log) {
		this.controlInputStream = controlInputStream;
		this.controlOutputStream = controlOutputStream;
		this.dataInputStream = dataInputStream;
		this.dataOutputStream = dataOutputStream;
		this.log = log;
	}

	/**
	 * Execute as the side sends a {@link MeasurementMessage} initiating the
	 * measurement
	 * 
	 * The other side should execute
	 * {@link #executeAsTarget(TransferBytesCommand) }
	 * 
	 * @param command
	 *            the command to execute for the measurement.
	 * @return the measured speeds.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public SpeedMeasurement[] executeAsSource(TransferBytesCommand command) throws IOException, ClassNotFoundException {
		SpeedMeasurement[] speeds = new SpeedMeasurement[4];
		controlOutputStream.writeObject(new MeasurementMessage(command));
		log.info("Source Sending");
		speeds[MY_WRITE_SPEED] = command.executeSend(dataOutputStream);
		log.info("Source Awaiting Response");
		speeds[HIS_READ_SPEED] = getResponse(controlInputStream);
		log.info("Source Reading");
		speeds[MY_READ_SPEED] = command.executeRead(dataInputStream);
		log.info("Source Awaiting Response");
		speeds[HIS_WRITE_SPEED] = getResponse(controlInputStream);
		log.info("Source Got Response");
		return speeds;
	}

	/**
	 * Execute as the side that receives a {@link MeasurementMessage}
	 *
	 * The other side should execute
	 * {@link #executeAsSource(TransferBytesCommand) }
	 * 
	 * @param command
	 * @param controlOutputStream
	 * @param controlInputStream
	 * @throws IOException
	 */
	public void executeAsTarget(TransferBytesCommand command) throws IOException {
		SpeedMeasurement response;
		byte[] objectOutputStreamGarbage = new byte[11];
		dataInputStream.read(objectOutputStreamGarbage, 0, objectOutputStreamGarbage.length);
		log.info("Target Reading");
		response = command.executeRead(dataInputStream);
		log.info("Target Sending Response");
		sendResponse(controlOutputStream, response);
		log.info("Target Sending");
		response = command.executeSend(dataOutputStream);
		log.info("Target Sending Response");
		sendResponse(controlOutputStream, response);
		log.info("Target Sent Response");
	}

	private SpeedMeasurement getResponse(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		log.debug("Awaiting Response");
		SpeedMeasurement readObject = (SpeedMeasurement) inputStream.readObject();
		log.debug("Got Response");
		return readObject;

	}

	private void sendResponse(ObjectOutputStream outputStream, SpeedMeasurement speed) throws IOException {
		log.debug("Sending Response");
		outputStream.writeObject(speed);
		log.debug("Sent Response");
	}

}
