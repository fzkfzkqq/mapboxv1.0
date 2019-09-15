
package com.d26.mapbox.base;
//references: https://blog.csdn.net/tyk0910/article/details/51355026
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.app.Fragment;
/*
public abstract class BaseActivity extends AppCompatActivity {

    //content vierw ID
    protected abstract int getContentViewId();

    //layout fragment ID
    protected abstract int getFragmentContentId();

    //getfirst
    //add Fragment
    protected void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //remove fragment
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    //return button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

 */
