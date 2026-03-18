package com.victor.security2.controller;

import com.victor.security2.dto.CreateTweetDto;
import com.victor.security2.dto.FeedDto;
import com.victor.security2.service.TweetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto dto,
                                            JwtAuthenticationToken token) {
        tweetService.createTweet(dto, token.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var feedDto = tweetService.getFeed(page, pageSize);
        return ResponseEntity.ok(feedDto);
    }

    @DeleteMapping("/tweet/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
                                            JwtAuthenticationToken token) {
        tweetService.deleteTweet(tweetId, token.getName());
        return ResponseEntity.ok().build();
    }
}