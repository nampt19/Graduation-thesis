package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.login.LoginResponse;
import com.example.nampt.domain.response.search.DataPeopleSearch;
import com.example.nampt.domain.response.search.ListPeopleResponse;
import com.example.nampt.domain.response.search.PeopleResponse;
import com.example.nampt.domain.response.user.AlbumResponse;
import com.example.nampt.domain.response.user.ProfileUserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    BaseResponse signup(String name, String phone, String password);

    LoginResponse login(String phone, String password);

    BaseResponse logout(String token);

    ProfileUserResponse getProfile(String token, int userId);

    BaseResponse setBanner(String token, MultipartFile file);

    BaseResponse setAvatar(String token, MultipartFile file);

    BaseResponse setProfile(String token, String school, String address);

    ListPeopleResponse searchByName(String token, String name);

    BaseResponse changePassword(String token, String newPass, String oldPass);

    PeopleResponse getType(String token, int userId);

    BaseResponse getBlock(String token, int userId);

    AlbumResponse getAlbum(String token, int userId);

    BaseResponse setName(String token, String name);
}
