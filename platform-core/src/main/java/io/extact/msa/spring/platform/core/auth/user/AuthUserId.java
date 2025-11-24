package io.extact.msa.spring.platform.core.auth.user;

public record AuthUserId(int value) {

    public static final AuthUserId ANONYMOUS_ID = new AuthUserId(-1);

    public AuthUserId(String userId) {
        this(parse(userId));
    }

    private static int parse(String userId) {
        try {
            return Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            throw new InvalidUserIdException(e.getMessage(), e);
        }
    }

    public boolean isAnonymousId() {
        return value == ANONYMOUS_ID.value();
    }

    @Override
    public String toString() {
        return isAnonymousId()
                ? getClass().getSimpleName() + "(Anonymous)"
                : getClass().getSimpleName() + "(userId=" + value + ")";
    }
}