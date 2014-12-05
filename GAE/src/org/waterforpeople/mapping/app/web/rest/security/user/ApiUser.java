package org.waterforpeople.mapping.app.web.rest.security.user;

import java.io.Serializable;

public class ApiUser implements Serializable {

    private static final long serialVersionUID = -8372182998303162190L;
    private String userName;
    private String accessKey;
    private String secret;
    private Long userId;

    public ApiUser(String userName, String accessKey, String secret, Long userId) {

        this.setUserName(userName);
        this.setAccessKey(accessKey);
        this.setSecret(secret);
        this.userId = userId;
    }

    public ApiUser() {
        this.setUserName("");
        this.setAccessKey("");
        this.setSecret("");
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
