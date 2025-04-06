package org.example.betty.domain.game.service;

import org.example.betty.domain.game.dto.redis.RedisGameSchedule;

import java.util.List;

public interface GameReadService {

    /**
     * 오늘 날짜의 모든 경기 일정 조회
     * - Redis에서 조회 (없으면 DB에서 조회하여 가공)
     */
    List<RedisGameSchedule> getTodayGameSchedules();
}
