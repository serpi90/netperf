package netperf.controller;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.impl.SimpleLog;

import netperf.protocol.DisconnectMessage;
import netperf.protocol.MessageMarshaller;
import netperf.protocol.StopMessage;

@SuppressWarnings("javadoc")
public class Stopper {

	public static void main(String[] args) throws Exception {
		SimpleLog log = new SimpleLog("Stopper");
		List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
		addresses.add(new InetSocketAddress("localhost", 1990));
		addresses.add(new InetSocketAddress("localhost", 1991));
		MessageMarshaller marshaller = new MessageMarshaller();

		for (InetSocketAddress address : addresses) {
			try (Socket socket = new Socket()) {
				socket.connect(address);
				socket.getOutputStream().write(marshaller.marshallMessage(new StopMessage()));
				socket.getOutputStream().write(marshaller.marshallMessage(new DisconnectMessage()));
				socket.close();
			}
		}
		log.info("End");
	}
}
