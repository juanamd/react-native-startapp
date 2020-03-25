package os.juanamd.startapp;

import android.util.Log;
import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.common.model.AdPreferences;

public class RNStartappBanner extends SimpleViewManager<ReactViewGroup> {
	private static final String TAG = "RNStartappBanner";

	@Override
	public String getName() {
		return TAG;
	}

	@Override
	protected ReactViewGroup createViewInstance(final ThemedReactContext themedReactContext) {
		final Banner banner = createBanner(themedReactContext);
        final ReactViewGroup mainView = new ReactViewGroup(themedReactContext) {
			@Override
			protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
				super.onLayout(changed, left, top, right, bottom);
				this.getViewTreeObserver().dispatchOnGlobalLayout();
			}
		};
		mainView.addView(banner);
		banner.loadAd();
		return mainView;
	}

	private Banner createBanner(final ThemedReactContext themedReactContext) {
		final AdPreferences prefs = new AdPreferences();
		//prefs.setTestMode(true);
		return new Banner(themedReactContext, prefs) {
			private boolean hasFailed = false;
			@Override
			protected void onAttachedToWindow() {
				super.onAttachedToWindow();
				if (hasFailed) reload();
				else showBanner();
			}
			@Override
			protected void onDetachedFromWindow() {
				super.onDetachedFromWindow();
				hideBanner();
			}
			@Override
			public void onReceiveAd(final Ad ad) {
				super.onReceiveAd(ad);
				Log.d(TAG, "onReceiveAd");
				hasFailed = false;
				try {
					int width = ((View) getParent()).getWidth();
					int height = ((View) getParent()).getHeight();
					if (width > 0 && height > 0) {
						measure(width, height);
						layout(0, 0, width, height);
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			@Override
			public void onFailedToReceiveAd(final Ad ad) {
				Log.d(TAG, "onFailedToReceiveAd");
				super.onFailedToReceiveAd(ad);
				hasFailed = true;
			}
			@Override
			public void setErrorMessage(final String error) {
				Log.d(TAG, "setErrorMessage: " + error);
				super.setErrorMessage(error);
			}
		};
	}
}
