package com.insta.searchservice.service;
import com.insta.searchservice.client.PostClient;
import com.insta.searchservice.client.UserClient;
import com.insta.searchservice.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;
@Service @RequiredArgsConstructor
public class SearchService {
    private final UserClient userClient;
    private final PostClient postClient;
    public SearchResponse search(String query) {
        SearchResponse response = new SearchResponse();
        try { response.setUsers(userClient.searchUsers(query)); }
        catch (Exception e) { response.setUsers(Collections.emptyList()); }
        try { response.setPosts(postClient.searchPosts(query)); }
        catch (Exception e) { response.setPosts(Collections.emptyList()); }
        return response;
    }
}
