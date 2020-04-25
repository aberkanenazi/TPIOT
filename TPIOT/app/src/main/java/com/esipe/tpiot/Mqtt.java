package com.esipe.tpiot;

import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;

import androidx.annotation.NonNull;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

/**
 * Cette classe contient liée a la connexion deconexion echange de message avec mqtt
 */
public class Mqtt {
    MqttAndroidClient mqttAndroidClient;

    public Mqtt() {
        mqttAndroidClient = null;
    }

    /**
     * Obtenir un client mqtt
     * @param context
     * @param brokerUrl
     * @param clientId
     * @return
     */
    public MqttAndroidClient getMqttClient(Context context, String brokerUrl, String clientId) {
        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
        try {
            mqttAndroidClient.connect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    String message = "Vous etes connecter au serveur mqtt";
                    System.out.println(message);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    String message = "Erreur lors de la connexion";
                    System.out.println(message);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return mqttAndroidClient;
    }

    /**
     * publier un message dans un topic
     * @param client
     * @param msg
     * @param qos
     * @param topic
     * @throws MqttException
     * @throws UnsupportedEncodingException
     */
    public void publishMessage(@NonNull MqttAndroidClient client,
                               @NonNull String msg, int qos, @NonNull String topic)
            throws MqttException, UnsupportedEncodingException {
        byte[] encodedPayload = new byte[0];
        encodedPayload = msg.getBytes("UTF-8");
        MqttMessage message = new MqttMessage(encodedPayload);
        message.setRetained(true);
        message.setQos(qos);
        client.publish(topic, message);
    }

    /**
     * soucrire a un topic
     * @param client
     * @param topic
     * @param qos
     * @throws MqttException
     */
    public void subscribe(@NonNull MqttAndroidClient client,
                          @NonNull final String topic, int qos) throws MqttException {
        IMqttToken token = client.subscribe(topic, qos);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Subscribe Successfully " + topic);
            }
            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e(TAG, "Subscribe Failed " + topic);
            }
        });
    }

    /**
     * Deconnexion du client
     * @param client
     * @throws MqttException
     */
    public void disconnect(@NonNull MqttAndroidClient client)
            throws MqttException {
        IMqttToken mqttToken = client.disconnect();
        mqttToken.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d(TAG, "Successfully disconnected");
            }
            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.d(TAG, "Failed to disconnected " + throwable.toString());
            }
        });
    }
}
