package io.extact.msa.spring.platform.core.auth.user;


public class AuthUserId {

    public static final AuthUserId ANONYMOUS_ID = new AuthUserId(-1);
    private final int userId;

    public AuthUserId(int userId) {
        this.userId = userId;
    }

    public AuthUserId(String userId) {
        try {
            this.userId = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new InvalidUserIdException(e.getMessage(), e);
        }
    }

    public boolean isAnonymousId() {
        return userId == ANONYMOUS_ID.value();
    }

    public int value() {
        return userId;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + (this.userId != ANONYMOUS_ID.value() ? "(userId=" + this.userId + ")" : "(Anonymous)");
    }
}
