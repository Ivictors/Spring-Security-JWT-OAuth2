package com.victor.security2.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tb_tweets")
public class Tweet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "tweet_Id")
    private Long tweetId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
        
    private String content;
    
    @CreationTimestamp
    private Instant creationTimeStamp;


}
