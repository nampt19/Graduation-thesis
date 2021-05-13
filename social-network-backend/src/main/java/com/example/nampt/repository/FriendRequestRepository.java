package com.example.nampt.repository;

import com.example.nampt.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest,Integer> {
    FriendRequest findByUserIdAndSenderId(int userId,int senderId);

    @Query(value = "select sender_id from friend_request where user_id = (:id)", nativeQuery = true)
    List<Integer> findRequestedSenderIdListByUserId(@Param("id") int userId);

    @Query(value = "select user_id from friend_request where sender_id = (:id)", nativeQuery = true)
    List<Integer> findInvitationSentIdListBySenderId(@Param("id") int senderId);
}
