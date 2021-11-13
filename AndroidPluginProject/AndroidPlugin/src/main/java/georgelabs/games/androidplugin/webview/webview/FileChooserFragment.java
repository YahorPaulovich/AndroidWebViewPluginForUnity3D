package com.voxelbusters.android.essentialkit.features.webview;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.voxelbusters.android.essentialkit.defines.CommonDefines;
import com.voxelbusters.android.essentialkit.helpers.interfaces.IPermissionRequestCallback;
import com.voxelbusters.android.essentialkit.utilities.FileUtil;
import com.voxelbusters.android.essentialkit.utilities.IntentUtil;
import com.voxelbusters.android.essentialkit.utilities.Logger;
import com.voxelbusters.android.essentialkit.utilities.PermissionUtil;

import static android.app.Activity.RESULT_OK;
public class FileChooserFragment extends Fragment
{
    public static final int            REQUEST_CODE_FILE_CHOOSER = 111;
    private             Uri            cameraFileUri             = null;
    private             ResultReceiver callback;

    private String type;
    private String mimeType;

    public FileChooserFragment()
    {
        callback = null;
    }

    public void setCallback(ResultReceiver callback)
    {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle       bundleInfo = getArguments();
        type       = getArguments().getString("type");
        mimeType   = bundleInfo.getString("mime-types");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        PermissionUtil.requestPermission(this.getActivity(), Manifest.permission.CAMERA, "Need camera permission for taking pictures.", new IPermissionRequestCallback()
        {
            @Override
            public void onPermissionGrant()
            {
                Intent galleryIntent = IntentUtil.getGalleryIntent(mimeType);

                cameraFileUri = FileUtil.createLocalCacheFileUri(FileChooserFragment.this.getActivity(), null, 0, CommonDefines.SHARING_DIR, "CameraCapture.jpg");

                Intent extraIntent = IntentUtil.getCameraIntent(FileChooserFragment.this.getActivity(), cameraFileUri);

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{extraIntent});
                startActivityForResult(Intent.createChooser(chooserIntent, ""), REQUEST_CODE_FILE_CHOOSER);
            }

            @Override
            public void onPermissionDeny()
            {
                setCallbackResult(Uri.EMPTY);
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        setCallbackResult(Uri.EMPTY);
    }

    private void setCallbackResult(Uri value)
    {
        if (callback != null)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("DATA", value);
            callback.send(0, bundle);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode)
        {
            case REQUEST_CODE_FILE_CHOOSER:

                if (resultCode == RESULT_OK)
                {
                    Uri value = null;

                    if (intent != null)
                    {
                        value = intent.getData();
                    }

                    if (value == null)
                    {
                        value = cameraFileUri;
                    }

                    Logger.debug("Uri:" + value + " cameraFileUri : " + cameraFileUri);
                    setCallbackResult(value);

                    //Reset callback to handle onDestroy case when there are no activity reuslts.
                    callback = null;
                    break;
                }
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
