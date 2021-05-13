package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.example.nampt.domain.response.friend.ListFriendResponse;
import com.example.nampt.entity.Friend;
import com.example.nampt.entity.FriendRequest;
import com.example.nampt.entity.User;
import com.example.nampt.repository.FriendRepository;
import com.example.nampt.repository.FriendRequestRepository;
import com.example.nampt.repository.UserRepository;
import com.example.nampt.service.IFriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FriendService extends BaseService implements IFriendService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    FriendRepository friendRepo;

    @Autowired
    FriendRequestRepository friendRequestRepo;

    @Override
    public ListFriendResponse getFriendsList(String token, int index, int count) {
        ListFriendResponse response = new ListFriendResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            List<Integer> friendsIdList = friendRepo.findFriendIdListByUserIdPagination(user.getId(), index, count);
            Collections.sort(friendsIdList, Collections.reverseOrder());
            List<DataSingleFriend> dataSingleFriendList = new ArrayList<>();
            for (int friendId : friendsIdList) {
                DataSingleFriend dataSingleFriend = new DataSingleFriend();
                User author = userRepo.findById(friendId);

                dataSingleFriend.setId(friendId);
                dataSingleFriend.setName(author.getName());
                dataSingleFriend.setAvatar(author.getLinkAvatar());
                dataSingleFriend.setBlock(false);
                dataSingleFriendList.add(dataSingleFriend);

            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setTotalFriend(friendRepo.findFriendIdListByUserIdPagination(user.getId(), 0, 1000).size());
            response.setData(dataSingleFriendList);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public ListFriendResponse getFriendsListOnline(String token) {
        ListFriendResponse response = new ListFriendResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            List<Integer> friendsIdList = friendRepo.findFriendIdListByUserIdPagination(user.getId(), 0, 1000);
            List<DataSingleFriend> dataSingleFriendList = new ArrayList<>();
            for (int friendId : friendsIdList) {
                DataSingleFriend dataSingleFriend = new DataSingleFriend();
                User author = userRepo.findById(friendId);
                if (!author.isOnline()) {
                    continue;
                }
                boolean isBlock = checkIsBlock(user.getId(), author.getId());

                dataSingleFriend.setId(friendId);
                dataSingleFriend.setName(author.getName());
                dataSingleFriend.setAvatar(author.getLinkAvatar());
                dataSingleFriend.setBlock(isBlock);
                dataSingleFriendList.add(dataSingleFriend);
            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setTotalFriend(0);
            response.setData(dataSingleFriendList);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public BaseResponse setRequestFriend(String token, int userId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User sender = userRepo.findByAccessToken(token);
            User requestedUser = userRepo.findById(userId);
            if (requestedUser == null) {
                response.setCode(9992);
                response.setMessage("User is not exist");
                return response;
            }
            boolean isFriend = checkIsFriend(sender.getId(), userId);
            if (isFriend) {
                if (!checkIsBlock(sender.getId(), userId)) {
                    response.setCode(9994);
                    response.setMessage("2 people were friends");
                } else {
                    response.setCode(9996);
                    response.setMessage("2 people were blocks");
                }
            } else {
                if (friendRequestRepo.findByUserIdAndSenderId(requestedUser.getId(), sender.getId()) == null
                && friendRequestRepo.findByUserIdAndSenderId(sender.getId(),requestedUser.getId()) == null) {
                    FriendRequest friendRequest = new FriendRequest(requestedUser.getId(), sender.getId());
                    friendRequestRepo.save(friendRequest);
                    response.setCode(1000);
                    response.setMessage("OK");
                } else {
                    response.setCode(9995);
                    response.setMessage("Sent request");
                }

            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public BaseResponse deleteInvitationSentFriend(String token, int userId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User sender = userRepo.findByAccessToken(token);
            User receiver = userRepo.findById(userId);
            if (receiver == null) {
                response.setCode(9992);
                response.setMessage("User(Receiver) not exist");
                return response;
            }
            FriendRequest friendRequest = friendRequestRepo.findByUserIdAndSenderId(userId, sender.getId());
            if (friendRequest == null) {
                response.setCode(9992);
                response.setMessage("Friend Request not exist");
                return response;
            }
            friendRequestRepo.delete(friendRequest);
            response.setCode(1000);
            response.setMessage("OK");
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public BaseResponse setAcceptFriend(String token, int senderId, int isAccept) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User acceptUser = userRepo.findByAccessToken(token);
            User sender = userRepo.findById(senderId);
            if (sender == null) {
                response.setCode(9992);
                response.setMessage("User is not exist");
                return response;
            }
            boolean isFriend = checkIsFriend(senderId, acceptUser.getId());
            if (isFriend) {
                if (!checkIsBlock(senderId, acceptUser.getId())) {
                    response.setCode(9994);
                    response.setMessage("2 people were friends");
                } else {
                    response.setCode(9996);
                    response.setMessage("2 people were blocks");
                }
            } else {
                FriendRequest friendRequest = friendRequestRepo.findByUserIdAndSenderId(acceptUser.getId(), sender.getId());
                if (friendRequest == null) {
                    response.setCode(9992);
                    response.setMessage("Friend request is not exist");
                } else {
                    friendRequestRepo.delete(friendRequest);
                    if (isAccept != 0) {
                        Friend newFriend = new Friend(acceptUser.getId(), sender.getId());
                        friendRepo.save(newFriend);
                    }
                    response.setCode(1000);
                    response.setMessage("OK");
                }
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }

    @Override
    public ListFriendResponse getRequestFriendsList(String token) {
        ListFriendResponse response = new ListFriendResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            List<Integer> sendersIdList = friendRequestRepo.findRequestedSenderIdListByUserId(user.getId());
            List<DataSingleFriend> dataSingleFriendList = new ArrayList<>();
            for (int friendId : sendersIdList) {
                DataSingleFriend dataSingleFriend = new DataSingleFriend();
                User author = userRepo.findById(friendId);

                dataSingleFriend.setId(friendId);
                dataSingleFriend.setName(author.getName());
                dataSingleFriend.setAvatar(author.getLinkAvatar());
                dataSingleFriend.setBlock(checkIsBlock(user.getId(), friendId));
                dataSingleFriendList.add(dataSingleFriend);

            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setTotalFriend(sendersIdList.size());
            response.setData(dataSingleFriendList);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public ListFriendResponse getInvitationSentFriendList(String token) {
        ListFriendResponse response = new ListFriendResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            List<Integer> sendersIdList = friendRequestRepo.findInvitationSentIdListBySenderId(user.getId());
            List<DataSingleFriend> dataSingleFriendList = new ArrayList<>();
            for (int friendId : sendersIdList) {
                DataSingleFriend dataSingleFriend = new DataSingleFriend();
                User author = userRepo.findById(friendId);

                dataSingleFriend.setId(friendId);
                dataSingleFriend.setName(author.getName());
                dataSingleFriend.setAvatar(author.getLinkAvatar());
                dataSingleFriend.setBlock(checkIsBlock(user.getId(), friendId));
                dataSingleFriendList.add(dataSingleFriend);
            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setTotalFriend(sendersIdList.size());
            response.setData(dataSingleFriendList);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public BaseResponse deleteFriend(String token, int userId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User requester = userRepo.findByAccessToken(token);
            User partner = userRepo.findById(userId);
            if (partner == null) {
                response.setCode(9992);
                response.setMessage("User is not exist");
                return response;
            }
            boolean isFriend = checkIsFriend(userId, requester.getId());
            if (isFriend) {
                Friend friend = friendRepo.findByUserAIdAndAndUserBId(userId, requester.getId());
                if (friend == null) {
                    friend = friendRepo.findByUserAIdAndAndUserBId(requester.getId(), userId);
                }
                friendRepo.delete(friend);
            }
            response.setCode(1000);
            response.setMessage("OK");
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }

    @Override
    public BaseResponse blockUser(String token, int userId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User requester = userRepo.findByAccessToken(token);
            User partner = userRepo.findById(userId);
            if (partner == null) {
                response.setCode(9992);
                response.setMessage("User is not exist");
                return response;
            }
            boolean isFriend = checkIsFriend(userId, requester.getId());
            Friend friend;
            if (isFriend) {
                friend = friendRepo.findByUserAIdAndAndUserBId(userId, requester.getId());
                if (friend == null) {
                    friend = friendRepo.findByUserAIdAndAndUserBId(requester.getId(), userId);
                }
                friend.setBlock(true);
            } else {
                friend = new Friend();
                friend.setBlock(true);
                friend.setUserAId(userId);
                friend.setUserBId(requester.getId());
            }
            friendRepo.save(friend);
            List<FriendRequest> requests = new ArrayList<>();
            FriendRequest request1 = friendRequestRepo.findByUserIdAndSenderId(requester.getId(), partner.getId());
            FriendRequest request2 = friendRequestRepo.findByUserIdAndSenderId(partner.getId(), requester.getId());
            if (request1!=null) requests.add(request1);
            if (request2!=null) requests.add(request2);
            if (!requests.isEmpty()){
                friendRequestRepo.deleteAll(requests);
            }
            response.setCode(1000);
            response.setMessage("OK");
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }

    @Override
    public BaseResponse unBlockUser(String token, int userId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User requester = userRepo.findByAccessToken(token);
            User partner = userRepo.findById(userId);
            if (partner == null) {
                response.setCode(9992);
                response.setMessage("User is not exist");
                return response;
            }
            boolean isFriend = checkIsFriend(userId, requester.getId());
            if (isFriend) {
                Friend friend = friendRepo.findByUserAIdAndAndUserBId(userId, requester.getId());
                if (friend == null) {
                    friend = friendRepo.findByUserAIdAndAndUserBId(requester.getId(), userId);
                }
                friendRepo.delete(friend);
            }
            response.setCode(1000);
            response.setMessage("OK");
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }

    @Override
    public ListFriendResponse getBlockList(String token) {
        ListFriendResponse response = new ListFriendResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);

            List<Friend> friendList = friendRepo.findAllByUserAId(user.getId());
            friendList.addAll(friendRepo.findAllByUserBId(user.getId()));

            List<DataSingleFriend> dataList = new ArrayList<>();

            if (friendList != null && !friendList.isEmpty()) {
                Iterator<Friend> it = friendList.iterator();
                while (it.hasNext()) {
                    Friend friend = it.next();
                    if (!friend.isBlock()) {
                        it.remove();
                    }
                }
                int friendIdBlocked;
                for (Friend f : friendList) {
                    if (f.getUserAId() == user.getId()) friendIdBlocked = f.getUserBId();
                    else friendIdBlocked = f.getUserAId();
                    User friend = userRepo.findById(friendIdBlocked);
                    DataSingleFriend singleFriend = new DataSingleFriend();
                    singleFriend.setId(friend.getId());
                    singleFriend.setName(friend.getName());
                    singleFriend.setAvatar(friend.getLinkAvatar());
                    singleFriend.setBlock(f.isBlock());
                    dataList.add(singleFriend);
                }
            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setData(dataList);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }

    public boolean checkIsFriend(int userAId, int userBId) {
        boolean isFriend = false;
        Friend friend = friendRepo.findByUserAIdAndAndUserBId(userAId, userBId);
        if (friend != null) {
            isFriend = true;
        } else {
            friend = friendRepo.findByUserAIdAndAndUserBId(userBId, userAId);
            if (friend != null) {
                isFriend = true;
            }
        }
        return isFriend;
    }

    public boolean checkIsBlock(int userAId, int userBId) {
        boolean isBlock = false;
        Friend friend = friendRepo.findByUserAIdAndAndUserBId(userAId, userBId);
        if (friend != null) {
            isBlock = friend.isBlock();
        } else {
            friend = friendRepo.findByUserAIdAndAndUserBId(userBId, userAId);
            if (friend != null) {
                isBlock = friend.isBlock();
            }
        }
        return isBlock;
    }

    public boolean checkRequestedBySeeker(int seekerId, int userSearchId) {
        boolean isRequest;
        FriendRequest friend = friendRequestRepo.findByUserIdAndSenderId(userSearchId, seekerId);
        if (friend != null) {
            isRequest = true;
        } else {
            isRequest = false;
        }
        return isRequest;
    }

}
