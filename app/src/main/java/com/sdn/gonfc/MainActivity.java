package com.sdn.gonfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sdn.gonfc.entity.NfcStatusObject;
import com.sdn.gonfc.nfcController.NfcReadWriteController;
import com.sdn.gonfc.nfcController.NfcStatusListener;

public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleChangeBtn;
    private EditText editText;
    private NfcReadWriteController nfcController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleChangeBtn = findViewById(R.id.id_nfc_selector);
        editText = findViewById(R.id.id_editText);
        nfcController = new NfcReadWriteController(this);
    }

    private void checkNfcIsAvailability() {
        nfcController.isNfcAvailable(new NfcStatusListener() {
            @Override
            public void nfcProcessOnError(NfcStatusObject object) {
                if (!object.success) {
                    Toast.makeText(getApplicationContext(), "NFC not available !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void nfcMessageOnSuccess(NfcStatusObject object) {

                if (object.success) {
                    Toast.makeText(getApplicationContext(), "NFC available", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void onToggleBtnChange(View view) {
        editText.setText("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (toggleChangeBtn.isChecked()) {
            nfcController.readNfcTag(intent, new NfcStatusListener() {
                @Override
                public void nfcProcessOnError(NfcStatusObject object) {
                    Toast.makeText(getApplicationContext(), "status : " + object.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void nfcMessageOnSuccess(NfcStatusObject object) {
                    if (object.isSuccess())
                        editText.setText(object.getMessage());
                    Toast.makeText(getApplicationContext(), "Read tag successful", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            nfcController.writeNfcTag(intent, editText.getText().toString(), new NfcStatusListener() {
                @Override
                public void nfcProcessOnError(NfcStatusObject object) {
                    Toast.makeText(getApplicationContext(), "status : " + object.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void nfcMessageOnSuccess(NfcStatusObject object) {
                    if (object.isSuccess())
                        Toast.makeText(getApplicationContext(), "Tag written", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNfcIsAvailability();
        nfcController.enableForegroundDispatchSystem(this, MainActivity.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcController.disableForegroundDispatchSystem(this);
    }
}
