package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.example.nampt.domain.response.notification.DataSingleNotify;
import com.example.nampt.domain.response.notification.ListNotifyResponse;
import com.example.nampt.entity.Notification;
import com.example.nampt.entity.Participate;
import com.example.nampt.entity.User;
import com.example.nampt.repository.*;
import com.example.nampt.service.IDeviceService;
import com.example.nampt.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class NotificationService implements INotificationService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    FriendRepository friendRepo;

    @Autowired
    ParticipateRepository participateRepo;

    @Autowired
    NotificationRepository notificationRepo;


    @Autowired
    FriendService friendService;

    @Autowired
    IDeviceService deviceService;

    @Override
    public BaseResponse setNotify(String token, int type, int partnerId, int roomId,String content) {
        BaseResponse response = new BaseResponse();
        User sender = userRepo.findByAccessToken(token);
        if (sender != null) {
            switch (type) {
                case 1: // new post
                    //  partnerId chinh la postId
                    List<Integer> mFriendIdList = friendRepo.findFriendIdListByUserIdPagination(sender.getId(), 0, 1000);
                    if (!mFriendIdList.isEmpty()) {
                        int postID = partnerId;
                        for (int friendId : mFriendIdList) {
                            saveNotify(type, friendId, sender.getId(), postID);
                        }
                        deviceService.sendMessage(sender.getId(), mFriendIdList, 1, postID,null);
                    }

                    break;
                case 2: // gửi lời mời kb
                    User partner = userRepo.findById(partnerId);
                    if (partner != null) {
                        saveNotify(type, partnerId, sender.getId(), sender.getId());
                        List<Integer> receiverIds = new ArrayList<>();
                        receiverIds.add(partnerId);
                        deviceService.sendMessage(sender.getId(), receiverIds, 2, sender.getId(),null);
                    }
                    break;
                case 3: // chat đơn
                    if (roomId == 0 && partnerId != 0) {
                        User partners = userRepo.findById(partnerId);
                        if (partners != null) {
                            List<Integer> receiverIds = new ArrayList<>();
                            receiverIds.add(partnerId);
                            deviceService.sendMessage(sender.getId(), receiverIds, 3, sender.getId(),content);
                        }
                    }
                    break;
                case 4: // chat nhóm
                    if (roomId != 0 && partnerId == 0) {
                        List<Participate> participateList = participateRepo.findAllByConversationId(roomId);
                        if (!participateList.isEmpty()) {
                            List<Integer> partnerIds = new ArrayList<>();

                            for (Participate p : participateList) {
                                if (p.getUserId() != sender.getId()) {
                                    partnerIds.add(p.getUserId());
                                }
                            }

                            deviceService.sendMessage(sender.getId(), partnerIds, 4, roomId,content);
                        }
                    }
                    break;
            }

            response.setCode(1000);
            response.setMessage("OK");

        } else {
            response.setCode(9993);
            response.setMessage("Token invalid");
        }
        return response;
    }

    private void saveNotify(int type, int partnerId, int senderId, int dataId) {
        Notification notification = new Notification();
        notification.setUserId(partnerId);
        notification.setSenderId(senderId);
        notification.setDataId(dataId);
        notification.setSeen(false);
        notification.setType(type);
        notification.setCreateTime(new Date());
        notificationRepo.save(notification);
    }

    @Override
    public BaseResponse setSeen(String token, int notifyId) {
        BaseResponse response = new BaseResponse();
        User me = userRepo.findByAccessToken(token);
        if (me != null) {
            Notification notification = notificationRepo.findById(notifyId);
            if (notification != null) {
                if (!notification.isSeen()) {
                    notification.setSeen(true);
                }
                notificationRepo.save(notification);

                response.setCode(1000);
                response.setMessage("OK");
            } else {
                response.setCode(9995);
                response.setMessage("Notify not exist");
            }

        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse delNotification(String token, int notifyId) {
        BaseResponse response = new BaseResponse();
        User me = userRepo.findByAccessToken(token);
        if (me != null) {
            Notification notification = notificationRepo.findById(notifyId);
            if (notification != null) {
                notificationRepo.delete(notification);
                response.setCode(1000);
                response.setMessage("OK");
            } else {
                response.setCode(9995);
                response.setMessage("Notify not exist");
            }

        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public ListNotifyResponse getListNotification(String token) {
        ListNotifyResponse response = new ListNotifyResponse();
        User me = userRepo.findByAccessToken(token);
        if (me != null) {
            List<Notification> notifications = notificationRepo.findAllByUserId(me.getId());
            List<DataSingleNotify> dataList = new ArrayList<>();

            for (Notification n : notifications) {
                User sender = userRepo.findById(n.getSenderId());
                DataSingleFriend friend = new DataSingleFriend();

                friend.setId(sender.getId());
                friend.setAvatar(sender.getLinkAvatar());
                friend.setName(sender.getName());
                friend.setBlock(friendService.checkIsBlock(n.getUserId(), n.getSenderId()));

                DataSingleNotify dataSingle = new DataSingleNotify();
                dataSingle.setId(n.getId());
                dataSingle.setSender(friend);
                dataSingle.setType(n.getType());
                dataSingle.setSeen(n.isSeen());
                dataSingle.setDataId(n.getDataId());
                dataSingle.setCreateTime(n.getCreateTime());

                dataList.add(dataSingle);
            }

            response.setData(dataList);
            response.setCode(1000);
            response.setMessage("OK");
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }

    @Override
    public BaseResponse getTotalNotification(String token) {
        BaseResponse response = new BaseResponse();
        User me = userRepo.findByAccessToken(token);
        if (me != null) {
            List<Notification> notifications = notificationRepo.findAllByUserId(me.getId());
            Iterator<Notification> it = notifications.iterator();
            while (it.hasNext()) {
                Notification n = it.next();
                if (n.isSeen())
                    it.remove();
            }
            response.setCode(1000);
            response.setMessage(notifications.size() + "");
        } else {
            response.setCode(9993);
            response.setMessage("token is invalid");
        }
        return response;
    }
}
