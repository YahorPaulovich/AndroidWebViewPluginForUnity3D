using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class AndroidWebViewPlugin : MonoBehaviour
{
	private const string _pluginName = "georgelabs.games.androidplugin.AndroidWebViewPlugin";

    private static AndroidJavaClass _pluginClass;
    private static AndroidJavaObject _pluginInstance;

    public static AndroidJavaClass PluginClass
	{
		get
		{
			if (_pluginClass == null)
			{
				_pluginClass = new AndroidJavaClass(_pluginName);
				AndroidJavaClass playerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
				AndroidJavaObject activity = playerClass.GetStatic<AndroidJavaObject>("currentActivity");
				_pluginClass.SetStatic<AndroidJavaObject>("mainActivity", activity);
			}
			return _pluginClass;
		}
	}

	public static AndroidJavaObject PluginInstance
	{
		get
		{
			if (_pluginInstance == null)
			{
				_pluginInstance = PluginClass.CallStatic<AndroidJavaObject>("getInstance");
			}
			return _pluginInstance;
		}
	}

    private void StartPackage(string package, string startActivity)
    {
        AndroidJavaClass activityClass;
        AndroidJavaObject activity, packageManager;
        AndroidJavaObject launch;

        activityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        activity = activityClass.GetStatic<AndroidJavaObject>("currentActivity");
        packageManager = activity.Call<AndroidJavaObject>("getPackageManager");
        launch = packageManager.Call<AndroidJavaObject>("getLaunchIntentForPackage", package);
        activity.Call(startActivity, launch);
    }

    private void Awake()
    {
        Debug.Log("Elapsed Time: " + getElapsedTime());
        //OpenWebView("https://video-slots.live/liveslots");
        StartPackage("georgelabs.games.androidplugin.WebViewActivity", "WebViewActivity");
    }

    private void Update()
    {

    }

    private double getElapsedTime()
	{
        if (Application.platform == RuntimePlatform.Android)
        {
            return PluginInstance.Call<double>("getElapsedTime");
		}

		Debug.LogWarning("Wrong platform");
        return 0;
	}

    public void OpenWebView(string url)
	{
        if (Application.platform == RuntimePlatform.Android)
        {
            PluginInstance.Call("showWebView", url);
		}
    }

	public void CloseWebView()
	{
        if (Application.platform == RuntimePlatform.Android)
        {
            PluginInstance.Call("closeWebView");
		}
    }

    private void OnDestroy()
    {
        CloseWebView();
    }
}