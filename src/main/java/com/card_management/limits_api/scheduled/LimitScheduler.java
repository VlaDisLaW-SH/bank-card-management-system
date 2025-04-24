package com.card_management.limits_api.scheduled;

import com.card_management.limits_api.enumeration.LimitType;
import com.card_management.limits_api.model.Limit;
import com.card_management.limits_api.repository.LimitRepository;
import com.card_management.limits_api.service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitScheduler {

    private final LimitService limitService;

    private final LimitRepository limitRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyLimits() {
        limitService.resetLimitsByType(LimitType.DAILY);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyLimits() {
        limitService.resetLimitsByType(LimitType.MONTHLY);
    }

    @Scheduled(cron = "0 1 0 * * ?")
    public void applyDailyLimitUpdates() {
        List<Limit> dailyLimits = limitRepository
                .findByLimitTypeAndHasPendingUpdate(LimitType.DAILY, true);
        applyPendingUpdates(dailyLimits);
    }

    @Scheduled(cron = "0 1 0 1 * ?")
    public void applyMonthlyLimitUpdates() {
        List<Limit> monthlyLimits = limitRepository
                .findByLimitTypeAndHasPendingUpdate(LimitType.MONTHLY, true);
        applyPendingUpdates(monthlyLimits);
    }

    private void applyPendingUpdates(List<Limit> limits) {
        for (Limit limit : limits) {
            if (limit.getPendingLimitAmount() != null) {
                limit.setLimitAmount(limit.getPendingLimitAmount());
            }
            limit.setHasPendingUpdate(false);
            limitRepository.save(limit);
        }
    }
}
