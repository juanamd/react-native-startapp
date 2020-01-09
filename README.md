# react-native-startapp
React Native implementation of Startapp ads SDK
***Only Android is supported for now***
## Usage

```javascript
import React from "react";
import Startapp, { Banner } from "react-native-startapp";

const useReturnAds = false;
await Startapp.initialize(STARTAPP_APP_ID, useReturnAds);
await Startapp.setUserConsent(true);
await Startapp.loadRewarded();
Startapp.addRewardedListener(() => console.log("rewarded watched!"));
await Startapp.showRewarded();
await Startapp.loadInterstitial();
await Startapp.showInterstitial();

<Banner style={{ width: 320, height: 50 }} />;
```
