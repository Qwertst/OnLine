package ru.hse.online.service;

import ru.hse.online.model.UserStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StatisticsService {
    List<UserStatistics> getStatisticsForPeriod(UUID userId, String name, LocalDate start, LocalDate end);

    void saveStatistics(List<UserStatistics> UserStats);
}
