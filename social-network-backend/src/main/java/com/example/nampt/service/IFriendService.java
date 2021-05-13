package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.friend.ListFriendResponse;

public interface IFriendService {
    ListFriendResponse getFriendsList(String token, int index, int count);

    ListFriendResponse getFriendsListOnline(String token);

    BaseResponse setRequestFriend(String token, int userId);

    BaseResponse deleteInvitationSentFriend(String token, int userId);


    BaseResponse setAcceptFriend(String token, int userId, int isAccept);


    ListFriendResponse getRequestFriendsList(String token);

    ListFriendResponse getInvitationSentFriendList(String token);

    BaseResponse deleteFriend(String token, int userId);

    BaseResponse blockUser(String token, int userId);

    BaseResponse unBlockUser(String token, int userId);

    ListFriendResponse getBlockList(String token);
}
