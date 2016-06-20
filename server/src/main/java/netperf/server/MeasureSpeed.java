package netperf.server;

/*
 * #%L
 * Server
 * %%
 * Copyright (C) 2016 Julián Maestri
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import netperf.protocol.MeasurementMessage;
import netperf.protocol.MessageMarshaller;
import netperf.protocol.SpeedMeasurement;
import netperf.protocol.TransferCommand;
import netperf.protocol.MessageMarshaller.MarshalException;

import org.apache.commons.logging.Log;

/**
 * A command encapsulating the protocol to measure a the speed between hosts
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class MeasureSpeed {
	static final int HIS_READ_SPEED = 1;
	static final int HIS_WRITE_SPEED = 3;
	static final int MY_READ_SPEED = 2;
	static final int MY_WRITE_SPEED = 0;
	private Log log;
	private InputStream inputStream;
	private OutputStream outputStream;
	private MessageMarshaller marshaller;

	/**
	 * @param inputStream
	 *            the stream to send messages to
	 * @param outputStream
	 *            the stream to read messages from
	 * @param log
	 *            the logger to use
	 */
	public MeasureSpeed(InputStream inputStream, OutputStream outputStream,
			Log log) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.log = log;
		this.marshaller = new MessageMarshaller();
	}

	/**
	 * Execute as the side sends a {@link MeasurementMessage} initiating the
	 * measurement
	 * 
	 * The other side should execute {@link #executeAsTarget(TransferCommand) }
	 * 
	 * @param command
	 *            the command to execute for the measurement.
	 * @return the measured speeds.
	 * @throws MarshalException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public SpeedMeasurement[] executeAsSource(TransferCommand command)
			throws IOException, MarshalException {
		SpeedMeasurement[] speeds = new SpeedMeasurement[4];
		outputStream.write(marshaller.marshallMessage(new MeasurementMessage(
				command)));
		log.info("Source Sending " + command);
		speeds[MY_WRITE_SPEED] = command.executeSend(outputStream);
		log.info("Source Write Speed " + speeds[MY_WRITE_SPEED]);
		log.info("Source Awaiting Read Speed");
		speeds[HIS_READ_SPEED] = getResponse(inputStream);
		log.info("Source got Target Read Speed " + speeds[HIS_READ_SPEED]);
		log.info("Source Reading " + command);
		speeds[MY_READ_SPEED] = command.executeRead(inputStream);
		log.info("Source Read Speed " + speeds[MY_READ_SPEED]);
		log.info("Source Awaiting Write Speed");
		speeds[HIS_WRITE_SPEED] = getResponse(inputStream);
		log.info("Source got Target Write Speed " + speeds[HIS_READ_SPEED]);
		return speeds;
	}

	/**
	 * Execute as the side that receives a {@link MeasurementMessage}
	 * 
	 * The other side should execute {@link #executeAsSource(TransferCommand) }
	 * 
	 * @param command
	 * @param controlOutputStream
	 * @param controlInputStream
	 * @throws IOException
	 */
	public void executeAsTarget(TransferCommand command) throws IOException {
		SpeedMeasurement response;
		log.info("Target Reading " + command);
		response = command.executeRead(inputStream);
		log.info("Target Sending Read Speed");
		sendResponse(outputStream, response);
		log.info("Target Sent Read Speed " + response);
		log.info("Target Sending " + command);
		response = command.executeSend(outputStream);
		log.info("Target Sending Send Speed");
		sendResponse(outputStream, response);
		log.info("Target Sent Send Speed " + response);
		log.info("Target Done");
	}

	private SpeedMeasurement getResponse(InputStream inputStream) {
		log.debug("Awaiting Response");
		SpeedMeasurement readObject = marshaller.unmarshallSpeed(inputStream);
		log.debug("Got Response");
		return readObject;

	}

	private void sendResponse(OutputStream outputStream, SpeedMeasurement speed)
			throws IOException {
		log.debug("Sending Response");
		outputStream.write(marshaller.marshallSpeed(speed));
		log.debug("Sent Response");
	}

}
