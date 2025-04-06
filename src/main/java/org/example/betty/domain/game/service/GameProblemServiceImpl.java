package org.example.betty.domain.game.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.game.dto.redis.PlayerRelayInfo;
import org.example.betty.domain.game.dto.redis.QuestionCode;
import org.example.betty.domain.game.dto.redis.RedisGameRelay;
import org.example.betty.domain.game.dto.redis.RedisGameProblem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameProblemServiceImpl implements GameProblemService {

    private final ProblemGenerator problemGenerator;
    @Qualifier("redisTemplate2")
    private final RedisTemplate<String, Object> redisTemplate2;
    private final Map<String, String> previousBatterMap = new ConcurrentHashMap<>();
    private final SseService sseService;

    @Override
    public void handleRelayUpdate(String gameId, RedisGameRelay currentRelay) {
        PlayerRelayInfo currentBatter = currentRelay.getBatter();
        if (currentBatter == null || currentBatter.getName() == null) return;

        String currentBatterName = currentBatter.getName();
        String previousBatterName = previousBatterMap.get(gameId);
        
        // 앱 실행시 : 현재경기중인 타자로 초기화 (문제생성x)
        if (previousBatterName == null) {
            previousBatterMap.put(gameId, currentBatterName);
            log.info("[초기 중계 세팅] gameId={} | 현재 타자: {}", gameId, currentBatterName);
            return;
        }
        
        // 타자 교체 감지
        if (!currentBatterName.equals(previousBatterName)) {
            PlayerRelayInfo prevBatter = currentRelay.getPreviousBatter();
            
            // 1. 정답 채점
            if (prevBatter != null && prevBatter.getName() != null) {
                String redisKey = "game:" + LocalDate.now() + ":" + gameId + ":problem";
                
                // 일단 전체 생성된 문제 조회
                ListOperations<String, Object> listOps = redisTemplate2.opsForList();
                List<Object> problems = listOps.range(redisKey, 0, -1);

                log.info("[채점 시작] gameId={} | previousBatter={}", gameId, prevBatter.getName());
                if (problems == null || problems.isEmpty()) {
                    log.warn("[채점 대상 없음] gameId={} | 저장된 문제가 없습니다", gameId);
                }

                for (int i = 0; i < problems.size(); i++) {
                    Object obj = problems.get(i);

                    if (obj instanceof RedisGameProblem problem &&
                            problem.getAnswer() == null &&
                            problem.getBatterName().equals(prevBatter.getName())) {

                        log.info("[채점 대상 발견] 문제ID={} | 유형={} | 타자={}", problem.getProblemId(), problem.getQuestionCode(), problem.getBatterName());

                        String answer = determineAnswer(problem, prevBatter);

                        if (answer == null) {
                            log.warn("[정답 미정] 문제ID={} | 타자={} | summary 또는 pitchResults 부족", problem.getProblemId(), problem.getBatterName());
                            continue;
                        }

                        RedisGameProblem updated = RedisGameProblem.builder()
                                .problemId(problem.getProblemId())
                                .gameId(problem.getGameId())
                                .inning(problem.getInning())
                                .attackTeam(problem.getAttackTeam())
                                .batterName(problem.getBatterName())
                                .questionCode(problem.getQuestionCode())
                                .description(problem.getDescription())
                                .options(problem.getOptions())
                                .answer(answer)
                                .timestamp(problem.getTimestamp())
                                .build();

                        listOps.set(redisKey, i, updated);
                        log.info("[문제 정답 확정] 문제ID={} | 유형={} | 타자={} | 정답={}",
                                problem.getProblemId(), problem.getQuestionCode(), problem.getBatterName(), answer);
                    }
                }
            }

            // 2. 새 문제 출제
            List<RedisGameProblem> problems = problemGenerator.generateAllProblems(gameId, currentRelay);
            String redisKey = "game:" + LocalDate.now() + ":" + gameId + ":problem";
            ListOperations<String, Object> listOps = redisTemplate2.opsForList();

            // 문제 저장
            for (RedisGameProblem problem : problems) {
                listOps.rightPush(redisKey, problem);
                log.info("[문제 생성] {} | 문제ID: {}", problem.getDescription(), problem.getProblemId());
            }

            // SSE 전송용 문제 랜덤 1개 선택
            if (!problems.isEmpty()) {
                int randomIndex = new Random().nextInt(problems.size());
                RedisGameProblem selected = problems.get(randomIndex);
                selected.setPush(true); 

                // 해당 문제만 push 여부 업데이트
                listOps.set(redisKey, redisTemplate2.opsForList().size(redisKey) - problems.size() + randomIndex, selected);

                // SSE 전송
                // SSE 전송
                sseService.send(gameId, selected);

                // 콘솔 확인용 출력 (description 포함)
                System.out.println("📢 [SSE 전송됨] gameId=" + gameId +
                        " | 문제ID=" + selected.getProblemId() +
                        " | 내용=" + selected.getDescription());

                log.info("[SSE 문제 전송] gameId={} | 문제ID={} | 내용={}",
                        gameId, selected.getProblemId(), selected.getDescription());

            }


            previousBatterMap.put(gameId, currentBatterName);
        }
    }

    private String determineAnswer(RedisGameProblem problem, PlayerRelayInfo prevBatter) {
        String summary = prevBatter.getSummaryText();
        List<String> pitchResults = prevBatter.getPitchResults();
        String questionCode = problem.getQuestionCode();

        if (questionCode == null) return null;

        switch (QuestionCode.valueOf(questionCode)) {
            case PITCH_COUNT:
                if (pitchResults == null) return null;
                int pitchCount = pitchResults.size();
                if (pitchCount <= 3) return "① 1~3구";
                else if (pitchCount <= 6) return "② 4~6구";
                else return "③ 7구 이상";

            case ON_BASE:
                if (summary == null) return null;
                if (summary.contains("출루") || summary.contains("안타") || summary.contains("2루타") ||
                        summary.contains("3루타") || summary.contains("홈런") || summary.contains("볼넷") ||
                        summary.contains("데드볼") || summary.contains("사구") || summary.contains("몸에 맞는 볼") ||
                        summary.contains("실책")) {
                    return "O";
                }
                return "X";

            case STRIKE_OUT:
                if (summary == null) return null;
                return summary.contains("삼진") ? "O" : "X";

            case STRIKE_SEQUENCE:
                if (pitchResults == null || pitchResults.size() < 2) return "X";
                int count = 0;
                for (String pitch : pitchResults) {
                    if (pitch.contains("스트라이크") || pitch.contains("헛스윙") || pitch.contains("파울") || pitch.contains("번트") || pitch.contains("낫아웃")) {
                        count++;
                    } else break;
                }
                return count >= 2 ? "O" : "X";

            case HOME_RUN:
                if (summary == null) return null;
                return summary.contains("홈런") ? "O" : "X";

            case PITCHER_DUTY:
                String inningPitched = prevBatter.getInningPitched();
                if (inningPitched == null) return null;
                double inning;
                try {
                    inning = Double.parseDouble(inningPitched);
                } catch (NumberFormatException e) {
                    return null;
                }
                if (inning <= 5.0) return "① 5이닝 이하";
                else if (inning <= 7.0) return "② 6~7이닝";
                else return "③ 8이닝 이상";

            case RECORD_RBI:
                if (summary == null) return null;
                if (summary.contains("타점") || summary.contains("득점") || summary.contains("홈 인") || summary.contains("2루 주자 득점")) {
                    return "O";
                }
                return "X";

            case FIRST_ON_BASE:
                if (summary == null) return null;
                if (summary.contains("출루") || summary.contains("안타") || summary.contains("2루타") ||
                        summary.contains("3루타") || summary.contains("홈런") || summary.contains("볼넷") ||
                        summary.contains("사구") || summary.contains("몸에 맞는 볼") || summary.contains("실책")) {
                    return "O";
                }
                return "X";

            case NO_RUN_ALLOWED:
                if (summary == null) return "O";
                if (summary.contains("득점") || summary.contains("실점") || summary.contains("홈 인") || summary.contains("타점")) {
                    return "X";
                }
                return "O";

            case LAST_BATTER:
                if (summary == null) return "X";
                if (summary.contains("마지막") || summary.contains("3아웃") || summary.contains("경기 종료") || summary.contains("경기 끝")) {
                    return "O";
                }
                return "X";

            default:
                return null;
        }
    }
}
