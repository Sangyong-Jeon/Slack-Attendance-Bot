package shop.studyletme.attendance.config;

import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomAccessTokenResponseConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {

    private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
            OAuth2ParameterNames.ACCESS_TOKEN,
            OAuth2ParameterNames.TOKEN_TYPE,
            OAuth2ParameterNames.EXPIRES_IN,
            OAuth2ParameterNames.REFRESH_TOKEN,
            OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
        // 슬랙 API는 access_token을 봇과 사용자로 나눠서 주기때문에 사용자 토큰을 받기위해 따로 처리함
        Map<String, String> authed_user = (Map<String, String>) tokenResponseParameters.get("authed_user");
        String accessToken = authed_user.get("access_token");
        // 아래는 봇 토큰
//        String accessToken = (String) tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);

        // 슬랙 토큰값 출력
        System.out.println("봇 Access Token = " + (String) tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN));
        System.out.println("사용자 Access Token = " + accessToken);
        System.out.println("봇 ID = " + (String) tokenResponseParameters.get("bot_user_id"));
        System.out.println("앱 ID = " + (String) tokenResponseParameters.get("app_id"));
        System.out.println("사용자 ID = " + (String) authed_user.get("id"));

        // 슬랙 API는 access_token에 "Bearer"가 없는 토큰값이기 때문에 명시해준다.
        OAuth2AccessToken.TokenType accessTokenType = OAuth2AccessToken.TokenType.BEARER;

        long expiresIn = 0;
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.EXPIRES_IN)) {
            try {
                expiresIn = Long.valueOf((String) tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN));
            } catch (NumberFormatException ex) {
            }
        }

        Set<String> scopes = Collections.emptySet();
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
            String scope = (String) tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " ")).collect(Collectors.toSet());
        }

        Map<String, Object> additionalParameters = new LinkedHashMap<>();
        tokenResponseParameters.entrySet().stream()
                .filter(e -> !TOKEN_RESPONSE_PARAMETER_NAMES.contains(e.getKey()))
                .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(accessTokenType)
                .expiresIn(expiresIn)
                .scopes(scopes)
                .additionalParameters(additionalParameters)
                .build();
    }
}