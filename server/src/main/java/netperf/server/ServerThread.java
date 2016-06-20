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
import java.net.InetSocketAddress;
import java.net.Socket;

import netperf.protocol.DisconnectMessage;
import netperf.protocol.MeasurementMessage;
import netperf.protocol.Message;
import netperf.protocol.MessageMarshaller;
import netperf.protocol.MessageVisitor;
import netperf.protocol.SpeedMeasurement;
import netperf.protocol.StartMessage;
import netperf.protocol.StopMessage;
import netperf.protocol.MessageMarshaller.MarshalException;

/**
 * The thread to execute when an incoming connection is established.
 * 
 * On {@link StartMessage}: for each address provided in
 * {@link StartMessage#getAddresses()}, a speed measurement is established using
 * {@link SpeedMeasurementThread} which sends executes
 * {@link MeasureSpeed#executeAsSource(SendBytesCommand)} that sends a
 * {@link MeasurementMessage} to the other side.
 * 
 * On {@link MeasurementMessage}:
 * {@link MeasureSpeed#executeAsTarget(SendBytesCommand)} is invoked
 * 
 * On {@link StopMessage} {@link NetPerfServer#stop()} is invoked
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 * 
 */
public class ServerThread extends Thread implements MessageVisitor {

	private Socket listeningSocket;
	private NetPerfServer server;
	private OutputStream outputStream;
	private InputStream inputStream;
	private MessageMarshaller marshaller;

	/**
	 * @param accept
	 *            the incoming connection.
	 * @param server
	 *            the server which this thread belongs to.
	 */
	public ServerThread(Socket accept, NetPerfServer server) {
		marshaller = new MessageMarshaller();
		listeningSocket = accept;
		try {
			inputStream = listeningSocket.getInputStream();
			outputStream = listeningSocket.getOutputStream();
		} catch (IOException e) {
			server.getLog().error("Error creating streams", e);
		}
		this.server = server;
	}

	@Override
	public void acceptMeasurementMessage(MeasurementMessage dataMessage) {
		server.getLog().info(
				"Received: " + dataMessage + " from: "
						+ listeningSocket.getRemoteSocketAddress().toString());
		try {
			new MeasureSpeed(inputStream, outputStream, server.getLog())
					.executeAsTarget(dataMessage.getCommand());
		} catch (IOException e) {
			server.getLog().error(
					"Error executing MeasureSpeedCommand asTarget", e);
			System.exit(-1);
		}
	}

	@Override
	public void acceptStartMessage(final StartMessage startMessage) {
		server.getLog().info("Received " + startMessage);
		for (final InetSocketAddress address : startMessage.getAddresses()) {
			server.getLog().info("Measuring against " + address.toString());
			new Thread() {
				@Override
				public void run() {
					try (Socket socket = new Socket()) {
						socket.connect(address);
						server.getLog().info(
								"Incomming connection from" + address
										+ " port: " + socket.getLocalPort());
						OutputStream outputStream = socket.getOutputStream();
						InputStream inputStream = socket.getInputStream();
						SpeedMeasurement[] measurement = new MeasureSpeed(
								inputStream, outputStream, server.getLog())
								.executeAsSource(startMessage.getCommand());
						outputStream.write(marshaller
								.marshallMessage(new DisconnectMessage()));
						socket.close();
						respond(measurement);
					} catch (MarshalException | IOException e) {
						server.getLog().error(
								"Error executing MeasureSpeed asSource", e);
						System.exit(-1);
					}
				}
			}.start();
		}
	}

	@Override
	public void acceptStopMessage(StopMessage stopMessage) {
		try {
			server.getLog().info("Stopping server");
			listeningSocket.close();
		} catch (IOException e) {
			server.getLog().error("Error closing listening socket", e);
			System.exit(-1);
		}
		server.stop();
	}

	/**
	 * @return the server
	 */
	public NetPerfServer getServer() {
		return server;
	}

	/**
	 * Send the measured speeds to the one who sent the {@link StartMessage}
	 * 
	 * @param speeds
	 *            indexed by: {@link MeasureSpeed#HIS_READ_SPEED},
	 *            {@link MeasureSpeed#HIS_WRITE_SPEED},
	 *            {@link MeasureSpeed#MY_READ_SPEED} and
	 *            {@link MeasureSpeed#MY_WRITE_SPEED}
	 * @throws IOException
	 */
	public synchronized void respond(SpeedMeasurement[] speeds)
			throws IOException {
		server.getLog().info("Sending " + speeds.length + " speeds back");
		outputStream.write(marshaller.marshallSpeeds(speeds));
		server.getLog().info("Sent back " + speeds.length + " speeds");
	}

	@Override
	public void run() {
		Message message;
		while (!listeningSocket.isClosed() && listeningSocket.isConnected()) {
			try {
				message = marshaller.unmarshallMessage(inputStream);
				message.accept(this);
			} catch (MarshalException e) {
				server.getLog().error("Error reading message", e);
			}
		}
	}

	@Override
	public void acceptDisconnectMessage(DisconnectMessage disconnectMessage) {
		try {
			server.getLog().info(
					"Closing connection from"
							+ listeningSocket.getRemoteSocketAddress());
			listeningSocket.close();
			server.getLog().info(
					"Closed connection from"
							+ listeningSocket.getRemoteSocketAddress());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
