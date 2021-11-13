package com.voxelbusters.android.essentialkit.features.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.voxelbusters.android.essentialkit.Resource;
import com.voxelbusters.android.essentialkit.common.ByteBuffer;
import com.voxelbusters.android.essentialkit.common.annotations.RunOnUiThread;
import com.voxelbusters.android.essentialkit.common.annotations.SkipInCodeGenerator;
import com.voxelbusters.android.essentialkit.common.interfaces.IFeature;
import com.voxelbusters.android.essentialkit.utilities.ApplicationUtil;
import com.voxelbusters.android.essentialkit.utilities.FileUtil;
import com.voxelbusters.android.essentialkit.utilities.Logger;
import com.voxelbusters.android.essentialkit.utilities.ResourcesUtil;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
public class WebkitWebView extends FrameLayout implements IFeature
{
    private final String PDF_OPENER_PREFIX = "http://docs.google.com/gview?embedded=true&url=";

    Context           context;
    WebView           webView;
    NavigationToolBar toolBar;
    ImageButton       closeButton;
    LinearLayout      progressViewLayout;
    String            tag;
    IWebViewListener  viewListener;

    IEvaluateJavaScriptListener evaluateJavaScriptListener;
    RectF             rect       = new RectF();
    Rect              screenRect = new Rect();
    ArrayList<String> supportedSchemaList = new ArrayList();
    private boolean canGoBack = true;
    private boolean canGoForward = true;
    JavaScriptInterface javaScriptInterface;
    private boolean isLoading;
    private String url, title;
    private boolean showLoadingOnLoad;
    private boolean autoShowAfterLoad;
    private ViewContainerFragment viewContainerFragment;

    @RunOnUiThread
    public WebkitWebView(Context context)
    {
        super(context);
        this.context = context;

        ((Activity)context).runOnUiThread(()->{

            load();
        });
    }

    @RunOnUiThread
    public void setViewListener(IWebViewListener viewListener)
    {
        this.viewListener = viewListener;
    }


