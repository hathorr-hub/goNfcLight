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

package com.sdn.gonfc.nfcController;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.text.TextUtils;

import com.sdn.gonfc.Constants;
import com.sdn.gonfc.NfcHelper;
import com.sdn.gonfc.nfcservices.NfcReadServiceImpl;
import com.sdn.gonfc.nfcservices.NfcWriteServiceImpl;

public class NfcReadWriteController implements NfcServiceInterface {

    private final NfcAdapter nfcAdapter;
    private Activity activity;
    private NfcWriteServiceImpl nfcWriteServiceImpl;
    private NfcReadServiceImpl nfcReadServiceImpl;

    public NfcReadWriteController(Activity activity) {
        this.activity = activity;
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (nfcAdapter == null) {
            return;
        }
        nfcReadServiceImpl = new NfcReadServiceImpl(activity);
        nfcWriteServiceImpl = new NfcWriteServiceImpl(activity);
    }

    @Override
    public void enableForegroundDispatchSystem(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, intentFilters, null);
    }

    @Override
    public void disableForegroundDispatchSystem(Activity activity) {
        nfcAdapter.disableForegroundDispatch(activity);
    }

    @Override
    public void isNfcAvailable(NfcStatusListener statusListener) {
        if(nfcAdapter==null || !nfcAdapter.isEnabled()){
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.ERROR_NFC_IS_NOT_AVAILABLE, "ERROR_NFC_IS_NOT_AVAILABLE", false));
        }else{
            statusListener.nfcMessageOnSuccess(NfcHelper.getStatusObject(Constants.NFC_DEVICE_AVAILABLE, "NFC_DEVICE_AVAILABLE", true));
        }
    }

    public void readNfcTag(Intent intent, NfcStatusListener statusListener) {
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            nfcReadService(intent, statusListener);
        } else {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.NO_EXTRA_TAG_FOUND, "NO_EXTRA_TAG_FOUND", false));

        }
    }

    private void nfcReadService(Intent intent, NfcStatusListener statusListener) {
        nfcReadServiceImpl.nfcReadService(intent, statusListener);
    }

    public void writeNfcTag(Intent intent, String content, NfcStatusListener statusListener) {
        if (TextUtils.isEmpty(content)) {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.EMPTY_CONTENT, "EMPTY_CONTENT", false));
            return;
        }
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            nfcWriteService(intent, content, statusListener);
        } else {
            statusListener.nfcProcessOnError(NfcHelper.getStatusObject(Constants.NO_EXTRA_TAG_FOUND, "NO_EXTRA_TAG_FOUND", false));
        }
    }

    private void nfcWriteService(Intent intent, String content, NfcStatusListener statusListener) {
        nfcWriteServiceImpl.nfcWriteService(intent, content, statusListener);
    }
}
