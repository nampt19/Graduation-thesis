package com.example.nampt.socket;

import com.example.nampt.domain.response.BaseResponse;
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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketHandlerChatGroup extends AbstractWebSocketHandler {
    ChatService chatService;

    UserRepository userRepo;

    List<SessionData> sessionList;

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public WebSocketHandlerChatGroup() {
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
        int roomId = Integer.parseInt(session.getHandshakeHeaders().get("Room-Id").get(0));
        User user = userRepo.findByAccessToken(token);

        DataMessageSend messageData = gson.fromJson(message.getPayload(), DataMessageSend.class);


        BaseResponse response = chatService.sendMessageGroup(token,
                roomId,
                messageData.getContent(), messageData.getCreateTime());
        switch (response.getCode()) {
            case 1000:
                sendMsgServerToClientBySocket(roomId, user, messageData.getContent(), messageData.getCreateTime());
                break;
            default:
                System.out.println(response.getCode() + response.getMessage());
                break;}


    }

    private void sendMsgServerToClientBySocket(int roomId, User user, String msg, Date createTime) throws IOException {

        DataSingleMessage message = new DataSingleMessage();
        DataSingleFriend sender = new DataSingleFriend();

        sender.setId(user.getId());
        sender.setName(user.getName());
        sender.setAvatar(user.getLinkAvatar());

        message.setSender(sender);
        message.setContent(msg);
        message.setCreateTime(createTime);

        ResponseData responseData = new ResponseData();
        responseData.setIdRoom(roomId);
        responseData.setMessage(message);
        for (SessionData data : sessionList) {
            if (data.getId() == roomId) {
                data.getSession().sendMessage(new TextMessage(gson.toJson(responseData)));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String token = session.getHandshakeHeaders().get("Authorization").get(0);
        String roomId = session.getHandshakeHeaders().get("Room-Id").get(0);

        User user = userRepo.findByAccessToken(token);
        SessionData sessionData;
        if (user != null) {
            sessionData = new SessionData(Integer.parseInt(roomId), session);
            sessionList.add(sessionData);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        for (SessionData data : sessionList) {
            if (data.getSession() == session) {
                System.out.println("closed " + session.toString());
                sessionList.remove(data);
                break;
            }
        }
    }
}
