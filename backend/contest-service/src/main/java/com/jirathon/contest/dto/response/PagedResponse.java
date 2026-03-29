package com.jirathon.contest.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;

        public Builder<T> content(List<T> content) { this.content = content; return this; }
        public Builder<T> page(int page) { this.page = page; return this; }
        public Builder<T> size(int size) { this.size = size; return this; }
        public Builder<T> totalElements(long totalElements) { this.totalElements = totalElements; return this; }
        public Builder<T> totalPages(int totalPages) { this.totalPages = totalPages; return this; }
        public Builder<T> hasNext(boolean hasNext) { this.hasNext = hasNext; return this; }
        public Builder<T> hasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; return this; }

        public PagedResponse<T> build() {
            return new PagedResponse<>(content, page, size, totalElements, totalPages, hasNext, hasPrevious);
        }
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }
}
