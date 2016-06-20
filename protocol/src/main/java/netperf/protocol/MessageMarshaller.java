package netperf.protocol;

/*
 * #%L
 * Protocol
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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Julián Maestri <serpi90@gmail.com> Writes / Reads objects from the
 *         network.
 */
public class MessageMarshaller {

	public class MarshalException extends Exception {
		private static final long serialVersionUID = 2772923681006213964L;
	}

	private static final int LENGTH_HEADER_SIZE = Integer.SIZE / 8;

	private static final byte START_MESSAGE = (byte) 1;
	private static final byte MEASUREMENT_MESSAGE = (byte) 2;
	private static final byte STOP_MESSAGE = (byte) 3;
	private static final byte DISCONNECT_MESSAGE = (byte) 4;
	private static final byte FIXED_TIME_COMMAND = (byte) 1;

	private static final byte FIXED_SIZE_COMMAND = (byte) 2;

	/**
	 * @param bytes
	 * @return int read from bytes
	 */
	private static int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH_HEADER_SIZE);
		buffer.put(bytes);
		buffer.flip();// Switch from writing to reading
		return buffer.getInt();
	}

	/**
	 * @param inputStream
	 * @return Integer read from the inputStream
	 * @throws IOException
	 */
	private static int readMessageSize(InputStream inputStream)
			throws IOException {
		byte[] bytes = new byte[LENGTH_HEADER_SIZE];
		for (int read = 0; read < bytes.length; read += inputStream.read(bytes,
				read, bytes.length - read))
			;
		return bytesToInt(bytes);
	}

	/**
	 * Reads a raw chunk of data from the inputStream
	 * 
	 * @param inputStream
	 * @return byte representation of x
	 */
	private static byte[] readRawBytes(InputStream inputStream) {
		try {
			int size = readMessageSize(inputStream);
			byte[] raw = new byte[size];
			for (int read = 0; read < raw.length; read += inputStream.read(raw,
					read, raw.length - read))
				;
			return raw;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private byte[] marshallAddresses(List<InetSocketAddress> addresses) {
		int length = 0;
		for (InetSocketAddress address : addresses) {
			/*
			 * The address varies in ipv4/ipv6, so we put the address length
			 * before each address.
			 */
			length += LENGTH_HEADER_SIZE
					+ address.getAddress().getAddress().length + Short.SIZE / 8;
		}
		ByteBuffer buffer = ByteBuffer.allocate(length + LENGTH_HEADER_SIZE);
		buffer.putInt(addresses.size());
		for (InetSocketAddress address : addresses) {
			byte[] bytes = address.getAddress().getAddress();
			buffer.putInt(bytes.length);
			buffer.put(bytes);
			buffer.putShort((short) address.getPort());
		}
		return buffer.array();
	}

	private byte[] marshallMeasurementMessage(MeasurementMessage message)
			throws MarshalException {
		byte[] commandBytes = marshallTransferCommand(message.getCommand());
		int length = 1 + commandBytes.length;
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH_HEADER_SIZE + length);
		buffer.putInt(length);
		buffer.put(MEASUREMENT_MESSAGE);
		buffer.put(commandBytes);
		return buffer.array();
	}

	/**
	 * @param message
	 * @return bytes representing message
	 * @throws MarshalException
	 *             {@link Message} or {@link TransferCommand} unknown by
	 *             {@link MessageMarshaller}.
	 */
	public byte[] marshallMessage(Message message) throws MarshalException {
		if (message instanceof MeasurementMessage) {
			return marshallMeasurementMessage((MeasurementMessage) message);
		} else if (message instanceof StartMessage) {
			return marshallStartMessage((StartMessage) message);
		} else if (message instanceof StopMessage) {
			return marshallStopMessage((StopMessage) message);
		} else if (message instanceof DisconnectMessage) {
			return marshallDisconnectMessage((DisconnectMessage) message);
		} else {
			throw new MarshalException();
		}
	}

	/**
	 * @param speed
	 * @return a byte representation of the given speed
	 */
	public byte[] marshallSpeed(SpeedMeasurement speed) {
		SpeedMeasurement[] speeds = new SpeedMeasurement[1];
		speeds[0] = speed;
		return marshallSpeeds(speeds);
	}

	/**
	 * @param speeds
	 * @return a byte representation of the given speed array
	 */
	public byte[] marshallSpeeds(SpeedMeasurement[] speeds) {
		int length = (Long.SIZE / 8) * 3 * speeds.length;
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH_HEADER_SIZE + length);
		buffer.putInt(length);
		for (SpeedMeasurement speed : speeds) {
			buffer.putLong(speed.getStart());
			buffer.putLong(speed.getEnd());
			buffer.putLong(speed.getKilobytes());
		}
		return buffer.array();
	}

	private byte[] marshallStartMessage(StartMessage message)
			throws MarshalException {
		byte[] commandBytes = marshallTransferCommand(message.getCommand());
		byte[] addressBytes = marshallAddresses(message.getAddresses());
		int length = 1 + commandBytes.length + addressBytes.length;
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH_HEADER_SIZE + length);
		buffer.putInt(length);
		buffer.put(START_MESSAGE);
		buffer.put(commandBytes);
		buffer.put(addressBytes);
		return buffer.array();
	}

	private byte[] marshallDisconnectMessage(DisconnectMessage message) {
		int length = 1;
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH_HEADER_SIZE + length);
		buffer.putInt(1);
		buffer.put(DISCONNECT_MESSAGE);
		return buffer.array();
	}

	private byte[] marshallStopMessage(StopMessage message) {
		int length = 1;
		ByteBuffer buffer = ByteBuffer.allocate(LENGTH_HEADER_SIZE + length);
		buffer.putInt(1);
		buffer.put(STOP_MESSAGE);
		return buffer.array();
	}

	private byte[] marshallTransferCommand(TransferCommand command)
			throws MarshalException {
		// No length header is appended because this is currently embedded in
		// other messages.
		int length = 1 + Long.SIZE / 8;
		ByteBuffer buffer = ByteBuffer.allocate(length);
		if (command instanceof TransferDuringFixedTime) {
			buffer.put(FIXED_TIME_COMMAND);
			buffer.putLong(((TransferDuringFixedTime) command)
					.getMilliseconds());
		} else if (command instanceof TransferWithFixedSize) {
			buffer.put(FIXED_SIZE_COMMAND);
			buffer.putLong(((TransferWithFixedSize) command)
					.getKilobytesToSend());
		} else {
			throw new MarshalException();
		}
		return buffer.array();
	}

	private List<InetSocketAddress> unmarshallAddresses(ByteBuffer buffer)
			throws MarshalException {
		List<InetSocketAddress> addresses = new LinkedList<>();
		int total = buffer.getInt();
		for (int i = 0; i < total; i++) {
			int addressLength = buffer.getInt();
			byte[] rawAddress = new byte[addressLength];
			buffer.get(rawAddress);
			short port = buffer.getShort();
			try {
				InetAddress host = InetAddress.getByAddress(rawAddress);
				addresses.add(new InetSocketAddress(host, port));
			} catch (UnknownHostException e) {
				throw new MarshalException();
			}
		}
		return addresses;
	}

	private Message unmarshallMeasurementMessage(ByteBuffer buffer)
			throws MarshalException {
		TransferCommand command = unmarshallTransferCommand(buffer);
		return new MeasurementMessage(command);

	}

	private Message unmarshallMessage(byte[] bytes) throws MarshalException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		byte messageType = buffer.get();
		switch (messageType) {
		case START_MESSAGE:
			return unmarshallStartMessage(buffer);
		case MEASUREMENT_MESSAGE:
			return unmarshallMeasurementMessage(buffer);
		case STOP_MESSAGE:
			return new StopMessage();
		case DISCONNECT_MESSAGE:
			return new DisconnectMessage();
		default:
			throw new MarshalException();
		}
	}

	public Message unmarshallMessage(InputStream inputStream)
			throws MarshalException {
		return unmarshallMessage(readRawBytes(inputStream));
	}

	private SpeedMeasurement unmarshallSpeed(ByteBuffer buffer) {
		long start = buffer.getLong();
		long end = buffer.getLong();
		long kilobytes = buffer.getLong();
		return new SpeedMeasurement(start, end, kilobytes);
	}

	/**
	 * @param inputStream
	 * @return {@link SpeedMeasurement} read from the given inputStream
	 */
	public SpeedMeasurement unmarshallSpeed(InputStream inputStream) {
		return unmarshallSpeed(ByteBuffer.wrap(readRawBytes(inputStream)));
	}

	private SpeedMeasurement[] unmarshallSpeeds(ByteBuffer buffer) {
		int speedsToRead = (buffer.capacity()) / ((Long.SIZE / 8) * 3);
		SpeedMeasurement[] speeds = new SpeedMeasurement[speedsToRead];
		for (int i = 0; i < speedsToRead; i++) {
			speeds[i] = unmarshallSpeed(buffer);
		}
		return speeds;
	}

	/**
	 * @param inputStream
	 * @return {@link SpeedMeasurement} array from the given input stream
	 */
	public SpeedMeasurement[] unmarshallSpeeds(InputStream inputStream) {
		return unmarshallSpeeds(ByteBuffer.wrap(readRawBytes(inputStream)));
	}

	private Message unmarshallStartMessage(ByteBuffer buffer)
			throws MarshalException {
		TransferCommand command = unmarshallTransferCommand(buffer);
		List<InetSocketAddress> addresses = unmarshallAddresses(buffer);
		return new StartMessage(addresses, command);
	}

	private TransferCommand unmarshallTransferCommand(ByteBuffer buffer)
			throws MarshalException {
		byte commandType = buffer.get();
		long data = buffer.getLong();
		switch (commandType) {
		case FIXED_TIME_COMMAND:
			return new TransferDuringFixedTime(data);
		case FIXED_SIZE_COMMAND:
			return new TransferWithFixedSize(data);
		default:
			throw new MarshalException();
		}
	}
}
