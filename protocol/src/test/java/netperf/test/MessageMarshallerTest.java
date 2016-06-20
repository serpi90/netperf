package netperf.test;

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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import netperf.protocol.MeasurementMessage;
import netperf.protocol.Message;
import netperf.protocol.MessageMarshaller;
import netperf.protocol.MessageMarshaller.MarshalException;
import netperf.protocol.SpeedMeasurement;
import netperf.protocol.StartMessage;
import netperf.protocol.StopMessage;
import netperf.protocol.TransferDuringFixedTime;
import netperf.protocol.TransferWithFixedSize;

@SuppressWarnings("javadoc")
public class MessageMarshallerTest {

	private MessageMarshaller marshaller;

	@Before
	public void setUp() {
		marshaller = new MessageMarshaller();
	}

	@Test
	public void testMeasurementMessageFixedSize() throws MarshalException {
		TransferWithFixedSize command = new TransferWithFixedSize(60);
		Message message = new MeasurementMessage(command);
		byte[] bytes = marshaller.marshallMessage(message);
		message = marshaller.unmarshallMessage(new ByteArrayInputStream(bytes));
		assert (message instanceof MeasurementMessage);
		MeasurementMessage measurementMessage = (MeasurementMessage) message;
		assert (measurementMessage.getCommand() instanceof TransferWithFixedSize);
		assertEquals(((TransferWithFixedSize) measurementMessage.getCommand()).getKilobytesToSend(),
				command.getKilobytesToSend());
	}

	@Test
	public void testMeasurementMessageFixedTime() throws MarshalException {
		TransferDuringFixedTime command = new TransferDuringFixedTime(60);
		Message message = new MeasurementMessage(command);
		byte[] bytes = marshaller.marshallMessage(message);
		message = marshaller.unmarshallMessage(new ByteArrayInputStream(bytes));
		assert (message instanceof MeasurementMessage);
		MeasurementMessage measurementMessage = (MeasurementMessage) message;
		assert (measurementMessage.getCommand() instanceof TransferDuringFixedTime);
		assertEquals(((TransferDuringFixedTime) measurementMessage.getCommand()).getMilliseconds(),
				command.getMilliseconds());
	}

	@Test
	public void testSpeed() {
		SpeedMeasurement original = new SpeedMeasurement(1, 2, 3);
		byte[] bytes = marshaller.marshallSpeed(original);
		SpeedMeasurement speed = marshaller.unmarshallSpeed(new ByteArrayInputStream(bytes));
		assertEquals(speed.getStart(), original.getStart());
		assertEquals(speed.getEnd(), original.getEnd());
		assertEquals(speed.getKilobytes(), original.getKilobytes());
	}

	@Test
	public void testSpeeds() {
		SpeedMeasurement[] speeds = new SpeedMeasurement[2];
		SpeedMeasurement speed0 = new SpeedMeasurement(1, 2, 3);
		SpeedMeasurement speed1 = new SpeedMeasurement(3, 2, 1);
		speeds[0] = speed0;
		speeds[1] = speed1;
		byte[] bytes = marshaller.marshallSpeeds(speeds);
		speeds = marshaller.unmarshallSpeeds(new ByteArrayInputStream(bytes));
		assertEquals(speeds.length, 2);
		assertEquals(speeds[0].getStart(), speed0.getStart());
		assertEquals(speeds[0].getEnd(), speed0.getEnd());
		assertEquals(speeds[0].getKilobytes(), speed0.getKilobytes());
		assertEquals(speeds[1].getStart(), speed1.getStart());
		assertEquals(speeds[1].getEnd(), speed1.getEnd());
		assertEquals(speeds[1].getKilobytes(), speed1.getKilobytes());
	}

	@Test
	public void testStartMessageFixedSize() throws MarshalException {
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(2);
		addresses.add(new InetSocketAddress("192.168.0.1", 12345));
		addresses.add(new InetSocketAddress("10.0.0.2", 2345));
		addresses.add(new InetSocketAddress("127.0.0.1", 345));
		TransferWithFixedSize command = new TransferWithFixedSize(60);
		Message message = new StartMessage(addresses, command);
		byte[] bytes = marshaller.marshallMessage(message);
		message = marshaller.unmarshallMessage(new ByteArrayInputStream(bytes));
		assert (message instanceof StartMessage);
		StartMessage startMessage = (StartMessage) message;

		assertEquals(startMessage.getAddresses().get(0), addresses.get(0));
		assertEquals(startMessage.getAddresses().get(1), addresses.get(1));
		assertEquals(startMessage.getAddresses().get(2), addresses.get(2));

		assert (startMessage.getCommand() instanceof TransferWithFixedSize);
		assertEquals(((TransferWithFixedSize) startMessage.getCommand()).getKilobytesToSend(),
				command.getKilobytesToSend());
	}

	@Test
	public void testStartMessageFixedTime() throws MarshalException {
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>(2);
		addresses.add(new InetSocketAddress("192.168.0.1", 12345));
		addresses.add(new InetSocketAddress("10.0.0.2", 2345));
		addresses.add(new InetSocketAddress("127.0.0.1", 345));
		TransferDuringFixedTime command = new TransferDuringFixedTime(60);
		Message message = new StartMessage(addresses, command);
		byte[] bytes = marshaller.marshallMessage(message);
		message = marshaller.unmarshallMessage(new ByteArrayInputStream(bytes));
		assert (message instanceof StartMessage);
		StartMessage startMessage = (StartMessage) message;

		assertEquals(startMessage.getAddresses().get(0), addresses.get(0));
		assertEquals(startMessage.getAddresses().get(1), addresses.get(1));
		assertEquals(startMessage.getAddresses().get(2), addresses.get(2));

		assert (startMessage.getCommand() instanceof TransferDuringFixedTime);
		assertEquals(((TransferDuringFixedTime) startMessage.getCommand()).getMilliseconds(),
				command.getMilliseconds());
	}

	@Test
	public void testStopMessage() throws MarshalException {
		Message message = new StopMessage();
		byte[] bytes = marshaller.marshallMessage(message);
		message = marshaller.unmarshallMessage(new ByteArrayInputStream(bytes));
		assert (message instanceof StopMessage);
	}

}
