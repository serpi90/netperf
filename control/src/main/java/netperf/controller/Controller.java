package netperf.controller;

/*
 * #%L
 * Controller
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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.impl.SimpleLog;

import netperf.protocol.DisconnectMessage;
import netperf.protocol.MessageMarshaller;
import netperf.protocol.SpeedMeasurement;
import netperf.protocol.StartMessage;
import netperf.protocol.TransferCommand;
import netperf.protocol.TransferDuringFixedTime;
import netperf.protocol.TransferWithFixedSize;

@SuppressWarnings("javadoc")
public class Controller {

	private static MessageMarshaller marshaller = new MessageMarshaller();

	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket()) {
			SimpleLog log = new SimpleLog("Controller");

			InetSocketAddress src = new InetSocketAddress("localhost", 1990);
			InetSocketAddress tgt = new InetSocketAddress("localhost", 1991);
			List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
			addresses.add(tgt);

			log.info("Connecting");
			socket.connect(src);
			log.info("Connected");
			OutputStream os = socket.getOutputStream();
			os.flush();
			InputStream is = socket.getInputStream();

			boolean size = true;
			boolean time = true;

			if (size) {
				log.info("++++ Size ++++");
				long kilobytes = 1024 * 1024 * 1;
				log.info("Requesting transfer of " + kilobytes + " kb");
				TransferCommand command = new TransferWithFixedSize(kilobytes);
				os.write(marshaller.marshallMessage(new StartMessage(addresses, command)));
				log.info("Awaiting speed count");
				SpeedMeasurement[] speeds = marshaller.unmarshallSpeeds(is);
				log.info("Got " + speeds.length + " speeds");
				for (SpeedMeasurement speed : speeds) {
					log.info(speed);
				}
			}
			if (time) {
				log.info("++++ Time ++++");
				long milliseconds = 1000 * 1;
				log.info("Requesting transfer during " + milliseconds + " ms");
				TransferCommand command = new TransferDuringFixedTime(milliseconds);
				os.write(marshaller.marshallMessage(new StartMessage(addresses, command)));

				log.info("Awaiting speed count");
				SpeedMeasurement[] speeds = marshaller.unmarshallSpeeds(is);
				log.info("Got " + speeds.length + " speeds");
				for (SpeedMeasurement speed : speeds) {
					log.info(speed);
				}
			}
			os.write(marshaller.marshallMessage(new DisconnectMessage()));
			socket.close();
			log.info("End");
		}
	}
}
