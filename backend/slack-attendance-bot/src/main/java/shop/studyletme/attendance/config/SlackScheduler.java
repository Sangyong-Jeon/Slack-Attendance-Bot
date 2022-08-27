package shop.studyletme.attendance.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import shop.studyletme.attendance.service.SlackService;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SlackScheduler {
    private final SlackService slackService;

    // 1분마다 실행
    @Scheduled(cron = "0 0/1 * * * *")
    public void sayHello1minute() {
        slackService.postSlackMessage("_기울임_\n*굵게*\n~취소선~\n>블록 따옴표\n`code`\n```code여러개```\n" +
                "- 점 서식넣기\n- 음냐\n- 음냐냐\n" +
                "주소 : www.naver.com\n" +
                "<https://www.naver.com|네이버주소>\n" +
                ":smile: :wave:");
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void notification() {
        slackService.getSlackIdByEmail();
    }
}
