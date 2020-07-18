package os.juanamd.startapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppAd.AdMode;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.VideoListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;

import java.util.HashMap;
import java.util.Map;

public class RNStartappModule extends ReactContextBaseJavaModule {
	private static final String TAG = "RNStartapp";
	private static final String INTERSTITIAL_MODE = "interstitial";
	private static final String REWARDED_MODE = "rewarded";
	private static final String UNKNOWN_MODE = "unknown";
	private static final String DISPLAY_EVENT = "display";
	private static final String CLICKED_ACTION = "clicked";
	private static final String CLOSED_ACTION = "closed";

	private StartAppAd interstitial;
	private StartAppAd rewarded;

	public RNStartappModule(ReactApplicationContext reactContext) {
		super(reactContext);
	}

	@Override
	public String getName() {
		return TAG;
	}

	@ReactMethod
	public void hasPromptedStartAppConsent(final Promise promise) {
		try {
			SharedPreferences prefs = getReactApplicationContext()
				.getSharedPreferences("com.startapp.sdk", Context.MODE_PRIVATE);
			boolean hasPrompted = prefs.contains("consentApc");
			promise.resolve(hasPrompted);
			Log.d(TAG, "hasPromptedStartAppConsent: " + hasPrompted);
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void hasAgreedToStartAppConsent(final Promise promise) {
		try {
			SharedPreferences prefs = getReactApplicationContext()
				.getSharedPreferences("com.startapp.sdk", Context.MODE_PRIVATE);
			boolean hasAgreed = prefs.getBoolean("consentApc", false);
			promise.resolve(hasAgreed);
			Log.d(TAG, "hasAgreedToStartAppConsent: " + hasAgreed);
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void initialize(final String appId, final boolean useReturnAds, final Promise promise) {
		try {
			StartAppSDK.init(this.getReactApplicationContext(), appId, useReturnAds);
			promise.resolve(null);
			Log.d(TAG, "Initialized");
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void disableSplash(final Promise promise) {
		try {
			StartAppAd.disableSplash();
			promise.resolve(null);
			Log.d(TAG, "disableSplash");
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void setTestAdsEnabled(final boolean value, final Promise promise) {
		try {
			StartAppSDK.setTestAdsEnabled(value);
			promise.resolve(null);
			Log.d(TAG, "Set test ads: " + value);
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void setUserConsent(final boolean value, final Promise promise) {
		try {
			StartAppSDK.setUserConsent(this.getReactApplicationContext(), "pas", System.currentTimeMillis(), value);
			promise.resolve(null);
			Log.d(TAG, "Set user consent");
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void addRewardedListener(final Callback onCompleted) {
		this.getRewarded().setVideoListener(new VideoListener() {
			@Override
			public void onVideoCompleted() {
				onCompleted.invoke();
				Log.d(TAG, "On rewarded video completed");
			}
		});
	}

	@ReactMethod
	public void loadRewarded(final Promise promise) {
		try {
			this.loadAd(this.getRewarded(), AdMode.REWARDED_VIDEO, promise);
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void showRewarded(final Promise promise) {
		try {
			this.showAd(this.getRewarded(), AdMode.REWARDED_VIDEO, promise);
		} catch(Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void loadInterstitial(final Promise promise) {
		try {
			this.loadAd(this.getInterstitial(), AdMode.AUTOMATIC, promise);
		} catch (Exception e) {
			promise.reject(e);
		}
	}

	@ReactMethod
	public void showInterstitial(final Promise promise) {
		try {
			this.showAd(this.getInterstitial(), AdMode.AUTOMATIC, promise);
		} catch(Exception e) {
			promise.reject(e);
		}
	}

	private StartAppAd getRewarded() {
		if (this.rewarded == null) this.rewarded = new StartAppAd(this.getReactApplicationContext());
		return this.rewarded;
	}

	private StartAppAd getInterstitial() {
		if (this.interstitial == null) this.interstitial = new StartAppAd(this.getReactApplicationContext());
		return this.interstitial;
	}


	private void loadAd(final StartAppAd startappAd, final AdMode mode, final Promise promise) {
		startappAd.loadAd(mode, new AdEventListener() {
			@Override
			public void onReceiveAd(Ad ad) {
				try {
					promise.resolve(null);
					Log.d(TAG, "Received ad");
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			@Override
			public void onFailedToReceiveAd(Ad ad) {
				try {
					promise.reject(new Exception("Failed to receive ad"));
					Log.d(TAG, "Failed to receive ad");
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		});
	}

	private void showAd(final StartAppAd startappAd, final AdMode mode, final Promise promise) {
		final RNStartappModule self = this;
		startappAd.showAd(new AdDisplayListener() {
			@Override
			public void adDisplayed(Ad ad) {
				promise.resolve(null);
				Log.d(TAG, "Ad displayed");
			}
			@Override
			public void adNotDisplayed(Ad ad) {
				promise.reject(new Exception("Ad not displayed"));
				Log.e(TAG, "Ad not displayed");
			}
			@Override
			public void adClicked(Ad ad) {
				Log.d(TAG, "Ad clicked");
				try {
					WritableMap params = Arguments.createMap();
					params.putString("mode", self.getModeName(mode));
					params.putString("action", CLICKED_ACTION);
					self.sendJsEvent(DISPLAY_EVENT, params);
				} catch (Exception e) {
					Log.e(TAG, "Error sending clicked event", e);
				}
			}
			@Override
			public void adHidden(Ad ad) {
				Log.d(TAG, "Ad hidden");
				try {
					WritableMap params = Arguments.createMap();
					params.putString("mode", self.getModeName(mode));
					params.putString("action", CLOSED_ACTION);
					self.sendJsEvent(DISPLAY_EVENT, params);
				} catch (Exception e) {
					Log.e(TAG, "Error sending closed event", e);
				}
			}
		});
	}

	private String getModeName(final AdMode mode) {
		if (mode == AdMode.AUTOMATIC) return INTERSTITIAL_MODE;
		if (mode == AdMode.REWARDED_VIDEO) return REWARDED_MODE;
		return UNKNOWN_MODE;
	}

	private void sendJsEvent(final String eventName, WritableMap params) {
		getReactApplicationContext()
			.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
			.emit(eventName, params);
	}

	@Override
	public Map<String, Object> getConstants() {
		final Map<String, Object> constants = new HashMap<>();
		constants.put("INTERSTITIAL_MODE", INTERSTITIAL_MODE);
		constants.put("REWARDED_MODE", REWARDED_MODE);
		constants.put("UNKNOWN_MODE", UNKNOWN_MODE);
		constants.put("DISPLAY_EVENT", DISPLAY_EVENT);
		constants.put("CLICKED_ACTION", CLICKED_ACTION);
		constants.put("CLOSED_ACTION", CLOSED_ACTION);
		return constants;
	}
}
