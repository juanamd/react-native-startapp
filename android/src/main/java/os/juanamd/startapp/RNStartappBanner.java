package os.juanamd.startapp;

import android.util.Log;
import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;

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
		return new Banner(themedReactContext) {
			@Override
			public void onAttachedToWindow() {
				Log.d(TAG, "onAttachedToWindow");
				super.onAttachedToWindow();
				showBanner();
			}
			@Override
			public void onDetachedFromWindow() {
				Log.d(TAG, "onDetachedFromWindow");
				super.onDetachedFromWindow();
				hideBanner();
			}
			@Override
			public void onReceiveAd(final Ad ad) {
				super.onReceiveAd(ad);
				Log.d(TAG, "onReceiveAd");
				try {
					int width = ((View) getParent()).getWidth();
					int height = ((View) getParent()).getHeight();
					if (width > 0 && height > 0) {
						measure(width, height);
						layout(0, 0, width, height);
						showBanner();
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
			@Override
			public void onFailedToReceiveAd(final Ad ad) {
				Log.d(TAG, "onFailedToReceiveAd");
				super.onFailedToReceiveAd(ad);
			}
			@Override
			public void setErrorMessage(final String error) {
				Log.d(TAG, "setErrorMessage: " + error);
				super.setErrorMessage(error);
			}
		};
	}
}
