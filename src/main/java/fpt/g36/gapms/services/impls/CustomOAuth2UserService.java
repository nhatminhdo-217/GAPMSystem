package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Role;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.ImageService;
import fpt.g36.gapms.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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


        Role role = roleService.getRole("USER").orElseThrow(() -> new RuntimeException("Role not found"));


        // Kiểm tra email trong database
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    String pictureSave = null;
                    try {
                        pictureSave = imageService.saveImage(picture);
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    String finalPictureSave = pictureSave;
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setAvatar(finalPictureSave);
                    newUser.setRole(role);

                    return userRepository.save(newUser);
                });

        String roleName = "ROLE_" + user.getRole().getName().toUpperCase();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }

}
