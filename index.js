import { NativeModules, requireNativeComponent } from 'react-native';

const { RNStartapp } = NativeModules;

export default RNStartapp;

export const Banner = requireNativeComponent("RNStartappBanner");
