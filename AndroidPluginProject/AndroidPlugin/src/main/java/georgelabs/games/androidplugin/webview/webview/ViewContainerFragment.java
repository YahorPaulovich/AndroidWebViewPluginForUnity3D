package com.voxelbusters.android.essentialkit.features.webview;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.voxelbusters.android.essentialkit.utilities.Logger;

public class ViewContainerFragment extends Fragment
{
    ViewGroup view;

    public void setView(ViewGroup view)
    {
        this.view = view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
        View onCreateView = super.onCreateView(inflater, group, saved);

        return onCreateView;
    }


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        onContextAttached(activity);
    }

    private void onContextAttached(Context context)
    {

        if (view == null)
        {
            close();
        }
    }

    public void close()
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commitAllowingStateLoss();
    }



    public static ViewContainerFragment embedView(ViewGroup view, Activity parentActivity)
    {
        ViewContainerFragment fragment = null;
        if(view != null) {
            fragment = new ViewContainerFragment();
            fragment.setView(view);

            // Begin Transaction to add to the activity
            FragmentTransaction transaction = parentActivity.getFragmentManager().beginTransaction();
            transaction.add(0, fragment);
            //transaction.commitAllowingStateLoss();
            transaction.commit();
        } else {
            Logger.error("Cannot launch with empty view");
        }

        return fragment;
    }
}
