package com.esipe.tpiotesp32;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeMenuActivity extends AppCompatActivity {
    Button btnSetup,btnTemperature,btnHumidity;
    String macAdresseDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("MAC_ADRESSE_DEVICE")){ // vérifie qu'une valeur est associée à la clé “edittext”
            macAdresseDevice = intent.getStringExtra("MAC_ADRESSE_DEVICE"); // on récupère la valeur associée à la clé
        }else{
            System.out.println("error");
        }
        System.out.println("mac"+macAdresseDevice);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_menu);
        btnSetup=findViewById(R.id.btnSetup);
        btnTemperature=findViewById(R.id.btnTemperature);
        btnHumidity=findViewById(R.id.btnhumidity);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(HomeMenuActivity.this, SetupEsp32Activity.class);
                myIntent.putExtra("MAC_ADRESSE_DEVICE", macAdresseDevice);
                HomeMenuActivity.this.startActivity(myIntent);


            }
        });
        btnTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent2 = new Intent(HomeMenuActivity.this, TemperatureActivity.class);
                HomeMenuActivity.this.startActivity(myIntent2);


            }
        });
        btnHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent3 = new Intent(HomeMenuActivity.this, HumidityActivity.class);
                HomeMenuActivity.this.startActivity(myIntent3);


            }
        });
    }
}
