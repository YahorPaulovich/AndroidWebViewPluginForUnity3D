package com.voxelbusters.android.essentialkit.features.webview;

import com.voxelbusters.android.essentialkit.common.annotations.SkipInCodeGenerator;

import java.util.HashMap;
public class WebViewMessage
{
    public  String                  url;
    public  String                  host;
    public  String                  scheme;
    private HashMap<String, String> arguments;

    public String getArgumentValue(String key)
    {
        return arguments.get(key);
    }

    @SkipInCodeGenerator
    public void setArguments(HashMap<String, String> args)
    {
        arguments = args;
    }
}
