package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.entity.Conversation;
import com.example.nampt.entity.Device;
import com.example.nampt.entity.User;
import com.example.nampt.repository.ConversationRepository;
import com.example.nampt.repository.DeviceRepository;
import com.example.nampt.repository.UserRepository;
import com.example.nampt.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService extends BaseService implements IDeviceService {

    @Autowired
    DeviceRepository deviceRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ConversationRepository conversationRepo;


    @Override
    public BaseResponse saveToken(String token, int userId, String deviceToken) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            Device device = deviceRepo.findByDeviceToken(deviceToken);
            List<Device> deviceOld = deviceRepo.findAllByUserId(userId);
            if (!deviceOld.isEmpty()) {
                deviceRepo.deleteAll(deviceOld);
            }
            Device newDevice;
            if (device == null) {
                newDevice = new Device();
                newDevice.setUserId(userId);
                newDevice.setDeviceToken(deviceToken);
                newDevice.setCreateTime(new Date());
                deviceRepo.save(newDevice);
            } else {
                device.setUserId(userId);
                deviceRepo.save(device);
            }
            response.setCode(1000);
            response.setMessage("OK");

        } else {
            response.setCode(9993);
            response.setMessage("token  is invalid");
        }
        return response;
    }

    @Override
    public String sendMessage(int senderId, List<Integer> receiverIds, int typeNotify, int dataId, String contentChat) {
        User sender = userRepo.findById(senderId);
        if (sender != null) {
            String title = sender.getName();
            String body = "";
            String urlIcon = sender.getLinkAvatar();
            switch (typeNotify) {
                case 1:// new post
                    body = sender.getName() + " ????ng b??i vi???t m???i";
                    break;
                case 2:// g???i l???i m???i kb
                    body = sender.getName() + " g???i l???i m???i k???t b???n";
                    break;
                case 3:// chat ????n
                    body = sender.getName() + ": " + contentChat;
                    break;
                case 4:// chat nhoms
                    Conversation conversation = conversationRepo.findById(dataId);
                    title = "Nh??m: " + conversation.getName();
                    body = sender.getName() + ": " + contentChat;
                    break;
            }

            List<Device> deviceTokens = new ArrayList<>();
            for (int id : receiverIds) {
                deviceTokens.addAll(deviceRepo.findAllByUserId(id));
            }
            if (!deviceTokens.isEmpty()) {
                String[] tokenArray = deviceTokens.stream().map(p -> p.getDeviceToken())
                        .collect(Collectors.toList())
                        .toArray(new String[0]);

                String SERVER_KEY = "AAAA7nhB1Hw:APA91bGT6RAT3eRPMUSSbWfaFBLER6_1WLwArY-Zabjy3zRwkaQigLaS0ybVPHkEMhGJ4vqLEx2UVSetAQPgRqGu4myje7EqTOzQh6OJWuvSpOVmhvXL0M50ii-Ebh3vr-K8RRqE5RSw";
                String SENDER_ID = "1024219796604";
                String postData = "{ \"registration_ids\": [ \"" + String.join("\",\"", tokenArray) + "\" ]," +
                        "\"data\": {" +
                        "\"body\": \"" + body + "\"," +
                        "\"title\": \"" + title + "\"," +
                        "\"icon\": \"" + urlIcon + "\"," +
                        "\"data_id\": " + dataId + "," +
                        "\"type_notify\": " + typeNotify + "" +
                        "}}";

                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();       // m??? k???t n???i
                    connection.setRequestMethod("POST");                                         // lo???i request
                    connection.setRequestProperty("Authorization", "key=" + SERVER_KEY);            //requestHeader
                    connection.setRequestProperty("Sender", "id=" + SENDER_ID);            //requestHeader
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setDoOutput(true);                // true ????? ghi d??? li???u json body v??o connection

                    OutputStream os = connection.getOutputStream();
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);


                    StringBuilder strResponse = new StringBuilder();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8")
                    );
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        strResponse.append(responseLine);
                    }

                    return strResponse.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


}
