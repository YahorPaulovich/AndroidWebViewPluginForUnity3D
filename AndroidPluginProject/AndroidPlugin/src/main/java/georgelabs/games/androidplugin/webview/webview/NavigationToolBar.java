package com.voxelbusters.android.essentialkit.features.webview;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class NavigationToolBar
{
    public LinearLayout topBarLayout;
    public ImageButton  topBarBackutton;
    public ImageButton  topBarForwardButton;
    public ImageButton  topBarReloadButton;
    public ImageButton  topBarCloseButton;

    public NavigationToolBar(LinearLayout layout, ImageButton back, ImageButton forward, ImageButton reload, ImageButton close)
    {
        topBarLayout = layout;
        topBarBackutton = back;
        topBarForwardButton = forward;
        topBarReloadButton = reload;
        topBarCloseButton = close;
    }

    public void hide()
    {
        topBarLayout.setVisibility(LinearLayout.GONE);
    }

    public void show()
    {
        topBarLayout.setVisibility(LinearLayout.VISIBLE);
    }

    public View getBackButton()
    {

        return topBarBackutton;
    }

    public View getForwardButton()
    {
        return topBarForwardButton;
    }

    public View getReloadButton()
    {
        return topBarReloadButton;
    }

    public View getCloseButton()
    {
        return topBarCloseButton;
    }
}