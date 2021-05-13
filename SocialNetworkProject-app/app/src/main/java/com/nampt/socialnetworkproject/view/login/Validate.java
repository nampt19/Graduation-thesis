package com.nampt.socialnetworkproject.view.login;

import com.google.android.material.textfield.TextInputLayout;

public class Validate {
    public static final String INPUT_EMPTY = "chưa nhập";
    public static final String PHONE_REGEX = "^0[98753]{1}\\d{8}";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])([@#$%^`<>&+=\"!ºª·#~%&'¿¡€,:;*/+-.=_\\[\\]\\(\\)\\|\\_\\?\\\\]{0,})(?=\\S+$).{6,15}";
    public static final String USERNAME_REGEX = "^([a-zA-z0-9 ắằẳẵặăấầẩẫậâáàãảạđếềểễệêéèẻẽẹíìỉĩịốồổỗộôớờởỡợơóòõỏọứừửữựưúùủũụýỳỷỹỵẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ]){3,50}";

    public static boolean validateUsername(TextInputLayout txtUsernameRegister) {
        String username = txtUsernameRegister.getEditText().getText().toString().trim();
        if (username.isEmpty()) {
            txtUsernameRegister.setError(INPUT_EMPTY);
            return false;
        } else if (!username.matches(USERNAME_REGEX)) {
            txtUsernameRegister.setError("3 - 50 kí tự, không kí tự đặc biệt !");
            return false;
        } else {
            txtUsernameRegister.setError(null);
            return true;
        }
    }

    public static boolean validatePhoneNumber(TextInputLayout txtPhone) {
        String phoneNumber = txtPhone.getEditText().getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            txtPhone.setError(INPUT_EMPTY);
            return false;
        } else if (!phoneNumber.matches(PHONE_REGEX)) {
            txtPhone.setError("Sai định dạng");
            return false;
        } else {
            txtPhone.setError(null);
            return true;
        }

    }

    public static boolean validatePasswordRegisterPane(TextInputLayout txtPasswordRegister,
                                                       TextInputLayout txtVerifyPasswordRegister) {
        String password = txtPasswordRegister.getEditText().getText().toString().trim();
        String passwordVerify = txtVerifyPasswordRegister.getEditText().getText().toString().trim();
        if (password.isEmpty() && passwordVerify.isEmpty()) {
            txtPasswordRegister.setError(INPUT_EMPTY);
            txtPasswordRegister.setError(INPUT_EMPTY);
            return false;
        } else if (password.isEmpty() && !passwordVerify.isEmpty()) {
            txtPasswordRegister.setError(INPUT_EMPTY);
            txtVerifyPasswordRegister.setError(null);
            return false;
        } else if (passwordVerify.isEmpty() && !password.isEmpty()) {
            txtPasswordRegister.setError(null);
            txtVerifyPasswordRegister.setError(INPUT_EMPTY);
            return false;
        } else {
            if (!password.matches(PASSWORD_REGEX)) {
                txtPasswordRegister.setError("6 - 15 kí tự, gồm cả chữ và số !");
                txtVerifyPasswordRegister.setError(null);
                return false;
            } else {
                if (!passwordVerify.equals(password)) {
                    txtVerifyPasswordRegister.setError("mật khẩu xác nhận không khớp");
                    txtPasswordRegister.setError(null);
                    return false;
                } else {
                    txtVerifyPasswordRegister.setError(null);
                    txtPasswordRegister.setError(null);
                    return true;
                }
            }
        }
    }


    public static boolean validatePasswordLoginPane(TextInputLayout txtPasswordLogin) {
        String password = txtPasswordLogin.getEditText().getText().toString().trim();
        if (password.isEmpty()) {
            txtPasswordLogin.setError(INPUT_EMPTY);
            return false;
        } else if (!password.matches(PASSWORD_REGEX)) {
            txtPasswordLogin.setError("6 - 15 kí tự, gồm cả chữ và số !");
            return false;
        } else {
            txtPasswordLogin.setError(null);
            return true;
        }
    }

    public static String addChar(String str, char ch, int position) {
        int len = str.length();
        char[] updatedArr = new char[len + 1];
        str.getChars(0, position, updatedArr, 0);
        updatedArr[position] = ch;
        str.getChars(position, len, updatedArr, position + 1);
        return new String(updatedArr);
    }
}
