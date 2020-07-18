# react-native-startapp
React Native wrapper of Startapp ads SDK

***Only Android is supported for now***
## Usage

```javascript
import React from "react";
import { DeviceEventEmitter } from "react-native";
import Startapp, { Banner, DISPLAY_EVENT, INTERSTITIAL_MODE, REWARDED_MODE, UNKNOWN_MODE, CLOSED_ACTION, CLICKED_ACTION } from "react-native-startapp";

// Setup
const useReturnAds = false;
await Startapp.initialize(STARTAPP_APP_ID, useReturnAds);
await Startapp.disableSplash();
await Startapp.hasPromptedStartAppConsent(); // true if the automatic StartApp prompt has been accepted or declined; false otherwise
await Startapp.hasAgreedToStartAppConsent(); // boolean
await Startapp.setTestAdsEnabled(true);
await Startapp.setUserConsent(true);

// Rewarded
Startapp.addRewardedListener(() => console.log("rewarded watched!"));
await Startapp.loadRewarded();
await Startapp.showRewarded();

// Interstitial
await Startapp.loadInterstitial();
await Startapp.showInterstitial();

// Events: Native -> JS
DeviceEventEmitter.addListener(DISPLAY_EVENT, ({ mode, action }) => {
	if (mode === INTERSTITIAL_MODE) {
		if (action === CLOSED_ACTION) console.log("Interstitial closed");
		else if (action === CLICKED_ACTION) console.log("Interstitial clicked");
	} else if (mode === REWARDED_MODE) {
		if (action === CLOSED_ACTION) console.log("Rewarded closed");
		else if (action === CLICKED_ACTION) console.log("Rewarded clicked");
	} else if ( mode === UNKNOWN_MODE) {
		console.log("Unknown ad mode");
	}
});

// Banner
<Banner style={{ width: 320, height: 50 }} />;
```
