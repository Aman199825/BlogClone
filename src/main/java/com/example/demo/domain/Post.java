package com.example.demo.domain;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @NotBlank(message="postName can't be blank")
    private String postName;
    @Nullable
    private String url;
    @Nullable
    @Lob
    private  String description;

    private  Integer voteCount=0;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",referencedColumnName = "userId")
    private User user;
    private Instant createdDate;
   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name="id",referencedColumnName = "id")
    private SubReddit subreddit;


}
