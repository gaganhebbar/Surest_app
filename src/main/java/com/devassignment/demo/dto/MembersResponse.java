package com.devassignment.demo.dto;

import lombok.Data;

import java.util.List;
@Data
public class MembersResponse<T>{
    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public MembersResponse(List<T> data, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }
}
