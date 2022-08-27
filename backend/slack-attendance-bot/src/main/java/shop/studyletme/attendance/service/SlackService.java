package shop.studyletme.attendance.service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.oauth.OAuthV2AccessRequest;
import com.slack.api.methods.request.users.UsersIdentityRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.openid.connect.OpenIDConnectTokenResponse;
import com.slack.api.methods.response.openid.connect.OpenIDConnectUserInfoResponse;
import com.slack.api.methods.response.users.UsersIdentityResponse;
import com.slack.api.methods.response.users.UsersListResponse;
import com.slack.api.model.Message;
import com.slack.api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SlackService {

    @Value(value = "${slack.token}")
    String token;
    @Value(value = "${slack.channel.monitor}")
    String channel;
    @Value(value = "${spring.security.oauth2.client.registration.slack.client-id}")
    String clientId;
    @Value(value = "${spring.security.oauth2.client.registration.slack.client-secret}")
    String clientSecret;
    @Value(value = "${spring.security.oauth2.client.registration.slack.redirect-uri}")
    String redirectUrl;

    public void postSlackMessage(String message) {
        try {
            MethodsClient methods = Slack.getInstance().methods(token);
            methods.chatPostMessage(req -> req
                    .channel(channel) // channel 필드에 channel id 말고 user id를 넣으면 개인 DM으로 메시지 보내짐
                    .text(message));
        } catch (SlackApiException e) {
            // Slack API responded with unsuccessful status code (!= 20x)
            // Slack API가 실패한 상태 코드로 응답함(20x 아님)
            log.error(e.getMessage());
        } catch (IOException e) {
            // Throwing this exception indicates your app or Slack servers had a connectivity issue.
            // 이 예외가 발생하면 앱 또는 Slack 서버에 연결 문제가 있음을 나타냄
            log.error(e.getMessage());
        }
    }

    // userList를 호출하여 프로필 정보 및 이메일을 포함한 모든 Slack 사용자 목록 검색
    public void getSlackIdByEmail() {
        try {
            MethodsClient methods = Slack.getInstance().methods(token);
            UsersListResponse usersListResponse = methods.usersList(req -> req.token(token));
            usersListResponse.getMembers().forEach(u -> System.out.printf("member id : %s | name : %s | email : %s\n", u.getId(), u.getName(), u.getProfile().getEmail()));
        } catch (SlackApiException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void login(String code) {
        try {
            System.out.println("code = " + code);
            // 임시 OAuth 인증코드인 code를 AccessToken으로 발급
            MethodsClient methods = Slack.getInstance().methods(token);
            OpenIDConnectTokenResponse tokenResponse = methods.openIDConnectToken(req -> req.clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUrl)
                    .code(code));
            System.out.println("tokenResponse = " + tokenResponse);
            System.out.println(tokenResponse.getAccessToken());

            // Slack에 사용자 정보 조회 요청
            OpenIDConnectUserInfoResponse userInfoResponse = methods.openIDConnectUserInfo(req -> req.token(tokenResponse.getAccessToken()));
            System.out.println("userInfoResponse = " + userInfoResponse);
            // 로그인 로직 수행하기 (JWT 토큰, 쿠키, 세션 등)
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlackApiException e) {
            e.printStackTrace();
        }
    }
}
/*
- IOException
표준 예외 정보만 있음. (문자열 메시지 및 원인)
취소 및 연결 문제 또는 시간 초과로 인해 요청을 실행할 수 없는 경우 발생할 수 있음

- SlackApiException
기본 HTTP 응답, 원시 문자열 응답 본문 및 역직렬화 된 SlackApiException 객체가 있음
Slack API 서버가 실패한 HTTP 상태 코드(20x 아님)로 응답하는 경우 발생할 수 있음
 */

// email정보로 slack 고유 멤버 id를 가져올 수 있음 (권한 users:read.email)