// package com.insta.followservice.controller;
// import com.insta.followservice.dto.FollowResponse;
// import com.insta.followservice.service.FollowService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import java.util.List;
// @RestController @RequestMapping("/api/follows") @RequiredArgsConstructor
// public class FollowController {
//     private final FollowService followService;
//     @PostMapping("/{targetUserId}")
//     public ResponseEntity<FollowResponse> follow(
//             @PathVariable Long targetUserId, @RequestHeader("X-User-Id") Long userId) {
//         return ResponseEntity.ok(followService.follow(userId, targetUserId));
//     }
//     @DeleteMapping("/{targetUserId}")
//     public ResponseEntity<FollowResponse> unfollow(
//             @PathVariable Long targetUserId, @RequestHeader("X-User-Id") Long userId) {
//         return ResponseEntity.ok(followService.unfollow(userId, targetUserId));
//     }
//     @GetMapping("/{userId}/stats")
//     public ResponseEntity<FollowResponse> getStats(
//             @PathVariable Long userId,
//             @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
//         return ResponseEntity.ok(followService.getFollowStats(userId, currentUserId));
//     }
//     @GetMapping("/{userId}/followers")
//     public ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId) {
//         return ResponseEntity.ok(followService.getFollowerIds(userId));
//     }
//     @GetMapping("/{userId}/following")
//     public ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId) {
//         return ResponseEntity.ok(followService.getFollowingIds(userId));
//     }
// }
package com.insta.followservice.controller;

import com.insta.followservice.dto.FollowResponse;
import com.insta.followservice.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // ✅ FOLLOW USER
    @PostMapping("/{targetUserId}")
    public ResponseEntity<FollowResponse> follow(
            @PathVariable Long targetUserId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        Long currentUser = (userId == null) ? 1L : userId; // default user
        return ResponseEntity.ok(followService.follow(currentUser, targetUserId));
    }

    // ❌ UNFOLLOW USER
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<FollowResponse> unfollow(
            @PathVariable Long targetUserId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        Long currentUser = (userId == null) ? 1L : userId;
        return ResponseEntity.ok(followService.unfollow(currentUser, targetUserId));
    }

    // 📊 FOLLOW STATS
    @GetMapping("/{userId}/stats")
    public ResponseEntity<FollowResponse> getStats(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {

        return ResponseEntity.ok(
                followService.getFollowStats(userId, currentUserId)
        );
    }

    // 👥 FOLLOWERS LIST (NO HEADER NEEDED)
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Long>> getFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowerIds(userId));
    }

    // 👤 FOLLOWING LIST (NO HEADER NEEDED)
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<Long>> getFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getFollowingIds(userId));
    }
}