package com.example.nampt.helper;

public class Regex {
    // 3 < chuỗi tên < 50 kí tự, không kí tự đặc biêt.
    // số điện thoại 10 số theo kiểu việt nam
    // mật khẩu 6 -15 kí tự, gồm cả chữ và số.

    public static final String PHONE_REGEX = "^0[98753]{1}\\d{8}";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z])([@#$%^`<>&+=\"!ºª·#~%&'¿¡€,:;*/+-.=_\\[\\]\\(\\)\\|\\_\\?\\\\]{0,})(?=\\S+$).{6,15}";
    public static final String USERNAME_REGEX = "^([a-zA-z0-9 ắằẳẵặăấầẩẫậâáàãảạđếềểễệêéèẻẽẹíìỉĩịốồổỗộôớờởỡợơóòõỏọứừửữựưúùủũụýỳỷỹỵẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ]){3,50}";
}
