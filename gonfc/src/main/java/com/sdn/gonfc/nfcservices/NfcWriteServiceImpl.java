/**
 Copyright [2019] [soumen debnath]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.sdn.gonfc.nfcservices;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import com.sdn.gonfc.Constants;
import com.sdn.gonfc.NfcHelper;
import com.sdn.gonfc.nfcController.NfcStatusListener;

import java.io.ByteArrayOutputStream;
import java.util.Locale;



public class NfcWriteServiceImpl implements NfcWriteServiceInterface {


    private final Context context;

    public NfcWriteServiceImpl() {
        throw new RuntimeException("Empty NfcServiceImpl is not allowed!");
    }

    public NfcWriteServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void nfcWriteService(Intent intent, String content, NfcStatusListener statusListener) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage ndefMessage = createNdefMessage(content, statusListener);
        writeNdefMessage(tag, ndefMessage, context, statusListener);
    }

    private NdefMessage createNdefMessage(String content, NfcStatusListener statusListener) {
        NdefRecord ndefRecord = createTextRecord(content, statusListener);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
        return ndefMessage;
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage, Context context, NfcStatusListener statusListener) {
        try {
            if (tag == null) {
                statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_TAG_IS_NULL, "ERROR_TAG_IS_NULL", false));
                return;
            }
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                //format tag with the ndef format and writes the message
                formatTag(tag, ndefMessage, statusListener);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_TAG_IS_NOT_WRITABLE, "ERROR_TAG_IS_NOT_WRITABLE", false));
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                statusListener.nfcMessageOnSuccess(NfcHelper.getStatusObject(Constants.WRITE_SUCCESSFUL, "WRITE_SUCCESSFUL", true));

            }
        } catch (Exception e) {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_NDEF_CONNECT, "ERROR_NDEF_CONNECT", false));
        }
    }

    private NdefRecord createTextRecord(String content, NfcStatusListener statusListener) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (Exception e) {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_CREATE_TEXT_RECORD, "ERROR_CREATE_TEXT_RECORD", false));
        }
        return null;
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage, NfcStatusListener statusListener) {
        NdefFormatable ndefFormatable = NdefFormatable.get(tag);
        if (ndefFormatable == null) {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_CREATE_TEXT_RECORD, "ERROR_CREATE_TEXT_RECORD", false));
            return;
        }
        try {
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        } catch (Exception e) {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_NDEF_FORMATABLE_CONNECT, "ERROR_NDEF_FORMATABLE_CONNECT", false));


        }
    }
}
