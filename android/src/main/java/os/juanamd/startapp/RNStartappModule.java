package os.juanamd.startapp;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd.AdMode;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

public class RNStartappModule extends ReactContextBaseJavaModule {
	private static final String TAG = "RNStartapp";

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
			this.showAd(this.getRewarded(), promise);
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
			this.showAd(this.getInterstitial(), promise);
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

	private void showAd(final StartAppAd startappAd, final Promise promise) {
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
			}
			@Override
			public void adHidden(Ad ad) {
			}
		});
	}
}
