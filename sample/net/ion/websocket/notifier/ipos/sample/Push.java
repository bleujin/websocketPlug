package net.ion.websocket.notifier.ipos.sample;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;

public class Push {
	// APNs Server Host & port
	private static final String HOST = "gateway.sandbox.push.apple.com";
	private static final int PORT = 2195;

	// Badge
	private static final int BADGE = 66;

	// iPhone's UDID (64-char device token)
	private static String iPhoneId = "2ed202ac08ea9...cf8d55910df290567037dcc4";
	private static String certificate = "/absolute/path/to/my/certificate";
	private static String passwd = "mycertificatepassword";

	public static void main(String[] args) throws Exception {

		System.out.println("Setting up Push notification");

		try {
			// Setup up a simple message
			PayLoad aPayload = new PayLoad();
			aPayload.addBadge(BADGE);
			System.out.println("Payload setup successfull.");

			System.out.println(aPayload);

			// Get PushNotification Instance
			PushNotificationManager pushManager = PushNotificationManager.getInstance();

			// Link iPhone's UDID (64-char device token) to a stringName
			pushManager.addDevice("iPhone", iPhoneId);
			System.out.println("iPhone UDID taken.");

			System.out.println("Token: " + pushManager.getDevice("iPhone").getToken());

			// Get iPhone client
			Device client = pushManager.getDevice("iPhone");
			System.out.println("Client setup successfull.");

			// Initialize connection
			pushManager.initializeConnection(HOST, PORT, certificate, passwd, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
			System.out.println("Connection initialized...");

			// Send message
			pushManager.sendNotification(client, aPayload);
			System.out.println("Message sent!");

			System.out.println("# of attempts: " + pushManager.getRetryAttempts());
			pushManager.stopConnection();

			System.out.println("done");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
