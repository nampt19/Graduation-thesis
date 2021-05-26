package com.example.nampt.socket;

import com.example.nampt.domain.request.SingleChatRequest;
import com.example.nampt.domain.response.chat.DataSingleMessage;
import com.example.nampt.domain.response.chat.SingleChatResponse;
import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.example.nampt.entity.User;
import com.example.nampt.repository.FriendRepository;
import com.example.nampt.repository.UserRepository;
import com.example.nampt.service.ServiceImpl.ChatService;
import com.example.nampt.service.ServiceImpl.FriendService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class WebSocketHandlerChatSingle extends AbstractWebSocketHandler {


    ChatService chatService;

    UserRepository userRepo;

    FriendService friendService;

    List<SessionData> sessionList;

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    public WebSocketHandlerChatSingle() {
        sessionList = new CopyOnWriteArrayList<>();

        chatService = SpringConfiguration.contextProvider()
                .getApplicationContext().getBean(ChatService.class);

        userRepo = SpringConfiguration.contextProvider()
                .getApplicationContext().getBean(UserRepository.class);

        friendService = SpringConfiguration.contextProvider()
                .getApplicationContext().getBean(FriendService.class);

        System.out.println();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String token = session.getHandshakeHeaders().get("Authorization").get(0);
        User sender = userRepo.findByAccessToken(token);

        SingleChatRequest request = gson.fromJson(message.getPayload(), SingleChatRequest.class);
        User partner = userRepo.findById(request.getPartnerId());

        if (friendService.checkIsBlock(sender.getId(), partner.getId())) {
            for (SessionData data : sessionList) {
                if (data.getId() == sender.getId()) {
                    System.out.println("user block");
                    data.getSession().sendMessage(new TextMessage("2peopleblocked"));
                }
            }
            return;
        }

        SingleChatResponse response = chatService.setMessageSingle(token,
                request.getContent(),
                request.getPartnerId(),
                request.getCreateTime());
        switch (response.getCode()) {
            case 1000:
                sendMsgServerToClient(partner, sender,
                        Integer.parseInt(response.getMessage()),
                        request.getContent(), request.getCreateTime());
                break;
            default:
                System.out.println(response.getCode() + response.getMessage());
                break;
        }

    }

    private void sendMsgServerToClient(User partner, User sender, int roomId, String msg, Date createTime) throws IOException {
        DataSingleMessage message = new DataSingleMessage();
        DataSingleFriend dataSender = new DataSingleFriend();

        List<DataSingleFriend> dataPartners = new ArrayList<>();
        dataPartners.add(new DataSingleFriend(partner.getId(),
                partner.getName(),
                partner.getLinkAvatar(),
                false));

        dataSender.setId(sender.getId());
        dataSender.setName(sender.getName());
        dataSender.setAvatar(sender.getLinkAvatar());

        message.setSender(dataSender);
        message.setPartners(dataPartners);
        message.setContent(msg);
        message.setCreateTime(createTime);

        ResponseData responseData = new ResponseData();
        responseData.setIdRoom(roomId);
        responseData.setMessage(message);

        for (SessionData data : sessionList) {
            if (data.getId() == partner.getId() || data.getId() == sender.getId()) {
                data.getSession().sendMessage(new TextMessage(gson.toJson(responseData)));

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
                System.out.println("closed " + session.toString());
                sessionList.remove(data);
                break;
            }
        }

    }


}
