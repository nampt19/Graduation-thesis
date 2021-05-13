package com.example.nampt.socket;

import com.example.nampt.domain.response.chat.DataSingleMessage;
import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.example.nampt.entity.User;
import com.example.nampt.repository.UserRepository;
import com.example.nampt.service.ServiceImpl.ChatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketHandlerCreateGroup extends AbstractWebSocketHandler {
    ChatService chatService;

    UserRepository userRepo;

    List<SessionData> sessionList;
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    public WebSocketHandlerCreateGroup() {
        sessionList = new CopyOnWriteArrayList<>();

        chatService = (ChatService) SpringConfiguration.contextProvider()
                .getApplicationContext().getBean("chatService");

        userRepo = (UserRepository) SpringConfiguration.contextProvider()
                .getApplicationContext().getBean("userRepository");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String token = session.getHandshakeHeaders().get("Authorization").get(0);
        User user = userRepo.findByAccessToken(token);

        DataNewGroupRequest request = gson.fromJson(message.getPayload(), DataNewGroupRequest.class);

        List<Integer> partnerIds = request.getPartnerIds();
        int roomId = request.getRoomId();
        String roomName = request.getRoomName();
        Date createTime = request.getCreateTime();

        DataSingleMessage messageRes = new DataSingleMessage();
        DataSingleFriend sender = new DataSingleFriend();

        List<DataSingleFriend> dataPartners = new ArrayList<>();
        for (int partnerId : partnerIds) {
            User partner = userRepo.findById(partnerId);
            dataPartners.add(new DataSingleFriend(partner.getId(),
                    partner.getName(),
                    partner.getLinkAvatar(),
                    false));
        }

        sender.setId(user.getId());
        sender.setName(user.getName());
        sender.setAvatar(user.getLinkAvatar());
        messageRes.setSender(sender);
        messageRes.setPartners(dataPartners);

        messageRes.setContent("\uD83D\uDCE3\uD83D\uDCE3 nhóm mới, zô nào ! ✨✨");
        messageRes.setCreateTime(createTime);

        ResponseData responseData = new ResponseData();
        responseData.setIdRoom(roomId);
        responseData.setRoomName(roomName);
        responseData.setMessage(messageRes);

        for (SessionData data : sessionList) {
            for (int partnerId : partnerIds) {
                if (data.getId() == partnerId) {
                    gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    data.getSession().sendMessage(new TextMessage(
                            gson.
                            toJson(responseData)));
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String token = session.getHandshakeHeaders().get("Authorization").get(0);
        User user = userRepo.findByAccessToken(token);
        SessionData sessionData;
        if (user != null) {
            sessionData = new SessionData(user.getId(), session);
            sessionList.add(sessionData);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        for (SessionData data : sessionList) {
            if (data.getSession() == session) {
                sessionList.remove(data);
                break;
            }
        }
    }
}
