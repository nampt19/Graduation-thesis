package com.example.nampt.repository;

import com.example.nampt.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

    Friend findByUserAIdAndAndUserBId(int Aid, int Bid);


    @Query(value = "(select user_B_id as friendId from friend where user_A_id=(:id) and is_block=0)\n" +
            "union\n" +
            "(select user_A_id as friendId from friend where user_B_id=(:id) and is_block=0)\n" +
            "limit :index,:count", nativeQuery = true)
    List<Integer> findFriendIdListByUserIdPagination(@Param("id") int userId,
                                                     @Param("index") int index,
                                                     @Param("count") int count);

    List<Friend> findAllByUserBId(int Bid);

    List<Friend> findAllByUserAId(int Aid);
}
