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
import android.os.Parcelable;

import com.sdn.gonfc.Constants;
import com.sdn.gonfc.NfcHelper;
import com.sdn.gonfc.nfcController.NfcStatusListener;

import java.io.UnsupportedEncodingException;


public class NfcReadServiceImpl implements NfcReadServiceInterface {


    private final Context context;

    public NfcReadServiceImpl() {
        throw new RuntimeException("Empty NfcServiceImpl is not allowed!");
    }

    public NfcReadServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void nfcReadService(Intent intent, NfcStatusListener nfcStatusListener) {
        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (parcelables != null && parcelables.length > 0) {
            readContentFromMessage((NdefMessage) parcelables[0], context, nfcStatusListener);
        } else {
            nfcStatusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.NO_NDEF_MESSAGE_FOUND, "NO_NDEF_MESSAGE_FOUND", false));
        }

    }

    private void readContentFromMessage(NdefMessage ndefMessage, Context context, NfcStatusListener nfcStatusListener) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getMessageFromNdefRecord(ndefRecord, nfcStatusListener);
            nfcStatusListener.nfcMessageOnSuccess(NfcHelper.getStatusObject(Constants.READ_SUCESSFUL, tagContent, true));
        } else {
            nfcStatusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.NO_NDEF_RECORDS_FOUND, "NO_NDEF_RECORDS_FOUND", false));
        }
    }

    public String getMessageFromNdefRecord(NdefRecord ndefRecord, NfcStatusListener nfcStatusListener) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            nfcStatusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.UNSUPPORTED_ENCODING_EXCEPTION, "UNSUPPORTED_ENCODING_EXCEPTION", false));
        }
        return tagContent;
    }
}
