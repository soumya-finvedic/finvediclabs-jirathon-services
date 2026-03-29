package com.jirathon.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private String cursor;

    public PagedResponse() {}

    public PagedResponse(List<T> content, int page, int size, long totalElements,
                         int totalPages, boolean hasNext, boolean hasPrevious, String cursor) {
        this.content = content; this.page = page; this.size = size;
        this.totalElements = totalElements; this.totalPages = totalPages;
        this.hasNext = hasNext; this.hasPrevious = hasPrevious; this.cursor = cursor;
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private List<T> content;
        private int page, size, totalPages;
        private long totalElements;
        private boolean hasNext, hasPrevious;
        private String cursor;
        public Builder<T> content(List<T> v)       { this.content = v; return this; }
        public Builder<T> page(int v)              { this.page = v; return this; }
        public Builder<T> size(int v)              { this.size = v; return this; }
        public Builder<T> totalElements(long v)    { this.totalElements = v; return this; }
        public Builder<T> totalPages(int v)        { this.totalPages = v; return this; }
        public Builder<T> hasNext(boolean v)       { this.hasNext = v; return this; }
        public Builder<T> hasPrevious(boolean v)   { this.hasPrevious = v; return this; }
        public Builder<T> cursor(String v)         { this.cursor = v; return this; }
        public PagedResponse<T> build() {
            return new PagedResponse<>(content, page, size, totalElements, totalPages, hasNext, hasPrevious, cursor);
        }
    }

    public List<T> getContent()      { return content; }
    public int getPage()             { return page; }
    public int getSize()             { return size; }
    public long getTotalElements()   { return totalElements; }
    public int getTotalPages()       { return totalPages; }
    public boolean isHasNext()       { return hasNext; }
    public boolean isHasPrevious()   { return hasPrevious; }
    public String getCursor()        { return cursor; }
    public void setContent(List<T> content)          { this.content = content; }
    public void setPage(int page)                    { this.page = page; }
    public void setSize(int size)                    { this.size = size; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public void setTotalPages(int totalPages)        { this.totalPages = totalPages; }
    public void setHasNext(boolean hasNext)          { this.hasNext = hasNext; }
    public void setHasPrevious(boolean hasPrevious)  { this.hasPrevious = hasPrevious; }
    public void setCursor(String cursor)             { this.cursor = cursor; }
}
