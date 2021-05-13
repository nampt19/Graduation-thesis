package com.example.nampt.service.ServiceImpl;

import com.example.nampt.domain.response.BaseResponse;
import com.example.nampt.domain.response.LikeResponse;
import com.example.nampt.domain.response.post.AddPostResponse;
import com.example.nampt.domain.response.post.ListPostResponse;
import com.example.nampt.domain.response.post.SinglePostResponse;
import com.example.nampt.domain.response.post.data.Author;
import com.example.nampt.domain.response.post.data.DataSinglePost;
import com.example.nampt.entity.*;
import com.example.nampt.repository.*;
import com.example.nampt.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class PostService extends BaseService implements IPostService {
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
    public AddPostResponse addPost(String token, MultipartFile[] mediaFiles, String content,Date createTime) {
        AddPostResponse response = new AddPostResponse();
        if (checkToken(token)) {
            List<String> urlMediaList = null;
            urlMediaList = saveImagesAndVideo(mediaFiles);

            Post post = new Post();
            post.setPosterId(userRepo.findByAccessToken(token).getId());

            if (!content.isEmpty()) {
                post.setContent(content);
            }
            post.setCreateTime(createTime);
            int countImage = 0;
            int countVideo = 0;
            for (String url : urlMediaList) {
                if (url.contains("video")) {
                    countVideo++;
                } else {
                    countImage++;
                }
            }
            if (countVideo > 1 || countImage > 4) {
                response.setCode(1004);
                response.setMessage("Parameter value is invalid(limit 1 video or 4 images)");
                return response;
            }

            for (String url : urlMediaList) {
                if (url.contains(".mp4") || url.contains(".mov")) {
                    post.setLinkVideo(url);
                    urlMediaList.remove(url);
                    break;
                }
            }
            int count = urlMediaList.size();
            switch (count) {
                case 0:
                    break;
                case 1:
                    post.setLinkImage1(urlMediaList.get(0));
                    break;
                case 2:
                    post.setLinkImage1(urlMediaList.get(0));
                    post.setLinkImage2(urlMediaList.get(1));
                    break;
                case 3:
                    post.setLinkImage1(urlMediaList.get(0));
                    post.setLinkImage2(urlMediaList.get(1));
                    post.setLinkImage3(urlMediaList.get(2));
                    break;
                case 4:
                    post.setLinkImage1(urlMediaList.get(0));
                    post.setLinkImage2(urlMediaList.get(1));
                    post.setLinkImage3(urlMediaList.get(2));
                    post.setLinkImage4(urlMediaList.get(3));
                    break;
            }
            if (post.getContent() == null
                    && post.getLinkImage1() == null
                    && post.getLinkImage2() == null
                    && post.getLinkImage3() == null
                    && post.getLinkImage4() == null
                    && post.getLinkVideo() == null) {
                response.setCode(1004);
                response.setMessage("Parameter value is invalid(content empty and file empty)");
                return response;
            }
            Post postSaved = postRepo.save(post);
            response.setCode(1000);
            response.setMessage("OK");
            if (postSaved != null)
                response.setPostId(postSaved.getId());

        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public BaseResponse deletePost(String token, int postId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            Post post = postRepo.findById(postId);
            if (post != null) {
                List<Like> likes = likeRepo.findAllByPostId(postId);
                List<Comment> comments = commentRepo.findAllByPostId(postId);
                List<Hidden> hiddens = hiddenRepo.findAllByPostId(postId);

                likeRepo.deleteAll(likes);
                commentRepo.deleteAll(comments);
                hiddenRepo.deleteAll(hiddens);
                postRepo.delete(post);

                response.setCode(1000);
                response.setMessage("OK");
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
    public BaseResponse editPost(String token, int postId, String newContent) {
        BaseResponse response = new BaseResponse();
        Post post = postRepo.findById(postId);
        if (checkToken(token)) {
            if (post != null) {
                String oldContent = post.getContent();
                if (post.getLinkImage1() == null && post.getLinkVideo() == null) {
                    if (newContent != null && newContent.length() != 0) {
                        if (oldContent == null) oldContent = "";
                        if (oldContent.equals(newContent)) {
                            response.setCode(9996);
                            response.setMessage("content post is not changed");
                        } else {
                            if (newContent.length() <= 500) {
                                response.setCode(1000);
                                response.setMessage("OK");
                                post.setContent(newContent);
                                postRepo.save(post);
                            } else {
                                response.setCode(1004);
                                response.setMessage("Parameter value is not valid(>500 words)");
                            }
                        }
                    } else {
                        response.setCode(1004);
                        response.setMessage("Parameter value is not valid (content empty)");
                    }
                } else {
                    if (newContent == null || newContent.length() == 0) {
                        if (oldContent == null) {
                            response.setCode(9996);
                            response.setMessage("content post is not changed");
                        } else {
                            response.setCode(1000);
                            response.setMessage("OK");
                            post.setContent(null);
                            postRepo.save(post);
                        }
                    } else {
                        if (oldContent == null) oldContent = "";
                        if (oldContent.equals(newContent)) {
                            response.setCode(9996);
                            response.setMessage("content post is not changed");
                        } else {
                            if (newContent.length() <= 500) {
                                response.setCode(1000);
                                response.setMessage("OK");
                                post.setContent(newContent);
                                postRepo.save(post);
                            } else {
                                response.setCode(1004);
                                response.setMessage("Parameter value is not valid(>500 words)");
                            }
                        }
                    }
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
    public SinglePostResponse getPost(String token, int idPost) {
        SinglePostResponse response = new SinglePostResponse();
        if (checkToken(token)) {
            Post post = postRepo.findById(idPost);
            if (post != null) {
                DataSinglePost singlePost = handleGetPost(token, idPost);
                response.setCode(1000);
                response.setMessage("OK");
                response.setData(singlePost);

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

    private DataSinglePost handleGetPost(String token, int idPost) {
        Post post = postRepo.findById(idPost);
        int totalLikes = 0;
        int totalComments = 0;
        Author authorDetail = null;
        boolean isLike = false;
        boolean isBlock = false;
        boolean isHidden = false;
        User userMe = userRepo.findByAccessToken(token);
        User author = userRepo.getOne(post.getPosterId());

        List<Like> likes = likeRepo.findAllByPostId(idPost);
        totalLikes = likes.size();
        List<Comment> comments = commentRepo.findAllByPostId(idPost);
        totalComments = comments.size();
        authorDetail = new Author(author.getId(), author.getName(), author.getLinkAvatar());
        Like like = likeRepo.findByPostIdAndSenderId(post.getId(), userMe.getId());
        if (like != null) isLike = true;

        // nếu A là bạn của B thì chặn ăn theo thuộc tính isBlock
        // của bảng friend (và ngược lại), còn nếu không là friend thì false
        //chú ý : bảng friend có tính đối xứng ! , 1 -2 thì không 2-1
        Friend friend = friendRepo.findByUserAIdAndAndUserBId(userMe.getId(), author.getId());
        if (friend != null) {
            isBlock = friend.isBlock();
        } else {
            friend = friendRepo.findByUserAIdAndAndUserBId(author.getId(), userMe.getId());
            if (friend != null) {
                isBlock = friend.isBlock();
            }
        }

        Hidden hidden = hiddenRepo.findByPostIdAndHiddenUserId(post.getId(), userMe.getId());
        if (hidden != null) isHidden = true;

        DataSinglePost singlePostBody = new DataSinglePost(post, totalLikes, totalComments, authorDetail, isLike, isBlock, isHidden);
        return singlePostBody;
    }

    @Override
    public ListPostResponse getListPost(String token, int index, int count, int lastPostId) {
        ListPostResponse response = new ListPostResponse();
        if (checkToken(token)) {
            User userMe = userRepo.findByAccessToken(token);
            List<Integer> usersIdList = friendRepo.findFriendIdListByUserIdPagination(userMe.getId(),0,1000);
            usersIdList.add(userMe.getId());
            List<Integer> postIdList;
            if (lastPostId <= 0) {
                postIdList = postRepo.findPostsIdByUsersId(usersIdList, index, count);
            } else {
                postIdList = postRepo.findPostsIdByUsersIdAndLastPostId(usersIdList, index, count, lastPostId);
            }
            List<DataSinglePost> responseList = new LinkedList<>();
            for (int id : postIdList) {
                DataSinglePost singlePost = handleGetPost(token, id);
                responseList.add(singlePost);
            }
            Collections.sort(postIdList, Collections.reverseOrder());
            response.setCode(1000);
            response.setMessage("OK");
            response.setData(responseList);
            response.setLastId(lastPostId == 0 ? (postIdList.isEmpty() ? lastPostId : postIdList.get(0)) : lastPostId);
            response.setLastPage(postIdList.size() < count);
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    @Override
    public BaseResponse hiddenPost(String token, int postId) {
        BaseResponse response = new BaseResponse();
        if (checkToken(token)) {
            User user = userRepo.findByAccessToken(token);
            Post post = postRepo.findById(postId);
            if (post != null) {
                Hidden hidden = new Hidden(post.getId(), user.getId());
                hiddenRepo.save(hidden);
                response.setCode(1000);
                response.setMessage("OK");
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
    public LikeResponse like(String token, int postId) {
        LikeResponse response = new LikeResponse();
        if (checkToken(token)) {
            Post post = postRepo.findById(postId);
            if (post != null) {
                User user = userRepo.findByAccessToken(token);
                Like like = likeRepo.findByPostIdAndSenderId(postId, user.getId());
                if (like != null) {
                    likeRepo.delete(like);
                } else {
                    like = new Like();
                    like.setPostId(postId);
                    like.setSenderId(user.getId());
                    likeRepo.save(like);
                }
                List<Like> likes = likeRepo.findAllByPostId(postId);
                response.setCode(1000);
                response.setMessage("OK");
                response.setTotalLike(likes.size());
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
    public ListPostResponse getListPostByUserId(String token, int userId) {
        ListPostResponse response = new ListPostResponse();
        if (checkToken(token)) {
            User poster = userRepo.findById(userId);
            if (poster != null) {
                List<Integer> usersIdList = new ArrayList<>();
                usersIdList.add(poster.getId());
                List<DataSinglePost> responseList = new LinkedList<>();
                List<Integer> postIdList = postRepo.findPostsIdByUsersId(usersIdList, 0, 10000);
                for (int id : postIdList) {
                    DataSinglePost singlePost = handleGetPost(token, id);
                    responseList.add(singlePost);
                }
                response.setCode(1000);
                response.setMessage("OK");
                response.setData(responseList);
            } else {
                response.setCode(9992);
                response.setMessage("User is not existt");
            }
        } else {
            response.setCode(9993);
            response.setMessage("Code verify is incorrect");
        }
        return response;
    }

    private List<String> saveImagesAndVideo(MultipartFile[] mediaFiles) {
        List<String> urlList = new ArrayList<>();
        for (MultipartFile file : mediaFiles) {
            String urlSaved = saveMediaFile(file);
            if (urlSaved != null) {
                urlList.add(urlSaved);
            }
        }

        return urlList;
    }

    private String saveMediaFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        File mediaFile = new File("src/main/upload/post/" + System.currentTimeMillis() + "_" + file.getOriginalFilename());
        FileOutputStream writer = null;
        try {
            writer = new FileOutputStream(mediaFile);
            if (mediaFile.exists())
                mediaFile.createNewFile();
            writer.write(file.getBytes());
            writer.close();
            return "/upload/post/" + mediaFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
