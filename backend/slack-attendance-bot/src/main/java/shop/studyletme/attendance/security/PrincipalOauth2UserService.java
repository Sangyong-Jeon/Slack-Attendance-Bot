package shop.studyletme.attendance.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import shop.studyletme.attendance.domain.Member;
import shop.studyletme.attendance.repository.MemberRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("OAuth RegistrationId = " + userRequest.getClientRegistration());
        System.out.println("getAccessToken = " + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("attributes = " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = new SlackUserInfo(oAuth2User.getAttributes());
        log.info("PrincipalOauth2UserService : 슬랙 로그인 요청");

        String uuid = UUID.randomUUID().toString().substring(0, 6);

        Member member = memberRepository.findByProviderId(oAuth2UserInfo.getProviderId())
                .orElseGet(() -> Member.builder()
                        .email(oAuth2UserInfo.getEmail())
                        .name(oAuth2UserInfo.getName())
                        .provider(oAuth2UserInfo.getProvider())
                        .providerId(oAuth2UserInfo.getProviderId())
                        .password(bCryptPasswordEncoder.encode("snsMember" + uuid))
                        .role("ROLE_MEMBER")
                        .build());

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }
}
