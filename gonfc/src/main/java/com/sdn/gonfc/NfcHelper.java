package com.sdn.gonfc;

import com.sdn.gonfc.entity.NfcStatusObject;

public class NfcHelper {

    public static NfcStatusObject getStatusObject(int code, String message, boolean status) {
        NfcStatusObject statusObject = new NfcStatusObject();
        statusObject.setSuccess(status);
        statusObject.setMessage(message);
        statusObject.setStatusCode(code);
        return statusObject;
    }
}