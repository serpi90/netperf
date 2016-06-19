package netperf.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

import netperf.server.NetPerfServer;

/**
 * System entry point
 * 
 * @author Julián Maestri <serpi90@gmail.com>
 */
public class NetPerfServerStarter {

	/**
	 * System entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		short port = 1990;
		if (args.length > 0) {
			try {
				port = Short.parseShort(args[0]);
			} catch (NumberFormatException e) {
			}
		}
		Log log = new SimpleLog("NetPerfServer");
		log.info("Listening at port: " + port);
		NetPerfServer server = new NetPerfServer(port);
		server.setLog(log);
		server.start();
	}

}
