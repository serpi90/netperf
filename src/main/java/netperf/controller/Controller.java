package netperf.controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.impl.SimpleLog;

import netperf.protocol.SpeedMeasurement;
import netperf.protocol.StartMessage;
import netperf.protocol.TransferRandomBytesFixedTimeCommand;

@SuppressWarnings("javadoc")
public class Controller {

	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket()) {
			SimpleLog log = new SimpleLog("Controller");
			InetSocketAddress address = new InetSocketAddress("localhost", 1990);
			List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
			addresses.add(new InetSocketAddress("localhost", 1991));

			log.info("Connecting");
			socket.connect(address);
			log.info("Connected");
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

			/*
			 * long amountToSend = 1024 * 1024 * 1; log.info(
			 * "Requesting transfer of " + amountToSend + " kb");
			 * oos.writeObject(new StartMessage(addresses, new
			 * TrasferRandomBytesFixedSizeCommand(amountToSend)));
			 * 
			 * log.info("Awaiting speed count"); int total = ois.readInt();
			 * log.info("Expecting " + total + " speeds"); while (total-- > 0) {
			 * SpeedMeasurement speed = (SpeedMeasurement) ois.readObject();
			 * log.info(speed); }
			 */

			long milliseconds = 1000 * 5;
			log.info("Requesting transfer during " + milliseconds + " ms");
			oos.writeObject(new StartMessage(addresses, new TransferRandomBytesFixedTimeCommand(milliseconds)));

			log.info("Awaiting speed count");
			int total = ois.readInt();
			log.info("Expecting " + total + " speeds");
			while (total-- > 0) {
				SpeedMeasurement speed = (SpeedMeasurement) ois.readObject();
				log.info(speed);
			}
			log.info("End");
		}
	}
}
