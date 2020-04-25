package com.esipe.tpiotesp32;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TemperatureActivity extends AppCompatActivity {

    MqttHelper mqttHelper;
    ChartHelper mChart;
    LineChart chart;
    TextView dataReceived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperaure);
        dataReceived = (TextView) findViewById(R.id.dataReceived);
        chart = (LineChart) findViewById(R.id.chart);
        mChart = new ChartHelper(chart);
        startMqtt();
    }
    @Override
    public void onDestroy() {
        mqttHelper.disconnect();
        super.onDestroy();
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext(),"capteur/temperature");
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if(topic.equals("capteur/temperature")) {
                    Log.w("Debug", mqttMessage.toString());
                    dataReceived.setText(mqttMessage.toString());
                    mChart.addEntry(Float.valueOf(mqttMessage.toString()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
