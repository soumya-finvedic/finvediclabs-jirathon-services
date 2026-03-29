package com.jirathon.org.dto;

import java.util.List;

public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PagedResponse() {}

    public PagedResponse(List<T> content, int page, int size,
                         long totalElements, int totalPages, boolean last) {
        this.content = content; this.page = page; this.size = size;
        this.totalElements = totalElements; this.totalPages = totalPages; this.last = last;
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private List<T> content;
        private int page, size, totalPages;
        private long totalElements;
        private boolean last;
        public Builder<T> content(List<T> v)       { this.content = v; return this; }
        public Builder<T> page(int v)              { this.page = v; return this; }
        public Builder<T> size(int v)              { this.size = v; return this; }
        public Builder<T> totalElements(long v)    { this.totalElements = v; return this; }
        public Builder<T> totalPages(int v)        { this.totalPages = v; return this; }
        public Builder<T> last(boolean v)          { this.last = v; return this; }
        public PagedResponse<T> build() {
            return new PagedResponse<>(content, page, size, totalElements, totalPages, last);
        }
    }

    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return PagedResponse.<T>builder()
                .content(content).page(page).size(size)
                .totalElements(totalElements).totalPages(totalPages)
                .last(page >= totalPages - 1)
                .build();
    }

    public List<T> getContent()      { return content; }
    public int getPage()             { return page; }
    public int getSize()             { return size; }
    public long getTotalElements()   { return totalElements; }
    public int getTotalPages()       { return totalPages; }
    public boolean isLast()          { return last; }

    public void setContent(List<T> content)          { this.content = content; }
    public void setPage(int page)                    { this.page = page; }
    public void setSize(int size)                    { this.size = size; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public void setTotalPages(int totalPages)        { this.totalPages = totalPages; }
    public void setLast(boolean last)                { this.last = last; }
}
