package com.nampt.socialnetworkproject.view.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Toast;

import com.nampt.socialnetworkproject.view.home.HomeActivity;
import com.nampt.socialnetworkproject.view.notification.NotificationActivity;
import com.nampt.socialnetworkproject.R;
import com.nampt.socialnetworkproject.data_local.DataLocalManager;

public class LoginActivity extends AppCompatActivity implements ILoginCallBacks {

    Button btnLogin;
    FragmentTransaction ft;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    NotificationActivity notificationActivity = new NotificationActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addControl();
        addEvent();
    }


    private void addControl() {
        btnLogin = findViewById(R.id.btnLogin);
        ft = getSupportFragmentManager().beginTransaction();
        loginFragment = new LoginFragment();
        ft.add(R.id.main_holder_login, loginFragment, "LOGIN_FRAGMENT");
        ft.commit();

    }

    private void addEvent() {
        if (!DataLocalManager.getMyFirstInstall()) {
            DataLocalManager.setMyFirstInstall(true);
            Toast toast = Toast.makeText(this, "Chào mừng đến với mạng xã hội NTalk", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 32);
            toast.show();
        }
        if (DataLocalManager.getPrefUser() != null) {
            if (DataLocalManager.getPrefUser().getAccessToken() != null) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                this.finishAffinity();
            }
        }
    }


    @Override
    public void onMsgFromFragToMain(String sender) {
        switch (sender) {
            case "LOGIN_FRAGMENT":
                ft = getSupportFragmentManager().beginTransaction();
                registerFragment = new RegisterFragment();
                ft.add(R.id.main_holder_login, registerFragment, "REGISTER_FRAGMENT");
                ft.addToBackStack("REGISTER_FRAGMENT");
                ft.commit();
                break;
            case "REGISTER_FRAGMENT":
                if (getSupportFragmentManager() != null) {
                    getSupportFragmentManager().popBackStack();
                }

                break;
        }
    }
}