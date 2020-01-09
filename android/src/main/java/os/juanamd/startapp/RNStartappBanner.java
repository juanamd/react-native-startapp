package os.juanamd.startapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactPropGroup;

import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.ads.banner.BannerListener;

public class RNStartappBanner extends SimpleViewManager<RelativeLayout> {
	private static final String TAG = "RNStartappBanner";

	@Override
	public String getName() {
		return TAG;
	}

	@Override
	protected RelativeLayout createViewInstance(ThemedReactContext themedReactContext) {
		final Banner startappBanner = createBanner(themedReactContext);

        RelativeLayout mainLayout = new RelativeLayout(themedReactContext) {
			@Override protected void onAttachedToWindow() {
				super.onAttachedToWindow();
				Log.d(TAG, "onAttachedToWindow");
				if (getChildCount() == 0) addView(startappBanner);
				startappBanner.loadAd();
				startappBanner.showBanner();
			}
			@Override
			protected void onDetachedFromWindow() {
				super.onDetachedFromWindow();
				Log.d(TAG, "onDetachedFromWindow");
				startappBanner.hideBanner();
				if (getChildCount() > 0) removeAllViews();
			}
			@Override
			public void requestLayout() {
				super.requestLayout();
				Log.d(TAG, "requestLayout");
				if (getWidth() > 0 && getHeight() > 0) {
					measure(getWidth(), getHeight());
					layout(getLeft(), getTop(), getRight(), getBottom());
				}
			}
		};
		return mainLayout;
	}

	private Banner createBanner(ThemedReactContext themedReactContext) {
		return new Banner(themedReactContext, new BannerListener() {
			@Override
			public void onReceiveAd(View banner) {
				Log.d(TAG, "onReceiveAd");
			}
			@Override
			public void onFailedToReceiveAd(View banner) {
				Log.d(TAG, "onFailedToReceiveAd");
			}
			@Override
			public void onClick(View banner) {
				Log.d(TAG, "onClick");
			}
		});
	}
}
