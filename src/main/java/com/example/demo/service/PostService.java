package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.exceptions.PostNotFoundException;
import com.example.demo.exceptions.SubRedditNotFoundException;
import com.example.demo.repository.*;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.domain.VoteType.DOWNVOTE;
import static com.example.demo.domain.VoteType.UPVOTE;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private  final SubRedditRepository subRedditRepository;
    private  final AuthService authService;
    private final PostRepository postRepository;
    private  final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;


    //refractor it to return void
 public Post save(PostRequest postRequest)
 {
     SubReddit subReddit=subRedditRepository.findByName(postRequest.getSubRedditName()).orElseThrow(()->new SubRedditNotFoundException("subreddit with name: "+postRequest.getSubRedditName()+" doesn't exists"));
     User user=authService.getCurrentUser();
       Post post=mapToPost(postRequest);
       post.setSubreddit(subReddit);
       post.setUser(user);
       postRepository.save(post);
    return post;
 }
 Post mapToPost(PostRequest postRequest)
 {
        return Post.builder().postId(postRequest.getPostId()).description(postRequest.getDescription()).postName(postRequest.getPostName()).url(postRequest.getUrl()).createdDate(Instant.now()).build();
  }
 PostRequest mapToDto(Post post)
 {
     return PostRequest.builder().postId(post.getPostId()).postName(post.getPostName()).description(post.getDescription()).url(post.getUrl()).subRedditName(post.getSubreddit().getName()).build();
 }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return mapToPostResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        SubReddit subreddit = subRedditRepository.findById(subredditId)
                .orElseThrow(() -> new SubRedditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(this::mapToPostResponse).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(this::mapToPostResponse)
                .collect(toList());
    }
    public PostResponse mapToPostResponse(Post post)
    {
        return PostResponse.builder().id(post.getPostId()).description(post.getDescription()).postName(post.getPostName()).url(post.getUrl()).userName(post.getUser().getUsername()).
                subredditName(post.getSubreddit().getName()).
              voteCount(post.getVoteCount()).duration(getDuration(post)).commentCount(commentCount(post)).build();
    }

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                            authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }
}
