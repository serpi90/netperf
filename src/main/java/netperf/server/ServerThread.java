package netperf.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import netperf.protocol.MeasurementMessage;
import netperf.protocol.Message;
import netperf.protocol.MessageVisitor;
import netperf.protocol.SpeedMeasurement;
import netperf.protocol.StartMessage;
import netperf.protocol.StopMessage;

/**
 * The thread to execute when an incoming connection is established.
 * 
 * On {@link StartMessage}: for each address provided in
 * {@link StartMessage#getAddresses()}, a speed measurement is established using
 * {@link SpeedMeasurementThread} which sends executes
 * {@link MeasureSpeedCommand#executeAsSource(SendBytesCommand)} that sends a
 * {@link MeasurementMessage} to the other side.
 * 
 * On {@link MeasurementMessage}:
 * {@link MeasureSpeedCommand#executeAsTarget(SendBytesCommand)} is invoked
 * 
 * On {@link StopMessage} {@link NetPerfServer#stop()} is invoked
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 *
 */
public class ServerThread extends Thread implements MessageVisitor {

	private Socket listeningSocket;
	private NetPerfServer server;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	/**
	 * @param accept
	 *            the incoming connection.
	 * @param server
	 *            the server which this thread belongs to.
	 */
	public ServerThread(Socket accept, NetPerfServer server) {
		listeningSocket = accept;
		try {
			inputStream = new ObjectInputStream(listeningSocket.getInputStream());
			outputStream = new ObjectOutputStream(listeningSocket.getOutputStream());
		} catch (IOException e) {
			server.getLog().error("Error creating streams", e);
		}
		this.server = server;
	}

	@Override
	public void acceptMeasurementMessage(MeasurementMessage dataMessage) {
		server.getLog()
				.info("Received: " + dataMessage + " from: " + listeningSocket.getRemoteSocketAddress().toString());
		try {
			new MeasureSpeedCommand(inputStream, outputStream, listeningSocket.getInputStream(),
					listeningSocket.getOutputStream(), server.getLog()).executeAsTarget(dataMessage.getCommand());
		} catch (IOException e) {
			server.getLog().error("Error executing MeasureSpeedCommand asTarget", e);
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
						server.getLog()
								.info("Connected to " + address.toString() + " from port: " + socket.getLocalPort());
						ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
						ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
						outputStream.writeObject(new MeasurementMessage(startMessage.getCommand()));
						SpeedMeasurement[] measurement = new MeasureSpeedCommand(inputStream, outputStream,
								socket.getInputStream(), socket.getOutputStream(), server.getLog())
										.executeAsSource(startMessage.getCommand());
						respond(measurement);
					} catch (IOException e) {
						server.getLog().error("Error executing MeasureSpeedCommand asSource", e);
						System.exit(-1);
					} catch (ClassNotFoundException e) {
						server.getLog().error("Error when type casting", e);
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
	 * Send the measured speeds to the one who sent the {@link StartMessage}
	 * 
	 * @param speeds
	 *            indexed by: {@link MeasureSpeedCommand#HIS_READ_SPEED},
	 *            {@link MeasureSpeedCommand#HIS_WRITE_SPEED},
	 *            {@link MeasureSpeedCommand#MY_READ_SPEED} and
	 *            {@link MeasureSpeedCommand#MY_WRITE_SPEED}
	 * @throws IOException
	 */
	public synchronized void respond(SpeedMeasurement[] speeds) throws IOException {
		server.getLog().info("Sending " + speeds.length + " speeds back");
		outputStream.writeInt(speeds.length);
		server.getLog().info("Sent header informing about " + speeds.length + " speeds");
		for (SpeedMeasurement speed : speeds) {
			outputStream.writeObject(speed);
		}
		server.getLog().info("Sent back " + speeds.length + " speeds");
	}

	@Override
	public void run() {
		try {
			Message message = (Message) inputStream.readObject();
			message.accept(this);
		} catch (IOException | ClassNotFoundException e) {
			server.getLog().error("Error accepting request", e);
			System.exit(-1);
		}
	}

	/**
	 * @return the server
	 */
	public NetPerfServer getServer() {
		return server;
	}
}
