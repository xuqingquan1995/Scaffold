package top.xuqingquan.web;

import android.view.KeyEvent;
import com.tencent.smtt.sdk.WebView;
import top.xuqingquan.web.agent.EventInterceptor;
import top.xuqingquan.web.agent.IEventHandler;

/**
 * IEventHandler 对事件的处理，主要是针对
 * 视屏状态进行了处理 ， 如果当前状态为 视频状态
 * 则先退出视频。
 */
public class EventHandlerImpl implements IEventHandler {
	private WebView mWebView;
	private EventInterceptor mEventInterceptor;

	public static EventHandlerImpl getInstantce(WebView view, EventInterceptor eventInterceptor) {
		return new EventHandlerImpl(view, eventInterceptor);
	}

	public EventHandlerImpl(WebView webView, EventInterceptor eventInterceptor) {
		this.mWebView = webView;
		this.mEventInterceptor = eventInterceptor;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return back();
		}
		return false;
	}

	@Override
	public boolean back() {
		if (this.mEventInterceptor != null && this.mEventInterceptor.event()) {
			return true;
		}
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return false;
	}

}
