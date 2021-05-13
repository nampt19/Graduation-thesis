package com.nampt.socialnetworkproject.view.profileUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.nampt.socialnetworkproject.R;

public class PickerActivity extends AppCompatActivity {
    Toolbar toolbar;
    AutoCompleteTextView autoSearch;
    boolean isSchool;
    String arr[];
    ArrayAdapter<String> pickerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        addControl();
        initToolbar();

    }

    private void addControl() {
        isSchool = getIntent().getBooleanExtra("isSchool", false);
        toolbar = findViewById(R.id.toolbar_picker_activity);
        autoSearch = findViewById(R.id.auto_complete_text_picker);
        if (isSchool) {
            arr = getResources().getStringArray(R.array.arr_school);
        } else {
            arr = getResources().getStringArray(R.array.arr_city);

        }
        pickerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arr);
        autoSearch.setAdapter(pickerAdapter);
        autoSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent returnIntent = new Intent();
                if (isSchool) {
                    returnIntent.putExtra("school", autoSearch.getText().toString());
                } else {
                    returnIntent.putExtra("address", autoSearch.getText().toString());
                }
                setResult(Activity.RESULT_OK, returnIntent);
                onBackPressed();
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (isSchool)
                getSupportActionBar().setTitle("Chọn trường học");
            else
                getSupportActionBar().setTitle("Chọn tỉnh/thành phố hiện tại");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
    }
}