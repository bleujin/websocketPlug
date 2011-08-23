package net.ion.websocket.notifier.ipos.sample;

import java.util.ArrayList;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;
import javapns.data.PayLoadCustomAlert;

// http://artyst.egloos.com/2652130
public class ApnsManager {

	private String certificatePath = "/work/project/apple-apns-key.p12";
	private String certificatePassword = "인증서암호";
	private String host = "gateway.sandbox.push.apple.com";
	private int port = 2195;

	public void sendSimple() throws Exception {
		PayLoad simplePayLoad = new PayLoad();
		simplePayLoad.addAlert("My alert message");
		simplePayLoad.addBadge(45);
		simplePayLoad.addSound("default");
		Device client = PushNotificationManager.getInstance().getDevice("my_iPhone");
		PushNotificationManager.getInstance().initializeConnection(host, port, certificatePath, certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
		PushNotificationManager.getInstance().sendNotification(client, simplePayLoad);

		// {"aps":{"sound":"default","alert":"My alert message","badge":45}}

	}
	
	public void sendComplecate() throws Exception {
		 // Or create a complex PayLoad with a custom alert
		 PayLoad complexPayLoad = new PayLoad();
		 PayLoadCustomAlert customAlert = new PayLoadCustomAlert();
		 // You can use addBody to add simple message, but we'll use
		 // a more complex alert message so let's comment it
		 // customAlert.addBody("My alert message");
		 customAlert.addActionLocKey("Open App");
		 customAlert.addLocKey("javapns rocks %@ %@%@");
		 ArrayList parameters = new ArrayList();
		 parameters.add("Test1");
		 parameters.add("Test");
		 parameters.add(2);
		 customAlert.addLocArgs(parameters);
		 complexPayLoad.addCustomAlert(customAlert);
		 complexPayLoad.addBadge(45);
		 complexPayLoad.addSound("default");
		 complexPayLoad.addCustomDictionary("acme", "foo");
		 complexPayLoad.addCustomDictionary("acme2", 42);
		 ArrayList values = new ArrayList();
		 values.add("value1");
		 values.add(2);
		 complexPayLoad.addCustomDictionary("acme3", values);
		 Device client = PushNotificationManager.getInstance().getDevice("my_iPhone");
		 PushNotificationManager.getInstance().initializeConnection(host, port, certificatePath, certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
		 PushNotificationManager.getInstance().sendNotification(client, complexPayLoad);

		 // {"aps":{"sound":"default","alert":{"loc-args":["Test1","Test",2],"action-loc-key":"Open App","loc-key":"javapns rocks %@ %@%@"},"badge":45},"acme3":["value1",2],"acme2":42,"acme":"foo"}

	}

	public void sendCase1() throws Exception {

		try {

			String deviceToken = "6a4aa1981062d0b8bxxbbaa3b6b0fdc275exyyx0f5dcac1ce101d314acca1a35";
			PayLoad payLoad = new PayLoad();
			payLoad.addAlert("알림 메시지 테스트");
			payLoad.addSound("default");
			PushNotificationManager pushManager = PushNotificationManager.getInstance();
			pushManager.addDevice("iPhone", deviceToken);
			// Connect to APNs

			pushManager.initializeConnection(host, port, certificatePath, certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);

			// Send Push
			Device client = pushManager.getDevice("iPhone");
			pushManager.sendNotification(client, payLoad);
			pushManager.stopConnection();
			pushManager.removeDevice("iPhone");
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
