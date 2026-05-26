package com.insta.searchservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/users/search")
    List<Object> searchUsers(@RequestParam("q") String q);
}
