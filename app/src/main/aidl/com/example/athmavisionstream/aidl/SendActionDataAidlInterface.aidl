// SendActionDataAidlInterface.aidl
package com.example.athmavisionstream.aidl;

// Declare any non-default types here with import statements

interface SendActionDataAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void OnActionDataSend(String action, String title, String artist);
}
