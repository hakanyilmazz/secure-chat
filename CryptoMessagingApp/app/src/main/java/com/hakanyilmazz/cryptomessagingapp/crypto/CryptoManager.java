package com.hakanyilmazz.cryptomessagingapp.crypto;

import java.util.Arrays;

public class CryptoManager {

    private static CryptoManager cryptoManager;

    private int privateKey;

    public static CryptoManager getInstance() {
        if (cryptoManager == null) {
            cryptoManager = new CryptoManager();
        }

        return cryptoManager;
    }

    private CryptoManager() {
        privateKey = 42;
    }

    public String decryptToMessage(String encryptedMessage) {
        String temp = encryptedMessage.substring(1, encryptedMessage.length() - 1);
        temp = temp.replaceAll(", ", "-");

        String[] messageArray = temp.split("-");

        String result = "";
        for (int i = 0; i < messageArray.length; i++) {
            int firstDecrypt  = privateKey ^ Integer.parseInt(messageArray[i]) ^ privateKey;
            int secondDecrypt = privateKey ^ firstDecrypt;

            result += (char) secondDecrypt;
        }

        return result;
    }

    public String encryptToMessage(String message) {
        int[] result = new int[message.length()];

        for (int i = 0; i < message.length(); i++) {
            result[i] = privateKey ^ message.charAt(i);
        }

        return Arrays.toString(result);
    }

}
