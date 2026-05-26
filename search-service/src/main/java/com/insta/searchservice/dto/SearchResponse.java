package com.insta.searchservice.dto;
import lombok.Data;
import java.util.List;
@Data
public class SearchResponse {
    private List<Object> users;
    private List<Object> posts;
}
