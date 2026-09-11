package com.gamebasic.ranking.service;

import com.gamebasic.ranking.client.RankingClient;
import com.gamebasic.ranking.dto.response.RankingEntryResponse;
import com.gamebasic.ranking.dto.response.RankingResponse;
import com.gamebasic.ranking.dto.source.BossFightInfo;
import com.gamebasic.ranking.dto.source.BossPhaseInfo;
import com.gamebasic.ranking.dto.source.DeckInfo;
import com.gamebasic.ranking.dto.source.RankingRecord;
import com.gamebasic.ranking.dto.source.RankingSource;
import com.gamebasic.ranking.dto.source.RunInfo;
import com.gamebasic.ranking.dto.source.SourceCard;
import com.gamebasic.ranking.exception.RankingSourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private static final Set<String> CARD_TYPES = Arrays.stream(CardType.values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());

    private static final List<String> BOSS_PHASES = List.of("THRONE", "UNBOUND", "ECLIPSE");

    private static final Comparator<RankingRecord> RANKING_ORDER =
            Comparator.comparingInt((RankingRecord record) -> record.getRun().getDurationSeconds())
                    .thenComparing(Comparator.comparingInt(
                            (RankingRecord record) -> record.getRun().getFinalHp()).reversed())
                    .thenComparingLong(RankingRecord::getId);

    private final RankingClient rankingClient;

    public RankingResponse getRankings() {
        RankingSource source = rankingClient.fetch();
        validateSource(source);

        List<RankingRecord> validRecords = new ArrayList<>();
        int excludedCount = 0;

        for (RankingRecord record : source.getRecords()) {
            // 실패한 게임과 10층 미만 기록은 검증 제외 수에도 포함하지 않습니다.
            if (!isRankingCandidate(record)) {
                continue;
            }
            if (!isValidRecord(record)) {
                excludedCount++;
                continue;
            }
            validRecords.add(record);
        }

        validRecords.sort(RANKING_ORDER);

        Set<String> seenPlayerIds = new HashSet<>();
        List<RankingEntryResponse> entries = new ArrayList<>();
        for (RankingRecord record : validRecords) {
            // 먼저 정렬했으므로 같은 플레이어의 첫 기록이 최고 기록입니다.
            if (!seenPlayerIds.add(record.getPlayer().getId())) {
                continue;
            }
            entries.add(new RankingEntryResponse(
                    entries.size() + 1,
                    record.getPlayer().getName(),
                    record.getRun().getDurationSeconds(),
                    record.getRun().getFinalHp(),
                    record.getBossFight().getTotalTurns(),
                    record.getDeck().getCards().size()
            ));
        }

        return new RankingResponse(
                source.getMeta().getSeason().getId(),
                source.getRecords().size(),
                excludedCount,
                List.copyOf(entries)
        );
    }

    private void validateSource(RankingSource source) {
        if (source == null || source.getMeta() == null || source.getMeta().getSeason() == null
                || source.getMeta().getSeason().getId() == null
                || source.getMeta().getSeason().getId().isBlank()
                || source.getRecords() == null) {
            throw new RankingSourceException("외부 랭킹 응답 형식이 올바르지 않습니다.");
        }
    }

    private boolean isRankingCandidate(RankingRecord record) {
        return record != null && record.getRun() != null
                && "CLEARED".equals(record.getRun().getStatus())
                && Integer.valueOf(10).equals(record.getRun().getClearedFloor());
    }

    private boolean isValidRecord(RankingRecord record) {
        if (record.getId() == null || record.getPlayer() == null
                || record.getPlayer().getId() == null || record.getPlayer().getId().isBlank()
                || record.getPlayer().getName() == null || record.getPlayer().getName().isBlank()) {
            return false;
        }

        RunInfo run = record.getRun();
        if (run.getDurationSeconds() == null
                || run.getDurationSeconds() < run.getClearedFloor() * 30
                || run.getFinalHp() == null || run.getFinalHp() < 1 || run.getFinalHp() > 99) {
            return false;
        }

        DeckInfo deck = record.getDeck();
        if (deck == null || deck.getCards() == null || deck.getSize() == null
                || deck.getCards().size() < 9 || deck.getCards().size() > 20
                || deck.getSize() != deck.getCards().size()) {
            return false;
        }

        Set<String> deckTypes = new HashSet<>();
        for (SourceCard card : deck.getCards()) {
            if (card == null || card.getCardType() == null || !CARD_TYPES.contains(card.getCardType())
                    || card.getAcquiredFloor() == null
                    || card.getAcquiredFloor() < 0 || card.getAcquiredFloor() > 9) {
                return false;
            }
            deckTypes.add(card.getCardType());
        }

        BossFightInfo bossFight = record.getBossFight();
        if (bossFight == null || bossFight.getPhases() == null
                || bossFight.getPhases().size() != BOSS_PHASES.size()
                || bossFight.getTotalTurns() == null
                || !deckTypes.contains(bossFight.getFinishingCard())) {
            return false;
        }

        long totalTurns = 0;
        for (int index = 0; index < BOSS_PHASES.size(); index++) {
            BossPhaseInfo phase = bossFight.getPhases().get(index);
            if (phase == null || !BOSS_PHASES.get(index).equals(phase.getPhase())
                    || phase.getTurns() == null || phase.getTurns() < 1) {
                return false;
            }
            totalTurns += phase.getTurns();
        }
        return totalTurns == bossFight.getTotalTurns();
    }
}