    @RunOnUiThread
    private void load()
    {
        tag = NativeWebViewStore.getInstance().add(this);

        // Setup layout params
        FrameLayout.LayoutParams frameLayoutparams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
        setLayoutParams(frameLayoutparams);

        // Get the layout and inflate
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(ResourcesUtil.getLayoutResourceId(context, Resource.layout.essential_kit_webview_layout), this);

        // Get the references
        toolBar = new NavigationToolBar((LinearLayout) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_topbar_layout)), (ImageButton) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_toolbar_back)), (ImageButton) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_toolbar_forward)), (ImageButton) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_toolbar_reload)), (ImageButton) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_toolbar_close)));
        closeButton = (ImageButton) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_webview_closebutton));
        webView = (WebView) findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_webview));
        try
        {
            closeButton.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    hide();
                }
            });

            toolBar.getBackButton().setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    webView.goBack();
                }
            });
            toolBar.getForwardButton().setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    webView.goForward();
                }
            });

            toolBar.getCloseButton().setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    hide();
                }
            });

            toolBar.getReloadButton().setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    reload();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        javaScriptInterface = new JavaScriptInterface(tag);
        webView.addJavascriptInterface(javaScriptInterface, "WebInterface");

        setWebViewClient();
        setWebViewSettings();
        addProgressViewLayout();
        setStyle(WebKitWebViewStyle.ToolBar);
    }

    @RunOnUiThread
    public void loadUrl(String url)
    {
        this.url = url;
        webView.loadUrl(url);
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getTitle()
    {
        return this.title;
    }

    @RunOnUiThread
    public void loadHtmlString(String html, String baseUrl)
    {
        webView.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null);
    }

    @RunOnUiThread
    public void loadData(ByteBuffer data, int length, String mimeType, String encoding, String baseUrl)
    {
        int    index     = mimeType.lastIndexOf("/");
        String extension = mimeType.substring(index + 1);
        String fileName  = "tempFile" + "." + extension;
        String filePath  = FileUtil.getSavedFile(data.getBytes(), length, ApplicationUtil.getLocalCacheDirectory(context, "temp"), fileName, true);

        webView.loadUrl(filePath);
    }

    @RunOnUiThread
    public void stopLoading()
    {
        webView.stopLoading();
        hideProgressSpinner();
    }

    @RunOnUiThread
    public void show()
    {
        if(getParent() == null)
        {
            if (viewListener != null)
            {
                viewListener.onShow();
            }
            ((Activity)context).addContentView(this, this.getLayoutParams());
            //this.setVisibility(VISIBLE);
            /*Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("tag", tag);
            context.startActivity(intent);*/

            //viewContainerFragment = ViewContainerFragment.embedView(this, (Activity) context);

        }
    }

    @RunOnUiThread
    public void hide()
    {
        if(closeViewFragment()) {
            if (viewListener != null)
            {
                viewListener.onHide();
                //new Exception().printStackTrace();
            }
        }
    }
    private boolean closeViewFragment()
    {
        ViewGroup parent = (ViewGroup) this.getParent();
        if(parent != null) {
            parent.removeView(this);
            return true;
        } else {
            return false;
        }
    }

    @RunOnUiThread
    public void destroy()
    {
        webView.stopLoading();
        closeViewFragment();

        webView = null;
        NativeWebViewStore.getInstance().remove(tag);
    }

    @RunOnUiThread
    public void reload()
    {
        webView.reload();
    }

    @SkipInCodeGenerator
    public void setWebChromeClient(WebChromeClient client)
    {
        webView.setWebChromeClient(client);
    }

    @RunOnUiThread
    public void setFrame(float x, float y, float width, float height)
    {
        rect.left = x;
        rect.top = y;
        rect.right = x + width;
        rect.bottom = y + height;

        adjustLayout();
    }

    @RunOnUiThread
    public void setStyle(WebKitWebViewStyle style)
    {
        switch (style)
        {
            case Default:
                toolBar.hide();
                closeButton.setVisibility(GONE);
                break;
            case Popup:
                toolBar.hide();
                closeButton.setVisibility(VISIBLE);
                break;
            case ToolBar:
                closeButton.setVisibility(GONE);
                toolBar.show();
                break;
        }
        adjustLayout();
    }

    public double getProgress()
    {
        return webView.getProgress()/100.0;
    }

    public boolean isLoading()
    {
        return isLoading;
    }

    @RunOnUiThread
    public void setBackgroundColor(float red, float green, float blue, float alpha)
    {
        webView.setBackgroundColor(Color.argb((int)(alpha * 255), (int)(red * 255), (int)(green * 255), (int)(blue * 255)));
    }

    @RunOnUiThread
    public void setJavaScriptEnabled(boolean enable)
    {
        webView.getSettings().setJavaScriptEnabled(enable);
    }
    @RunOnUiThread
    public void setScalesPageToFit(boolean scaleToFit)
    {
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        webView.setInitialScale(1);
    }

    @RunOnUiThread
    public void setZoom(boolean enable)
    {
        webView.getSettings().setBuiltInZoomControls(enable);
        webView.getSettings().setSupportZoom(enable);
    }

    @RunOnUiThread
    public void addNewScheme(String scheme)
    {
        supportedSchemaList.add(scheme);
    }

    @RunOnUiThread
    public void removeScheme(String scheme)
    {
        supportedSchemaList.remove(scheme);
    }

    @RunOnUiThread
    public void clearCache()
    {
        webView.clearCache(true);
    }

    @RunOnUiThread
    public void clearCookies()
    {
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            cookieManager.removeAllCookies(status -> Logger.debug("Removed all cookies : " + status));
        }
        else
        {
            cookieManager.removeAllCookie();
        }
    }

    @RunOnUiThread
    public void setNavigation(boolean canGoBack, boolean canGoForward)
    {
        //Set the toolbar options accordingly
        this.canGoBack = canGoBack;
        this.canGoForward = canGoForward;

        //Update toolbar buttons
        setUpToolbarButtons();
    }

    @SkipInCodeGenerator
    public void sendJsEvaluationMessage(String result)
    {
        if (evaluateJavaScriptListener != null)
        {
            evaluateJavaScriptListener.onSuccess(result);
        }
    }

    @RunOnUiThread
    public void evaluateJavaScriptFromString(String jsScript, IEvaluateJavaScriptListener listener)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            webView.evaluateJavascript("javascript:" + jsScript, new ValueCallback<String>()
            {
                @Override
                public void onReceiveValue(String result)
                {
                    if(listener != null)
                    {
                        listener.onSuccess(result);
                    }
                }
            });
        }
        else
        {
            evaluateJavaScriptListener = listener;
            loadUrl("javascript:WebInterface.sendMessage(" + jsScript + ")");
        }
    }

    @RunOnUiThread
    public void setBounce(boolean canBounce)
    {
        if (canBounce)
        {
            webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        else
        {
            webView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        }
    }

    public void setCanGoBack(boolean canGoBack)
    {
        this.canGoBack = canGoBack;
    }

    @RunOnUiThread
    private void setUpToolbarButtons()
    {
        //Setup toolbar options
        toolBar.getBackButton().setEnabled((webView.canGoBack() && canGoBack));
        toolBar.getForwardButton().setEnabled((webView.canGoForward() && canGoForward));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout()
            {
                adjustLayout();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    WebkitWebView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else
                {
                    WebkitWebView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void addProgressViewLayout()
    {
        //Add as child to webview. So, we can have close button on top.
        View view = LayoutInflater.from(context).inflate(ResourcesUtil.getLayoutResourceId(context, Resource.layout.essential_kit_progressbar_layout), this, true);

        progressViewLayout = (LinearLayout) view.findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_progressbar_root));
        progressViewLayout.setBackgroundColor(context.getResources().getColor(ResourcesUtil.getColorResourceId(context, Resource.color.ESSENTIAL_KIT_COLOR_SEMI_TRANSPARENT)));

        ProgressBar progressBar = view.findViewById(ResourcesUtil.getLayoutIdentifierResourceId(context, Resource.id.essential_kit_progressbar_spinner));

        int overrideColorValue = ContextCompat.getColor(context, ResourcesUtil.getColorResourceId(context, Resource.color.ESSENTIAL_KIT_WEBVIEW_COLOR_LOADING_OVERRIDE));

        if (overrideColorValue != 0)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                progressBar.getIndeterminateDrawable().setColorFilter(overrideColorValue, PorterDuff.Mode.SRC_IN);
            }
            else
            {
                Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
                DrawableCompat.setTint(wrapDrawable, overrideColorValue);
                progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
            }
        }

        hideProgressSpinner();
    }

    private void showProgressSpinner()
    {
        progressViewLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressSpinner()
    {
        progressViewLayout.setVisibility(View.GONE);
    }

    private int getScreenWidth()
    {
        return ((Activity) context).getWindow().getDecorView().getWidth();
    }
    private int getScreenHeight()
    {
        return ((Activity) context).getWindow().getDecorView().getHeight();
    }

    public void adjustLayout()
    {
        int  parentViewWidth  = getScreenWidth();
        int  parentViewHeight = getScreenHeight();

        int                      width  = (int) (rect.width() * parentViewWidth);
        int                      height = (int) (rect.height() * parentViewHeight);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height, Gravity.TOP | Gravity.LEFT);

        params.leftMargin = (int) (rect.left * parentViewWidth);
        params.topMargin = (int) (rect.top * parentViewHeight);

        System.out.println("Adjust Layout [Parent Size] : " + parentViewWidth  + " " + parentViewHeight);
        System.out.println("Adjust Layout [Frame Size] : " + rect.toShortString());

        setLayoutParams(params);

        this.invalidate();
    }

    private void setWebViewSettings()
    {
        WebSettings settings = webView.getSettings();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            settings.setPluginState(PluginState.ON);
        }

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);

        // No cache by default
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Setting zoom by default to false
        settings.setSupportZoom(false);

        settings.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }

        settings.setGeolocationDatabasePath(context.getFilesDir().getPath());

        // cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
    }

    private void setWebViewClient()
    {
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);

                isLoading = true;

                if (showLoadingOnLoad)
                {
                    showProgressSpinner();
                }

                //Update toolbar buttons
                setUpToolbarButtons();

                if (viewListener != null)
                {
                    viewListener.onPageLoadStarted();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);

                if(view.getProgress() != 100 || !isLoading)
                    return;

                Logger.debug("On Page Finished : " + view.getProgress());

                isLoading = false;
                url = view.getUrl();
                title = view.getTitle();

                if (autoShowAfterLoad)
                {
                    webView.refreshDrawableState();
                    show();
                }

                hideProgressSpinner();

                //Update toolbar buttons
                setUpToolbarButtons();

                if (viewListener != null)
                {
                    viewListener.onPageLoadFinished(url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                super.onReceivedError(view, errorCode, description, failingUrl);

                isLoading = false;

                if (autoShowAfterLoad)
                {
                    show();
                }

                hideProgressSpinner();

                //Update toolbar buttons
                setUpToolbarButtons();

                Logger.error("Received Error : " + description);

                if (viewListener != null)
                {
                    viewListener.onPageLoadError(failingUrl, description);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String urlString)
            {
                Logger.debug("URL STRING = " + urlString);

                //First filter userScemelist then move on to default handling
                Uri    uri         = Uri.parse(urlString);
                String schemeOfUrl = uri.getScheme();

                if (supportedSchemaList.contains(schemeOfUrl))
                {
                    //Pass this info to unity.
                    parseCustomScheme(uri);
                    return true;
                }
                else
                {
                    if (urlString != null && urlString.startsWith("intent://"))
                    {
                        try
                        {
                            final Intent intent       = Intent.parseUri(urlString, Intent.URI_INTENT_SCHEME);
                            Intent       existPackage = context.getPackageManager().getLaunchIntentForPackage(intent.getPackage());

                            Runnable runnable = null;
                            if (existPackage != null)
                            {
                                runnable = new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        context.startActivity(intent);
                                    }
                                };

                            }
                            else
                            {
                                runnable = new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                                        context.startActivity(marketIntent);
                                    }
                                };
                            }

                            new Handler(Looper.getMainLooper()).post(runnable);
                            return true;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if (urlString != null && urlString.startsWith("market://"))
                    {

                        new Handler(Looper.getMainLooper()).post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Intent intent = null;
                                try
                                {
                                    intent = Intent.parseUri(urlString, Intent.URI_INTENT_SCHEME);
                                    if (intent != null)
                                    {
                                        context.startActivity(intent);
                                    }
                                }
                                catch (URISyntaxException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return true;
                    }
                    else if (uri != null && !urlString.startsWith(PDF_OPENER_PREFIX) && uri.getPath().endsWith(".pdf"))
                    {
                        loadUrl(PDF_OPENER_PREFIX + urlString);
                        return true;
                    }
                    return false;
                }
            }

            private void parseCustomScheme(Uri uri)
            {
                WebViewMessage message = new WebViewMessage();

                message.url = uri.toString();
                message.host = uri.getHost();
                message.scheme = uri.getScheme();

                String query = uri.getQuery();

                HashMap<String, String> keyValueMap = new HashMap();

                if (query != null)
                {
                    String[] keyValuePairs = query.split("&");

                    if (keyValuePairs != null)
                    {
                        for (String keyValuePair : keyValuePairs)
                        {
                            String[] keyAndValue = keyValuePair.split("=");
                            String   key         = keyAndValue[0];
                            String   value       = "";
                            if (keyAndValue.length > 1)
                            {
                                value = keyAndValue[1];
                            }
                            keyValueMap.put(key, value);
                        }

                    }
                }

                message.setArguments(keyValueMap);

                if (viewListener != null)
                {
                    viewListener.onMessageReceived(message);
                }
            }

        });
    }

    @Override
    public String getFeatureName()
    {
        return "Webkit Webview";
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == View.VISIBLE)
        {
            resumeWebView();
        }
        else
        {
            pauseWebView();
        }
    }

    @SuppressLint("NewApi")
    void pauseWebView()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            if (webView != null)
            {
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        webView.evaluateJavascript("if(window.localStream){window.localStream.stop();}", null);
                    }
                };
                ((Activity)context).runOnUiThread(runnable);
            }

        }

        callInternalWebViewMethod("onPause");
    }

    void resumeWebView()
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (getVisibility() == View.VISIBLE)
                {
                    callInternalWebViewMethod("onResume");
                }
            }
        };
        ((Activity)context).runOnUiThread(runnable);
    }

    protected void callInternalWebViewMethod(final String name)
    {
        Activity activity = (Activity) getContext();

        activity.runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                if (webView != null)
                {
                    try
                    {
                        Method method = WebView.class.getDeclaredMethod(name);
                        method.setAccessible(true);
                        method.invoke(webView);
                    }
                    catch (Exception e)
                    {
                        Logger.error("Could not find method " + name);
                    }
                }

            }

        });

    }
}
