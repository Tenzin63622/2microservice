package com.insta.searchservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
@FeignClient(name = "post-service")
public interface PostClient {
    @GetMapping("/api/posts/search")
    List<Object> searchPosts(@RequestParam("q") String q);
}
