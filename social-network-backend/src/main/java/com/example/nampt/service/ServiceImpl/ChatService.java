package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.chat.*;
import com.example.nampt.domain.response.friend.DataSingleFriend;
import com.example.nampt.entity.*;
import com.example.nampt.repository.*;
import com.example.nampt.service.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService extends BaseService implements IChatService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    FriendRepository friendRepo;

    @Autowired
    MessageRepository messageRepo;


    @Autowired
    ParticipateRepository participateRepo;
    @Autowired
    ConversationRepository conversationRepo;

    @Override
    public SingleChatResponse setMessageSingle(String token, String content, int partnerId,Date createTime) {
        SingleChatResponse response = new SingleChatResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            User partner = userRepo.findById(partnerId);
            if (partner == null) {
                response.setCode(9992);
                response.setMessage("Partner is not exist");
            } else {
                if (user.getId() == partnerId) {
                    response.setCode(9995);
                    response.setMessage("you send to yourself");
                    return response;
                }
                if (content.length() < 255) {
                    int roomId = handleMessageChatSingle(user.getId(), partnerId, content,createTime);
                    response.setCode(1000);
                    response.setMessage(String.valueOf(roomId));
                    response.setSenderId(user.getId());
                    response.setNewContent(content);
                } else {
                    response.setCode(1004);
                    response.setMessage("Parameter value is invalid");
                }
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }

        return response;
    }

    @Override
    public GroupChatResponse createGroupChat(String token, List<Integer> partnerIdList, String nameGroup,Date createTime) {
        GroupChatResponse response = new GroupChatResponse();
        if (checkToken(token)) {
            if (partnerIdList == null | partnerIdList.isEmpty()) {
                response.setCode(9992);
                response.setMessage("some partner is not exist");
                return response;
            }
            User user = userRepo.findByAccessToken(token);
            for (int id : partnerIdList) {
                if (userRepo.findById(id) == null | id == user.getId()) {
                    response.setCode(9992);
                    response.setMessage("some partner is not exist or invalid");
                    return response;
                }
            }
            partnerIdList.add(user.getId());
            partnerIdList = new ArrayList<>(new HashSet<>(partnerIdList));

            System.out.println(partnerIdList.toString());
            List<Conversation> conversations = new LinkedList<>();
            for (int id : partnerIdList) {
                conversations.addAll(conversationRepo.findAllByCreatorId(id));
            }
            boolean isConversationExist = false;

            if (conversations.isEmpty()) {
                isConversationExist = false;
            } else {
                for (Conversation conversation : conversations) {
                    List<Participate> participates = participateRepo.findAllByConversationId(conversation.getId());
                    if (!checkConversationExist(participates, partnerIdList)) {
                        isConversationExist = false;
                    } else {
                        if (conversation.getName().equals(nameGroup)) {
                            isConversationExist = true;
                            response.setCode(9995);
                            response.setMessage("Group is duplicate name");
                            break;
                        } else {
                            isConversationExist = false;
                        }
                    }
                }
            }
            if (!isConversationExist) {
                Set<Integer> partnerIdSet = new HashSet<>(partnerIdList);
                partnerIdSet.remove(user.getId());

                int idNewConversation = saveDataNewConversation(user.getId(), partnerIdSet, nameGroup, true,createTime);
                messageRepo.save(new Message(idNewConversation, user.getId(), "\uD83D\uDCE3\uD83D\uDCE3 nhóm mới, zô nào !", createTime));

                response.setCode(1000);
                response.setMessage("OK");
                response.setConversationId(idNewConversation);

            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }
        return response;
    }

    @Override
    public BaseResponse sendMessageGroup(String token, int conversationId, String content,Date createTime) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User sender = userRepo.findByAccessToken(token);
            Conversation conversation = conversationRepo.findById(conversationId);
            if (conversation != null) {
                boolean isSenderExist = false;
                List<Participate> participates = participateRepo.findAllByConversationId(conversationId);
                for (Participate participate : participates) {
                    if (participate.getUserId() == sender.getId()) {
                        isSenderExist = true;
                        break;
                    }
                }
                if (isSenderExist) {
                    messageRepo.save(new Message(conversation.getId(), sender.getId(), content, createTime));
                    setSeenForAllPeopleInRoom(participates, sender.getId());
                    response.setCode(1000);
                    response.setMessage("OK");
                } else {
                    response.setCode(9992);
                    response.setMessage("User is not exist in conversation");
                }
            } else {
                response.setCode(9992);
                response.setMessage("Conversation is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }
        return response;
    }

    private void setSeenForAllPeopleInRoom(List<Participate> participates, int senderId) {
        for (Participate part : participates) {
            if (part.getUserId() == senderId) {
                part.setSeen(true);
            } else {
                part.setSeen(false);
            }
            participateRepo.save(part);
        }
    }

    @Override
    public ListConversationResponse getListConversation(String token) {
        ListConversationResponse response = new ListConversationResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            List<Participate> participates = participateRepo.findAllByUserId(user.getId());

            List<DataSingleConversation> data = new ArrayList<>();
            if (!participates.isEmpty()) {

                for (Participate participate : participates) {
                    Conversation conversation = conversationRepo.findById(participate.getConversationId());
                    DataSingleConversation singleConversation = new DataSingleConversation();

                    singleConversation.setId(conversation.getId());
                    singleConversation.setName(conversation.getName());
                    singleConversation.setSeen(participate.isSeen());

                    Message message = messageRepo.getLastMessage(conversation.getId());
                    if (message != null) {
                        singleConversation.setCreateTimeLastMessage(message.getCreateTime());
                        singleConversation.setLastMessage(message.getContent());
                    } else {
                        singleConversation.setCreateTimeLastMessage(null);
                        singleConversation.setLastMessage(null);
                    }
                    List<Participate> participateList = participateRepo.findAllByConversationId(conversation.getId());
                    List<DataSingleFriend> partners = new ArrayList<>();

                    for (Participate part : participateList) {

                        User participant = userRepo.findById(part.getUserId());

                        boolean isBlock = checkIsBlock(user.getId(), participant.getId());
                        DataSingleFriend partner = new DataSingleFriend();
                        partner.setId(participant.getId());
                        partner.setName(participant.getName());
                        partner.setAvatar(participant.getLinkAvatar());
                        partner.setBlock(isBlock);
                        partners.add(partner);
                    }
                    singleConversation.setPartners(partners);
                    data.add(singleConversation);
                }
            }
            response.setCode(1000);
            response.setMessage("OK");
            response.setData(data);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }

        return response;
    }

    @Override
    public ListMessageResponse getConversation(String token, int idRoom) {
        ListMessageResponse response = new ListMessageResponse();
        if (checkToken(token)) {
            User getter = userRepo.findByAccessToken(token);
            Conversation conversation = conversationRepo.findById(idRoom);
            if (conversation != null) {
                List<Message> messageList = messageRepo.findAllByConversationId(idRoom);
                List<DataSingleMessage> dataResponse = new ArrayList<>();
                for (Message message : messageList) {
                    DataSingleFriend friend = new DataSingleFriend();
                    User sender = userRepo.findById(message.getSenderId());

                    friend.setId(sender.getId());
                    friend.setName(sender.getName());
                    friend.setAvatar(sender.getLinkAvatar());
                    friend.setBlock(checkIsBlock(getter.getId(), sender.getId()));

                    DataSingleMessage singleMessage = new DataSingleMessage();
                    singleMessage.setSender(friend);
                    singleMessage.setContent(message.getContent());
                    singleMessage.setCreateTime(message.getCreateTime());

                    dataResponse.add(singleMessage);
                }
                response.setCode(1000);
                response.setMessage("OK");
                response.setData(dataResponse);
            } else {
                response.setCode(9992);
                response.setMessage("Conversation is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }
        return response;
    }

    @Override
    public ListMessageResponse getConversationSingle(String token, int partnerId) {
        ListMessageResponse response = new ListMessageResponse();
        if (checkToken(token)) {
            User getter = userRepo.findByAccessToken(token);
            User partner = userRepo.findById(partnerId);
            if (partner != null) {
                if (getter.getId() == partnerId) {
                    response.setCode(9995);
                    response.setMessage("you send to yourself");
                    return response;
                }
                int idRoomIfExist = -1;

                List<Conversation> conversations = conversationRepo.findAllByCreatorId(getter.getId());
                conversations.addAll(conversationRepo.findAllByCreatorId(partnerId));

                List<Integer> participantIdList = new ArrayList<>();
                participantIdList.add(getter.getId());
                participantIdList.add(partnerId);

                if (!conversations.isEmpty()) {
                    for (Conversation conversation : conversations) {
                        List<Participate> participates = participateRepo.findAllByConversationId(conversation.getId());
                        if (checkConversationExist(participates, participantIdList)) {
                            idRoomIfExist = conversation.getId();
                            break;
                        }
                    }
                }
                ListMessageResponse response1 = getConversation(token, idRoomIfExist);
                return response1;
            } else {
                response.setCode(9992);
                response.setMessage("Partner is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }
        return response;
    }

    @Override
    public ListConversationResponse searchByName(String token, String name) {

        ListConversationResponse response = getListConversation(token);
        if (response.getData() != null) {
            List<DataSingleConversation> dataList = new ArrayList<>();
            for (DataSingleConversation data : response.getData()) {
                if (data.getName() != null && data.getName().contains(name)) {
                    dataList.add(data);
                }
            }
            response.setData(dataList);
        }
        return response;
    }

    @Override
    public BaseResponse setSeenByRoomId(String token, int roomId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            Conversation room = conversationRepo.findById(roomId);
            if (room != null) {
                List<Participate> participates = participateRepo.findAllByConversationId(roomId);

                for (Participate p : participates) {
                    if (p.getUserId() == userRepo.findByAccessToken(token).getId()) {
                        p.setSeen(true);
                        participateRepo.save(p);
                        break;
                    }
                }
                response.setCode(1000);
                response.setMessage("OK");

            } else {
                response.setCode(9995);
                response.setMessage("Room is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }
        return response;
    }

    @Override
    public BaseResponse setSeenSingle(String token, int partnerId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User seener = userRepo.findByAccessToken(token);
            User partner = userRepo.findById(partnerId);
            if (partner != null) {
                List<Participate> participates = participateRepo.findAllByUserId(seener.getId());
                if (!participates.isEmpty()) {
                    for (Participate p : participates) {
                        List<Participate> participateList = participateRepo.findAllByConversationId(p.getConversationId());
                        if (participateList.size() == 2) {
                            if (participateList.get(0).getUserId() == seener.getId() &&
                                    participateList.get(1).getUserId() == partnerId) {
                                Participate mChat = participateList.get(0);
                                mChat.setSeen(true);
                                participateRepo.save(mChat);
                                break;
                            }
                            if (participateList.get(1).getUserId() == seener.getId() &&
                                    participateList.get(0).getUserId() == partnerId) {
                                Participate mChat = participateList.get(1);
                                mChat.setSeen(true);
                                participateRepo.save(mChat);
                                break;
                            }
                        }
                    }
                    response.setCode(1000);
                    response.setMessage("OK");
                } else {
                    response.setCode(9995);
                    response.setMessage("User is not chat");
                }
            } else {
                response.setCode(9995);
                response.setMessage("Partner is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is not exist");
        }
        return response;
    }

    private int handleMessageChatSingle(int senderId, int partnerId, String content,Date createTime) {
        List<Conversation> conversations = conversationRepo.findAllByCreatorId(senderId);
        conversations.addAll(conversationRepo.findAllByCreatorId(partnerId));
        List<Integer> participantIdList = new ArrayList<>();
        participantIdList.add(senderId);
        participantIdList.add(partnerId);
        int idRoom = -1;
        boolean isConversationExist = false;
        if (conversations.isEmpty()) {
            isConversationExist = false;
        } else {
            for (Conversation conversation : conversations) {
                List<Participate> participates = participateRepo.findAllByConversationId(conversation.getId());
                if (!checkConversationExist(participates, participantIdList)) {
                    isConversationExist = false;
                } else {
                    isConversationExist = true;
                    messageRepo.save(new Message(conversation.getId(), senderId, content, createTime));
                    setSeenForAllPeopleInRoom(participates, senderId);
                    idRoom = conversation.getId();
                    break;
                }
            }
        }
        if (!isConversationExist) {
            Set<Integer> partnerIdSet = new HashSet<>(participantIdList);
            partnerIdSet.remove(senderId);
            int idNewConversation = saveDataNewConversation(senderId, partnerIdSet, null, false,createTime);
            messageRepo.save(new Message(idNewConversation, senderId, content, new Date()));
            idRoom = idNewConversation;
        }

        return idRoom;
    }

    private int saveDataNewConversation(int creatorId, Set<Integer> partnerIdSet, String nameGroup, boolean isGroup,Date createTime) {
        // tạo cuộc trò chuyện mới
        Conversation conversationSaved = conversationRepo.save(new Conversation(nameGroup, creatorId, createTime));
        // tạo phòng chat mới n người

        participateRepo.save(new Participate(conversationSaved.getId(), creatorId, true));

        for (int partnerId : partnerIdSet) {
            participateRepo.save(new Participate(conversationSaved.getId(), partnerId, false));
        }
        return conversationSaved.getId();
    }

    private boolean checkConversationExist(List<Participate> participates, List<Integer> userIdList) {
        List<Integer> integerList = new ArrayList<>();
        for (Participate participate : participates) {
            integerList.add(participate.getUserId());
        }
        Collections.sort(integerList);
        Collections.sort(userIdList);

        if (integerList.equals(userIdList)) {
            return true;
        }
        return false;
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
}
