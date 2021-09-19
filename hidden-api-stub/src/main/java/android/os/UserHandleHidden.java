package android.os;

public class UserHandleHidden {
    public static UserHandle ALL;

    public static UserHandle of(int userId) {
        throw new RuntimeException();
    }
}
