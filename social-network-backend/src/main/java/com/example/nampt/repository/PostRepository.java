package com.example.nampt.repository;

import com.example.nampt.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findById(int id);

    @Query(value = "select id from post where poster_id in (:userIdList)\n" +
            "order by post.create_time desc \n" +
            "limit :index,:count ",nativeQuery = true)
    List<Integer> findPostsIdByUsersId(@Param("userIdList") List<Integer> idList,@Param("index") int index,@Param("count") int count);


    @Query(value = "select id from post where poster_id in (:user_id_list) and id<=(:last_id)\n" +
            "order by post.create_time desc \n" +
            "limit :index,:count ",nativeQuery = true)
    List<Integer> findPostsIdByUsersIdAndLastPostId(@Param("user_id_list") List<Integer> idList,
                                                    @Param("index") int index,
                                                    @Param("count") int count,
                                                    @Param("last_id") int lastId);
    List<Post> findAllByPosterId(int poserId);

}
