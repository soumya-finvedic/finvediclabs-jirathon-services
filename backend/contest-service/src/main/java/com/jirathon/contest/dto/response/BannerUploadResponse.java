package com.jirathon.contest.dto.response;

public class BannerUploadResponse {

    private String contestId;
    private String bannerUrl;

    public BannerUploadResponse() {
    }

    public BannerUploadResponse(String contestId, String bannerUrl) {
        this.contestId = contestId;
        this.bannerUrl = bannerUrl;
    }

    public String getContestId() {
        return contestId;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public static BannerUploadResponseBuilder builder() {
        return new BannerUploadResponseBuilder();
    }

    public static class BannerUploadResponseBuilder {
        private String contestId;
        private String bannerUrl;

        public BannerUploadResponseBuilder contestId(String contestId) {
            this.contestId = contestId;
            return this;
        }

        public BannerUploadResponseBuilder bannerUrl(String bannerUrl) {
            this.bannerUrl = bannerUrl;
            return this;
        }

        public BannerUploadResponse build() {
            return new BannerUploadResponse(contestId, bannerUrl);
        }
    }
}
