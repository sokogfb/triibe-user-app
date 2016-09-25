/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.triibe.triibeuserapp.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.triibe.triibeuserapp.R;
import com.example.triibe.triibeuserapp.data.User;
import com.example.triibe.triibeuserapp.view_surveys.ViewSurveysActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthUiActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    @BindView(android.R.id.content)
    View mRootView;

    private FirebaseAuth mAuth;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            setUser();
            Intent intent = new Intent(this, ViewSurveysActivity.class);
            intent.putExtra(ViewSurveysActivity.EXTRA_USER_ID, mUserId);
            startActivity(intent);
            finish();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(AuthUI.GOOGLE_PROVIDER)
                            .setLogo(R.drawable.the_westfield_group_logo)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    RC_SIGN_IN);
        }

        // This code will move to the action of a question that will ask the user if they want
        // to do this (maybe it puts them on double points). Just leaving commented out for now
        // until that part is ready. Or maybe not depending on time.

        // Check if UsageStatsManager for app tracking permission enabled
//        if (AppUsageStats.getUsageStatsList(this).isEmpty()){
//            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setUser();
            Intent intent = new Intent(this, ViewSurveysActivity.class);
            intent.putExtra(ViewSurveysActivity.EXTRA_USER_ID, mUserId);
            startActivity(intent);
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar(R.string.sign_in_cancelled);
            return;
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    private void setUser() {
        if (mAuth.getCurrentUser() != null) {
            User user = new User(mAuth.getCurrentUser().getUid());
            mUserId = user.getId();
            Globals.getInstance().setUser(user);
        }
    }
}
