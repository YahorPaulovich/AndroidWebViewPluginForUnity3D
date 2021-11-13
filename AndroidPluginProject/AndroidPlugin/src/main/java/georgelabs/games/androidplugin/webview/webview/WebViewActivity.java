package com.voxelbusters.android.essentialkit.features.webview;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.voxelbusters.android.essentialkit.Resource;
import com.voxelbusters.android.essentialkit.common.FullScreenActivity;
import com.voxelbusters.android.essentialkit.utilities.Logger;
import com.voxelbusters.android.essentialkit.utilities.ResourcesUtil;

public class WebViewActivity extends FullScreenActivity
{
    WebkitWebView webView;

    View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener()
    {
        @Override
        public void onViewAttachedToWindow(View v)
        {
            System.out.println("[Activity Monitor] onWebViewAttachedToWindow : " + v);
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            System.out.println("[Activity Monitor] onViewDetachedFromWindow : " + v);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
        {
            Intent intent = getIntent();
            String tag = intent.getStringExtra("tag");
            setupWebView(tag);
        }
        else
        {
            String tag = savedInstanceState.getString("TAG");
            setupWebView(tag);
        }
    }

    private void setupWebView(String tag)
    {
        webView = NativeWebViewStore.getInstance().get(tag);
        Logger.debug("Parent : " + webView.getParent());
        removeParentView(webView);
        setContentView(webView);

        webView.addOnAttachStateChangeListener(listener);
        webView.adjustLayout();

        webView.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
            {
                // TODO Auto-generated method stub
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result)
            {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage)
            {
                Logger.debug(consoleMessage.message());

                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, Callback callback)
            {
                callback.invoke(origin, true, false);//For obtaining permission.
            }

            // < 3.0
            public void openFileChooser(ValueCallback<Uri> callback)
            {
                openFileChooser(callback, "*/*");
            }

            // > 3.0+
            public void openFileChooser(ValueCallback<Uri> callback, String acceptType)
            {
                launchFileChooserActivity(new SerialisedValueCallback(callback), "*/*", false);
            }

            // > 4.1+
            public void openFileChooser(ValueCallback<Uri> callback, String acceptType, String capture)
            {
                openFileChooser(callback, "*/*");
            }

            // > 5.0+
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> callback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                launchFileChooserActivity(new SerialisedValueCallback(callback), "image/*", true);
                return true;
            }

            private void launchFileChooserActivity(final SerialisedValueCallback callback, String acceptType, final boolean isCallbackArray)
            {

                final FileChooserFragment request = new FileChooserFragment();
                request.setCallback(new ResultReceiver(null)
                {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData)
                    {
                        Uri uri = resultData.getParcelable("DATA");
                        if (isCallbackArray)
                        {
                            callback.onReceiveValue(new Uri[]{uri});
                        }
                        else
                        {
                            callback.onReceiveValue(uri);
                        }
                    }
                });
                Bundle bundle = new Bundle();

                bundle.putString("mime-types", acceptType);

                request.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(0, request);
                fragmentTransaction.commit();
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request)
            {

                Runnable runnable = (new Runnable()
                {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run()
                    {
                        request.grant(request.getResources());
                    }
                });

                runOnUiThread(runnable);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //outState.putString("TAG", webView.getTag());
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        removeParentView(webView);
    }

    @Override
    public void onBackPressed() {
        if (ResourcesUtil.getBoolean(this, Resource.string.WEB_VIEW_ALLOW_BACK_NAVIGATION_KEY)) {
            Logger.debug("Dismissing webview as back button is allowed. This is configurable in essential kit settings.");
            super.onBackPressed();
        }
        else {
            Logger.debug("Back button action disabled!");
        }
    }

    private void removeParentView(View view)
    {
        ViewGroup parent = (ViewGroup) view.getParent();
        if(parent != null) {
            parent.removeView(view);
        }
        view.removeOnAttachStateChangeListener(listener);
    }
}
