package com.example.nampt.service;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.chat.GroupChatResponse;
import com.example.nampt.domain.response.chat.ListConversationResponse;
import com.example.nampt.domain.response.chat.ListMessageResponse;
import com.example.nampt.domain.response.chat.SingleChatResponse;

import java.util.Date;
import java.util.List;

public interface IChatService {
    SingleChatResponse setMessageSingle(String token, String content, int partnerId,Date createTime);

    GroupChatResponse createGroupChat(String token, List<Integer> partnerIdList, String nameGroup, Date createTime);

    BaseResponse sendMessageGroup(String token, int conversationId, String content,Date createTime);

    ListConversationResponse getListConversation(String token);

    ListMessageResponse getConversation(String token, int idRoom);

    ListMessageResponse getConversationSingle(String token, int partnerId);

    ListConversationResponse searchByName(String token, String name);

    BaseResponse setSeenByRoomId(String token,int roomId);

    BaseResponse setSeenSingle(String token, int partnerId);
}
