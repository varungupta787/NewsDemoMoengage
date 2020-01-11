package com.news.demo.models;

public class ServerConfigDataResponse {

    private ServerConfigData configData;
    private ErrorModel errorModel;

    public ServerConfigDataResponse() {
    }

    public ServerConfigData getConfigData() {
        return configData;
    }

    public void setConfigData(ServerConfigData configData) {
        this.configData = configData;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(ErrorModel errorModel) {
        this.errorModel = errorModel;
    }
}
