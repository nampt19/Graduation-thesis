package com.nampt.socialnetworkproject.view.search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.ViewPagerAdapter;

public class SearchActivity extends AppCompatActivity {
    public static final int LAUNCH_PROFILE_ACTIVITY = 1;
    ViewPager mViewPager;
    TabLayout mTapLayout;
    ImageView icBack;

    Fragment peopleSearchFragment,groupChatSearchFragment;

    EditText edtSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        addControl();
        addEvent();
    }

    private void addControl() {
        mTapLayout = findViewById(R.id.tap_layout_search);
        mViewPager = findViewById(R.id.view_pager_search);
        icBack = findViewById(R.id.ic_back_search_activity);
        edtSearch=findViewById(R.id.edt_search);
    }

    private void addEvent() {
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        peopleSearchFragment = new PeopleSearchFragment();
        groupChatSearchFragment = new GroupChatSearchFragment();

        viewPagerAdapter.addFrag(peopleSearchFragment, "mọi người");
        viewPagerAdapter.addFrag(groupChatSearchFragment, "nhóm");



        mViewPager.setAdapter(viewPagerAdapter);
        mTapLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(2);

        edtSearch.requestFocus();
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                   String strValue = edtSearch.getText().toString().trim();
                    if (!strValue.isEmpty()) {
                        ((PeopleSearchFragment) peopleSearchFragment).onMsgFromMainToFragment(strValue);
                        ((GroupChatSearchFragment) groupChatSearchFragment).onMsgFromMainToFragment(strValue);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}