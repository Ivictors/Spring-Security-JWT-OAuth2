package com.victor.security2.controller;

import com.victor.security2.dto.CreateTweetDto;
import com.victor.security2.dto.FeedDto;
import com.victor.security2.dto.FeedItemDto;
import com.victor.security2.entities.Role;
import com.victor.security2.entities.Tweet;
import com.victor.security2.repository.TweetRepository;
import com.victor.security2.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto dto,
                                            JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(@RequestParam (value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        var tweets = tweetRepository.findAll(PageRequest.of(page,pageSize, Sort.Direction.DESC,"creationTimeStamp"))
                .map(tweet ->
                        new FeedItemDto(tweet.getTweetId(),tweet.getContent(),tweet.getUser().getUsername()));

        return ResponseEntity.ok(new FeedDto(tweets.getContent(),page,pageSize,tweets.getTotalPages(),tweets.getTotalElements()));
    }

    @DeleteMapping("/tweet/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
                                            JwtAuthenticationToken token) {
        var tweet = tweetRepository.findAllByTweetId(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var user = userRepository.findById(UUID.fromString(token.getName()));
        var isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || !tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        tweetRepository.deleteById(tweetId);

        return ResponseEntity.ok().build();
    }
}
