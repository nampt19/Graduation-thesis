package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.login.DataLoginResponse;
import com.example.nampt.domain.response.login.LoginResponse;
import com.example.nampt.domain.response.search.DataPeopleSearch;
import com.example.nampt.domain.response.search.ListPeopleResponse;
import com.example.nampt.domain.response.search.PeopleResponse;
import com.example.nampt.domain.response.user.AlbumResponse;
import com.example.nampt.domain.response.user.ProfileUserResponse;
import com.example.nampt.entity.Device;
import com.example.nampt.helper.JwtToken;
import com.example.nampt.helper.MD5;
import com.example.nampt.helper.Regex;
import com.example.nampt.entity.Post;
import com.example.nampt.entity.User;
import com.example.nampt.repository.DeviceRepository;
import com.example.nampt.repository.PostRepository;
import com.example.nampt.repository.UserRepository;
import com.example.nampt.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    FriendService friendService;


    @Override
    public BaseResponse signup(String name, String phone, String password) {
        BaseResponse response = new BaseResponse();
        if (!name.matches(Regex.USERNAME_REGEX)) {
            response.setCode(9997);
            response.setMessage("username invalid");
            return response;
        }
        if (!phone.matches(Regex.PHONE_REGEX)) {
            response.setCode(9997);
            response.setMessage("phone invalid");
            return response;
        }
        if (!password.matches(Regex.PASSWORD_REGEX)) {
            response.setCode(9997);
            response.setMessage("password invalid");
            return response;
        }
        if (userRepository.findByPhone(phone) != null) {
            response.setCode(9996);
            response.setMessage("user exist");
            return response;
        }
        User register = new User();
        register.setName(name);
        register.setPhone(phone);
        register.setPassword(MD5.encrypt(password, phone));
        userRepository.save(register);
        response.setCode(1000);
        response.setMessage("OK");
        return response;
    }

    @Override
    public LoginResponse login(String phone, String password) {
        LoginResponse response = new LoginResponse();
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            response.setCode(9992);
            response.setMessage("user is not existed");
        } else if (user != null && !password.equals(MD5.decrypt(user.getPassword(), phone))) {
            response.setCode(9997);
            response.setMessage("password wrong");
        } else if (user != null && password.equals(MD5.decrypt(user.getPassword(), phone))) {
            user.setAccessToken(new JwtToken().generateToken(user));
            user.setOnline(true);
            userRepository.save(user);
            DataLoginResponse data = new DataLoginResponse();
            data.setId(user.getId());
            data.setName(user.getName());
            data.setLink_avatar(user.getLinkAvatar());
            data.setLink_banner(user.getLinkBanner());
            data.setAddress(user.getAddress());
            data.setSchool(user.getSchool());
            data.setAccess_token(user.getAccessToken());
            data.setIs_online(user.isOnline());
            data.setPhone(user.getPhone());

            response.setCode(1000);
            response.setMessage("OK");
            response.setData(data);
        }
        return response;
    }

    @Override
    public BaseResponse logout(String token) {
        BaseResponse baseResponse = new BaseResponse();
        User user = userRepository.findByAccessToken(token);
        if (user == null) {
            baseResponse.setCode(9992);
            baseResponse.setMessage("user is not existed");
        } else {
            user.setAccessToken(null);
            user.setOnline(false);
            userRepository.save(user);

            List<Device> devices = deviceRepo.findAllByUserId(user.getId());
            if (devices != null) {
                deviceRepo.deleteAll(devices);
            }
            baseResponse.setCode(1000);
            baseResponse.setMessage("OK");
        }
        return baseResponse;
    }

    @Override
    public ProfileUserResponse getProfile(String token, int userId) {
        ProfileUserResponse response = new ProfileUserResponse();
        User user = userRepository.findByAccessToken(token);
        User profiler = userRepository.findById(userId);
        if (user != null) {
            if (profiler != null) {
                DataLoginResponse data = new DataLoginResponse();
                data.setId(profiler.getId());
                data.setName(profiler.getName());
                data.setLink_avatar(profiler.getLinkAvatar());
                data.setLink_banner(profiler.getLinkBanner());
                data.setAddress(profiler.getAddress());
                data.setSchool(profiler.getSchool());

                response.setCode(1000);
                response.setMessage("OK");
                response.setData(data);
                response.setBlock(friendService.checkIsBlock(userId, user.getId()));
            } else {
                response.setCode(9992);
                response.setMessage("user is not existed");
            }

        } else {
            response.setCode(9993);
            response.setMessage("token is not existed");
        }
        return response;
    }

    @Override
    public BaseResponse setBanner(String token, MultipartFile file) {
        BaseResponse response = new BaseResponse();
        User user = userRepository.findByAccessToken(token);
        if (user != null) {
            if (file == null || file.isEmpty()) {
                response.setCode(9992);
                response.setMessage("file is invalid");
                return response;
            }
            String urlFile = saveMediaFile(file);
            user.setLinkBanner(urlFile);
            userRepository.save(user);
            response.setCode(1000);
            response.setMessage(urlFile);

        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse setAvatar(String token, MultipartFile file) {
        BaseResponse response = new BaseResponse();
        User user = userRepository.findByAccessToken(token);
        if (user != null) {
            if (file == null || file.isEmpty()) {
                response.setCode(9992);
                response.setMessage("file is invalid");
                return response;
            }
            String urlFile = saveMediaFile(file);
            user.setLinkAvatar(urlFile);
            userRepository.save(user);
            response.setCode(1000);
            response.setMessage(urlFile);

        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse setProfile(String token, String school, String address) {
        BaseResponse response = new BaseResponse();
        User user = userRepository.findByAccessToken(token);
        if (user != null) {
            if (school.isEmpty() && address.isEmpty()) {
                response.setCode(1004);
                response.setMessage("Parameter is invalid");
                return response;
            }
            user.setSchool(school.isEmpty() ? null : school);
            user.setAddress(address.isEmpty() ? null : address);
            userRepository.save(user);
            response.setCode(1000);
            response.setMessage("OK");
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public ListPeopleResponse searchByName(String token, String characters) {
        ListPeopleResponse response = new ListPeopleResponse();
        User seeker = userRepository.findByAccessToken(token);
        if (seeker != null) {
            List<DataPeopleSearch> dataList = new ArrayList<>();
            List<User> peopleSearchList = userRepository.findByNameContaining(characters);
            if (!peopleSearchList.isEmpty()) {
                Iterator<User> it = peopleSearchList.iterator();
                while (it.hasNext()) {
                    User user = it.next();
                    if (user.getId() == seeker.getId()) {
                        it.remove();
                    }
                }

                for (User people : peopleSearchList) {
                    DataPeopleSearch data = new DataPeopleSearch();
                    data.setId(people.getId());
                    data.setName(people.getName());
                    data.setAvatar(people.getLinkAvatar());
                    data.setBlock(friendService.checkIsBlock(seeker.getId(), people.getId()));
                    if (friendService.checkIsFriend(seeker.getId(), people.getId())) {
                        data.setType(1);
                    } else {
                        if (friendService.checkRequestedBySeeker(seeker.getId(), people.getId())) {
                            data.setType(2);
                        } else {
                            if (friendService.checkRequestedBySeeker(people.getId(), seeker.getId())) {
                                data.setType(3);
                            } else {
                                data.setType(0);
                            }
                        }
                    }
                    dataList.add(data);
                }
            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setData(dataList);
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse changePassword(String token, String newPass, String oldPass) {
        BaseResponse response = new BaseResponse();
        User user = userRepository.findByAccessToken(token);
        if (user != null) {
            if (newPass.matches(Regex.PASSWORD_REGEX) && oldPass.matches(Regex.PASSWORD_REGEX)) {
                if (oldPass.equals(MD5.decrypt(user.getPassword(), user.getPhone()))) {
                    if (!oldPass.equals(newPass)) {
                        user.setPassword(MD5.encrypt(newPass, user.getPhone()));
                        userRepository.save(user);
                        response.setCode(1000);
                        response.setMessage("OK");
                    } else {
                        response.setCode(9995);
                        response.setMessage("new pass = old pass");
                    }
                } else {
                    response.setCode(9996);
                    response.setMessage(" old password not matches");
                }

            } else {
                response.setCode(9997);
                response.setMessage("password invalid");
            }

        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public PeopleResponse getType(String token, int userId) {
        PeopleResponse response = new PeopleResponse();
        User seeker = userRepository.findByAccessToken(token);
        User people = userRepository.findById(userId);
        if (seeker != null) {
            if (people != null) {
                DataPeopleSearch data = new DataPeopleSearch();
                data.setId(people.getId());
                data.setName(people.getName());
                data.setAvatar(people.getLinkAvatar());
                data.setBlock(friendService.checkIsBlock(seeker.getId(), people.getId()));
                if (friendService.checkIsFriend(seeker.getId(), people.getId())) {
                    data.setType(1);
                } else {
                    if (friendService.checkRequestedBySeeker(seeker.getId(), people.getId())) {
                        data.setType(2);
                    } else {
                        if (friendService.checkRequestedBySeeker(people.getId(), seeker.getId())) {
                            data.setType(3);
                        } else {
                            data.setType(0);
                        }
                    }
                }
                response.setCode(1000);
                response.setMessage("OK");
                response.setData(data);
            } else {
                response.setCode(9995);
                response.setMessage("user is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse getBlock(String token, int userId) {
        BaseResponse response = new BaseResponse();
        User seeker = userRepository.findByAccessToken(token);
        User people = userRepository.findById(userId);
        if (seeker != null) {
            if (people != null) {
                boolean isBlock = friendService.checkIsBlock(seeker.getId(), people.getId());
                response.setCode(1000);
                response.setMessage(String.valueOf(isBlock));
            } else {
                response.setCode(9995);
                response.setMessage("user is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public AlbumResponse getAlbum(String token, int userId) {
        AlbumResponse response = new AlbumResponse();
        User getter = userRepository.findByAccessToken(token);
        User people = userRepository.findById(userId);
        if (getter != null) {
            if (people != null) {
                boolean isBlock = friendService.checkIsBlock(getter.getId(), people.getId());
                if (!isBlock) {
                    List<String> data = new ArrayList<>();
                    List<Post> postList = postRepo.findAllByPosterId(userId);
                    for (Post p : postList) {
                        if (p.getLinkImage1() != null) {
                            data.add(p.getLinkImage1());
                        }
                        if (p.getLinkImage2() != null) {
                            data.add(p.getLinkImage2());
                        }
                        if (p.getLinkImage3() != null) {
                            data.add(p.getLinkImage3());
                        }
                        if (p.getLinkImage4() != null) {
                            data.add(p.getLinkImage4());
                        }
                    }

                    response.setCode(1000);
                    response.setMessage("OK");
                    response.setData(data);
                } else {
                    response.setCode(9996);
                    response.setMessage("user was block");
                }

            } else {
                response.setCode(9995);
                response.setMessage("user is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse setName(String token, String name) {
        BaseResponse response = new BaseResponse();
        User user = userRepository.findByAccessToken(token);

        if (user != null) {
            if (name.isEmpty() || user.getName().equals(name)) {
                response.setCode(1004);
                response.setMessage("name is invalid");
            } else {
                user.setName(name);
                userRepository.save(user);
                response.setCode(1000);
                response.setMessage("OK");
            }
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    public String saveMediaFile(MultipartFile file) {

        File mediaFile = new File("src/main/upload/user/" + System.currentTimeMillis() + "_" + file.getOriginalFilename());
        FileOutputStream writer;
        try {
            writer = new FileOutputStream(mediaFile);
            mediaFile.createNewFile();
            writer.write(file.getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/upload/user/" + mediaFile.getName();
    }
}
