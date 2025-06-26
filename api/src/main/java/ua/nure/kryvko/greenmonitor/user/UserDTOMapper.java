package ua.nure.kryvko.greenmonitor.user;

public class UserDTOMapper {
    public static UserResponse toDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}
