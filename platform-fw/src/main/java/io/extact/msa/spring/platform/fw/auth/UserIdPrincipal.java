package io.extact.msa.spring.platform.fw.auth;

import io.extact.msa.spring.platform.fw.auth.header.InvalidUserIdHeaderException;

public class UserIdPrincipal {

    private final int userId;

    public UserIdPrincipal(int userId) {
        this.userId = userId;
    }

    public UserIdPrincipal(String userId) {
        try {
            this.userId = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new InvalidUserIdHeaderException(e.getMessage(), e);
        }
    }

    public int userId() {
        return userId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + (this.userId != LoginUser.ANONYMOUS_ID ? "(userId=" + this.userId + ")" : "(Anonymous)");
    }
}
