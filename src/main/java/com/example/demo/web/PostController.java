package com.example.demo.web;

import com.example.demo.domain.Post;
import com.example.demo.dto.PostRequest;
import com.example.demo.dto.PostResponse;
import com.example.demo.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("api/posts/")
@AllArgsConstructor
public class PostController {
    private final PostService postService;
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostRequest postRequest)
    {
        Post post=postService.save(postRequest);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAll()
    {
        List<PostResponse> postResponseList=postService.getAllPosts();
        return  new ResponseEntity<>(postResponseList,HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getSingleById(@PathVariable Long id)
    {
        PostResponse postResponse=postService.getPost(id);
        return  new ResponseEntity<>(postResponse,HttpStatus.OK);
    }

    @GetMapping("by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(Long id) {
        return status(HttpStatus.OK).body(postService.getPostsBySubreddit(id));
    }
    @GetMapping("by-user/{name}")
    public ResponseEntity<List<PostResponse>> getSingleBySubbRedditId(@PathVariable String username)
    {
        return  new ResponseEntity<>(postService.getPostsByUsername(username),HttpStatus.OK);
    }
}
