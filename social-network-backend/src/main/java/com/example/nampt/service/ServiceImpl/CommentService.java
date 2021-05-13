package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.comment.ListCommentResponse;
import com.example.nampt.domain.response.comment.SingleCommentResponse;
import com.example.nampt.domain.response.comment.data.DataSingleComment;
import com.example.nampt.domain.response.post.data.Author;
import com.example.nampt.entity.*;
import com.example.nampt.repository.*;
import com.example.nampt.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class CommentService extends BaseService implements ICommentService {

    @Autowired
    PostRepository postRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    LikeRepository likeRepo;
    @Autowired
    CommentRepository commentRepo;
    @Autowired
    HiddenRepository hiddenRepo;

    @Autowired
    FriendRepository friendRepo;

    @Override
    public SingleCommentResponse setComment(String token, int idPost, String comment, Date createTime) {
        SingleCommentResponse response = new SingleCommentResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            Post post = postRepo.findById(idPost);
            if (post != null) {
                if (comment != null && (comment.length() > 0 && comment.length() < 500)) {
                    Comment newComment = new Comment();
                    newComment.setPostId(post.getId());
                    newComment.setContent(comment);
                    newComment.setSenderId(user.getId());
                    newComment.setCreateTime(createTime);
                    Comment savedComment = commentRepo.save(newComment);

                    DataSingleComment data = new DataSingleComment();
                    data.setComment(savedComment);
                    data.setAuthor(new Author(user.getId(), user.getName(), user.getLinkAvatar()));
                    data.setBlock(false);

                    response.setData(data);
                    response.setCode(1000);
                    response.setMessage("OK");
                } else {
                    response.setCode(1004);
                    response.setMessage("Parameter value is invalid(content empty or >500 words)");
                }
            } else {
                response.setCode(9992);
                response.setMessage("Post is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }

    @Override
    public BaseResponse deleteComment(String token, int idComment) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            Comment comment = commentRepo.findById(idComment);
            if (comment != null) {
                commentRepo.delete(comment);
                response.setCode(1000);
                response.setMessage("OK");
            } else {
                response.setCode(9992);
                response.setMessage("Comment is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public ListCommentResponse getListComment(String token, int idPost) {
        ListCommentResponse response = new ListCommentResponse();
        if (checkToken(token)) {
            Post post = postRepo.findById(idPost);
            if (post != null) {
                List<Comment> commentList = commentRepo.findAllByPostId(idPost);
                List<DataSingleComment> dataList = new ArrayList<>();
                for (Comment comment : commentList) {
                    User userMe = userRepo.findByAccessToken(token);
                    User author = userRepo.findById(comment.getSenderId());
                    DataSingleComment dataSingleComment = new DataSingleComment();
                    dataSingleComment.setComment(comment);
                    dataSingleComment.setAuthor(new Author(author.getId(), author.getName(), author.getLinkAvatar()));
                    boolean isBlock = false;
                    Friend friend = friendRepo.findByUserAIdAndAndUserBId(userMe.getId(), author.getId());
                    if (friend != null) {
                        isBlock = friend.isBlock();
                    } else {
                        friend = friendRepo.findByUserAIdAndAndUserBId(author.getId(), userMe.getId());
                        if (friend != null) {
                            isBlock = friend.isBlock();
                        }
                    }
                    dataSingleComment.setBlock(isBlock);
                    dataList.add(dataSingleComment);
                }
                response.setCode(1000);
                response.setMessage("OK");
                response.setData(dataList);
            } else {
                response.setCode(9992);
                response.setMessage("Post is not exist");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }

        return response;
    }
}
