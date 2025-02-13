package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.ImageService;
import fpt.g36.gapms.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private RoleService roleService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // ✅ Kiểm tra nếu email không tồn tại
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();

            // ✅ Kiểm tra nếu tài khoản bị khóa
            if (!user.isActive()) {
                throw new DisabledException("Tài khoản của bạn đã bị khóa!");
            }

        } else {
            // ✅ Nếu user chưa tồn tại, tự động tạo tài khoản mới
            Role role = roleService.getRole("USER").orElseThrow(() -> new RuntimeException("Role not found"));
            String pictureSave = null;

            try {
                pictureSave = imageService.saveImage(picture);
            } catch (IOException e) {
                e.printStackTrace();
            }

            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setAvatar(pictureSave);
            user.setRole(role);
            user.setVerified(true);
            user.setActive(true); // ✅ Mặc định user mới sẽ active

            user = userRepository.save(user);
        }

        // ✅ Gán quyền cho user
        String roleName = "ROLE_" + user.getRole().getName().toUpperCase();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }
}
