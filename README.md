# SignificantLocationChange-Android

SignificantLocationChange : The name itself says about something related to location! Yes, To being updated about current location within specific time interval is very frustrating task in terms of battery consumption! I tried my best to give better solution to keep your user updated with location without handling any time interval. This library will keep you updated when your users location will be changed within 500 meters or more. This is what we called Significant Location Change. Enjoy with very simple implementation by calling startReceivingUpdates method whenever you need it and implement the interface which will keep you updated with Significant Location Change.

How to use it?

StartReceiving Updates whenever you need it by calling :

/*Start Receiving Updates*/
SignificantLocationChange.sharedInstance().startReceivingUpdates(SampleActivity.this);

StopReceiving Updates whenever you are done with your stuffs by calling :

/*Stop ReceivingUpdates*/
SignificantLocationChange.sharedInstance().stopReceivingUpdates(SampleActivity.this);

Implement interface to be updated with new location :

Interface : SignificantLocationChange.SignificantLocation

@Override public void onLocationChanged(Location mLocation) {

}

Enjoy!
