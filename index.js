import { NativeModules, requireNativeComponent } from 'react-native';

const { RNStartapp } = NativeModules;

export default RNStartapp;

export const Banner = requireNativeComponent("RNStartappBanner");

export const { INTERSTITIAL_MODE, REWARDED_MODE, UNKNOWN_MODE,
				DISPLAY_EVENT, CLICKED_ACTION, CLOSED_ACTION } = RNStartapp;
