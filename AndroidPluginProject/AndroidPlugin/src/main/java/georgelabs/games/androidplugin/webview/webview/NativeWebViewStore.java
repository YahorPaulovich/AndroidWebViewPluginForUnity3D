package com.voxelbusters.android.essentialkit.features.webview;

import java.util.HashMap;
public class NativeWebViewStore
{
    private HashMap<String, WebkitWebView> collection = new HashMap();

    private static final NativeWebViewStore ourInstance = new NativeWebViewStore();

    public static NativeWebViewStore getInstance()
    {
        return ourInstance;
    }

    private NativeWebViewStore()
    {
    }

    public String add(WebkitWebView view)
    {
        String tag = "" + System.currentTimeMillis();
        collection.put(tag, view);

        return tag;
    }

    public void remove(String tag)
    {
        collection.remove(tag);
    }

    public WebkitWebView get(String tag)
    {
        return collection.get(tag);
    }

}
