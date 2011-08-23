package net.ion.websocket.notifier.ipos.sample;

import java.util.LinkedList;
import java.util.ListIterator;

import javapns.back.FeedbackServiceManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;

public class Feedback {
	// APNs Server Host & port
	private static final String HOST = "feedback.push.apple.com";
	private static final int PORT = 2196;

	private static String certificate = "/absolute/path/to/certificate";
	private static String passwd = "certificatePassword";

	public static void main(String[] args) throws Exception {

		try {
			// Get FeedbackServiceManager Instance
			FeedbackServiceManager feedbackManager = FeedbackServiceManager.getInstance();

			// Initialize connection
			LinkedList<Device> devices = feedbackManager.getDevices(HOST, PORT, certificate, passwd, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
			System.out.println("Connection initialized...");

			System.out.println("Devices returned: " + devices.size());

			ListIterator<Device> itr = devices.listIterator();
			while (itr.hasNext()) {
				Device device = itr.next();
				System.out.println("Device: id=[" + device.getId() + " token=[" + device.getToken() + "]");
			}

			System.out.println("done");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
