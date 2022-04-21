package com.http.client.handler.analysis.url;

import com.http.client.context.HttpRequestContext;


/**
 * @author linzhou
 * @ClassName AnalysisUrlParamHandler.java
 * @createTime 2022年02月07日 11:57:00
 * @Description
 */
public interface AnalysisUrlHandler {

    /**
     * 责任链模式调用
     * 解析方法上的参数
     * @param context 上下文
     * @param url 返回的url
     * @return
     */
    AnalysisUrlResult analysisUrl(HttpRequestContext context,String url) throws Exception;


    class AnalysisUrlResult{
        /**
         * 是否拦截,如果是false,则会继续调用后面的url处理器
         */
        private boolean isBreak = false;

        private String url;

        public AnalysisUrlResult(boolean isBreak, String url) {
            this.isBreak = isBreak;
            this.url = url;
        }

        public AnalysisUrlResult(String url) {
            this.url = url;
        }

        public boolean isBreak() {
            return isBreak;
        }

        public void setBreak(boolean aBreak) {
            isBreak = aBreak;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
