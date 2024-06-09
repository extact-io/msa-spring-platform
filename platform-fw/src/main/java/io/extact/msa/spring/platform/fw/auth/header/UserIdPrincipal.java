package io.extact.msa.spring.platform.fw.auth.header;

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
}
