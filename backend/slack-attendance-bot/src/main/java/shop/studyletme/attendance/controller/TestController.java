package shop.studyletme.attendance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.studyletme.attendance.service.SlackService;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final SlackService slackService;

    @GetMapping("/slack/oauth/test")
    public void test(Object test) {
        System.out.println(test);
    }

    /*
    Slack API에서 Redirect URL을 아래 주소로 요청
     */
    @GetMapping("/api/slack-login")
    public void login(@RequestParam String code) {
        slackService.login(code);
    }
}