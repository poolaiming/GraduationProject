package com.example.newsplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.newsplatform.entity.BrowseHistory;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.NewsCollect;
import com.example.newsplatform.entity.NewsComment;
import com.example.newsplatform.entity.NewsLike;
import com.example.newsplatform.entity.NewsTag;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.mapper.BrowseHistoryMapper;
import com.example.newsplatform.mapper.NewsCollectMapper;
import com.example.newsplatform.mapper.NewsCommentMapper;
import com.example.newsplatform.mapper.NewsLikeMapper;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.mapper.NewsTagMapper;
import com.example.newsplatform.service.NewsService;
import com.example.newsplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    private static final double COLLECT_WEIGHT = 1.0;
    private static final double LIKE_WEIGHT = 0.7;
    private static final double CF_WEIGHT = 0.65;
    private static final double TAG_WEIGHT = 0.15;
    private static final double HOT_WEIGHT = 0.12;
    private static final double FRESH_WEIGHT = 0.08;

    private final NewsTagMapper newsTagMapper;
    private final UserService userService;
    private final NewsLikeMapper newsLikeMapper;
    private final NewsCollectMapper newsCollectMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final NewsCommentMapper newsCommentMapper;

    @Override
    public Page<News> pageNews(Long categoryId, Long authorId, String keyword, String authorName, Long tagId, String sortBy, int pageNum, int pageSize) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(News::getCategoryId, categoryId);
        }
        if (authorId != null) {
            wrapper.eq(News::getAuthorId, authorId);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(News::getTitle, keyword);
        }
        if (StringUtils.hasText(authorName)) {
            List<Long> matchedAuthorIds = userService.lambdaQuery()
                    .and(q -> q.like(User::getUsername, authorName).or().like(User::getNickname, authorName))
                    .list()
                    .stream()
                    .map(User::getId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            if (matchedAuthorIds.isEmpty()) {
                return new Page<>(pageNum, pageSize, 0);
            }
            wrapper.in(News::getAuthorId, matchedAuthorIds);
        }
        if (tagId != null) {
            List<Long> newsIds = newsTagMapper.selectList(new LambdaQueryWrapper<NewsTag>()
                            .eq(NewsTag::getTagId, tagId))
                    .stream().map(NewsTag::getNewsId).distinct().collect(Collectors.toList());
            if (newsIds.isEmpty()) {
                return new Page<>(pageNum, pageSize, 0);
            }
            wrapper.in(News::getId, newsIds);
        }
        if (authorId == null) {
            wrapper.eq(News::getStatus, 2);
        }
        if ("likeCount".equals(sortBy)) {
            wrapper.orderByDesc(News::getLikeCount);
        } else if ("collectCount".equals(sortBy)) {
            wrapper.orderByDesc(News::getCollectCount);
        } else if (authorId != null) {
            wrapper.orderByDesc(News::getCreateTime);
        } else {
            wrapper.orderByDesc(News::getPublishTime);
        }
        Page<News> page = this.page(Page.of(pageNum, pageSize), wrapper);

        List<Long> authorIds = page.getRecords().stream()
                .map(News::getAuthorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        if (!authorIds.isEmpty()) {
            var authorMap = userService.listByIds(authorIds).stream()
                    .collect(Collectors.toMap(User::getId,
                            u -> StringUtils.hasText(u.getNickname()) ? u.getNickname() : u.getUsername()));
            page.getRecords().forEach(n -> n.setAuthorName(authorMap.getOrDefault(n.getAuthorId(), "--")));
        }

        return page;
    }

    @Override
    public void increaseViewCount(Long newsId) {
        this.lambdaUpdate()
                .eq(News::getId, newsId)
                .setSql("view_count = view_count + 1")
                .update();
    }

    @Override
    public List<News> recommendNews(Long userId, int size) {
        int safeSize = Math.max(1, Math.min(size, 20));
        LocalDateTime now = LocalDateTime.now();

        Set<Long> likedNewsIds = findLikedNewsIds(userId);
        Set<Long> collectedNewsIds = findCollectedNewsIds(userId);
        Set<Long> browsedNewsIds = findBrowsedNewsIds(userId);
        Set<Long> strongExcludedIds = mergeSets(likedNewsIds, collectedNewsIds, browsedNewsIds);
        Set<Long> softExcludedIds = mergeSets(likedNewsIds, collectedNewsIds);
        Set<Long> interestNewsIds = mergeSets(likedNewsIds, collectedNewsIds);

        List<News> publishedNews = this.list(new LambdaQueryWrapper<News>().eq(News::getStatus, 2));
        if (publishedNews.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> publishedNewsIds = publishedNews.stream()
                .map(News::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, Set<Long>> newsTagMap = buildNewsTagMap(publishedNewsIds);
        Map<Long, Long> commentCountMap = buildCommentCountMap(publishedNewsIds);
        Set<Long> interestTagIds = buildInterestTagIds(interestNewsIds);
        Map<Long, Map<Long, Double>> userPreferenceMap = buildUserPreferenceMap();
        Map<Long, Double> myPreferences = userPreferenceMap.getOrDefault(userId, Collections.emptyMap());
        Map<Long, Double> userSimilarityMap = myPreferences.isEmpty()
                ? Collections.emptyMap()
                : buildUserSimilarityMap(userId, myPreferences, userPreferenceMap);

        List<News> rankedNews = rankRecommendations(
                publishedNews,
                strongExcludedIds,
                interestTagIds,
                userSimilarityMap,
                userPreferenceMap,
                newsTagMap,
                commentCountMap,
                now
        );

        List<News> result = new ArrayList<>(rankedNews.stream().limit(safeSize).toList());
        if (result.size() < safeSize) {
            Set<Long> existingIds = result.stream()
                    .map(News::getId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            List<News> fallbackNews = publishedNews.stream()
                    .filter(news -> news.getId() != null)
                    .filter(news -> !softExcludedIds.contains(news.getId()))
                    .filter(news -> !existingIds.contains(news.getId()))
                    .sorted(Comparator.comparingDouble((News news) -> buildHotScore(news, commentCountMap) + timeBonus(news.getPublishTime(), now)).reversed())
                    .limit(safeSize - result.size())
                    .toList();
            result.addAll(fallbackNews);
        }

        return result;
    }

    private Set<Long> findLikedNewsIds(Long userId) {
        return newsLikeMapper.selectList(new LambdaQueryWrapper<NewsLike>().eq(NewsLike::getUserId, userId))
                .stream()
                .map(NewsLike::getNewsId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private Set<Long> findCollectedNewsIds(Long userId) {
        return newsCollectMapper.selectList(new LambdaQueryWrapper<NewsCollect>().eq(NewsCollect::getUserId, userId))
                .stream()
                .map(NewsCollect::getNewsId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private Set<Long> findBrowsedNewsIds(Long userId) {
        return browseHistoryMapper.selectList(new LambdaQueryWrapper<BrowseHistory>().eq(BrowseHistory::getUserId, userId))
                .stream()
                .map(BrowseHistory::getNewsId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private Set<Long> buildInterestTagIds(Set<Long> interestNewsIds) {
        if (interestNewsIds.isEmpty()) {
            return Collections.emptySet();
        }
        return newsTagMapper.selectList(new LambdaQueryWrapper<NewsTag>().in(NewsTag::getNewsId, interestNewsIds))
                .stream()
                .map(NewsTag::getTagId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
    }

    private Map<Long, Set<Long>> buildNewsTagMap(Set<Long> newsIds) {
        if (newsIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Set<Long>> newsTagMap = new HashMap<>();
        List<NewsTag> relations = newsTagMapper.selectList(new LambdaQueryWrapper<NewsTag>().in(NewsTag::getNewsId, newsIds));
        for (NewsTag relation : relations) {
            if (relation.getNewsId() == null || relation.getTagId() == null) {
                continue;
            }
            newsTagMap.computeIfAbsent(relation.getNewsId(), key -> new HashSet<>()).add(relation.getTagId());
        }
        return newsTagMap;
    }

    private Map<Long, Long> buildCommentCountMap(Set<Long> newsIds) {
        if (newsIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, Long> commentCountMap = new HashMap<>();
        List<NewsComment> comments = newsCommentMapper.selectList(new LambdaQueryWrapper<NewsComment>().in(NewsComment::getNewsId, newsIds));
        for (NewsComment comment : comments) {
            if (comment.getNewsId() == null) {
                continue;
            }
            commentCountMap.merge(comment.getNewsId(), 1L, Long::sum);
        }
        return commentCountMap;
    }

    private Map<Long, Map<Long, Double>> buildUserPreferenceMap() {
        Map<Long, Map<Long, Double>> userPreferenceMap = new HashMap<>();
        List<NewsLike> allLikes = newsLikeMapper.selectList(new LambdaQueryWrapper<>());
        for (NewsLike like : allLikes) {
            addPreference(userPreferenceMap, like.getUserId(), like.getNewsId(), LIKE_WEIGHT);
        }
        List<NewsCollect> allCollects = newsCollectMapper.selectList(new LambdaQueryWrapper<>());
        for (NewsCollect collect : allCollects) {
            addPreference(userPreferenceMap, collect.getUserId(), collect.getNewsId(), COLLECT_WEIGHT);
        }
        return userPreferenceMap;
    }

    private void addPreference(Map<Long, Map<Long, Double>> userPreferenceMap, Long userId, Long newsId, double weight) {
        if (userId == null || newsId == null) {
            return;
        }
        userPreferenceMap.computeIfAbsent(userId, key -> new HashMap<>())
                .merge(newsId, weight, Double::sum);
    }

    private Map<Long, Double> buildUserSimilarityMap(Long userId, Map<Long, Double> myPreferences, Map<Long, Map<Long, Double>> userPreferenceMap) {
        double myNorm = vectorNorm(myPreferences);
        if (myNorm <= 0) {
            return Collections.emptyMap();
        }

        Map<Long, Double> similarityMap = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : userPreferenceMap.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId == null || otherUserId.equals(userId)) {
                continue;
            }
            Map<Long, Double> otherPreferences = entry.getValue();
            double similarity = cosineSimilarity(myPreferences, myNorm, otherPreferences);
            if (similarity > 0) {
                similarityMap.put(otherUserId, similarity);
            }
        }
        return similarityMap;
    }

    private double cosineSimilarity(Map<Long, Double> myPreferences, double myNorm, Map<Long, Double> otherPreferences) {
        if (otherPreferences == null || otherPreferences.isEmpty()) {
            return 0;
        }

        double numerator = 0;
        for (Map.Entry<Long, Double> entry : myPreferences.entrySet()) {
            Double otherWeight = otherPreferences.get(entry.getKey());
            if (otherWeight != null) {
                numerator += entry.getValue() * otherWeight;
            }
        }
        if (numerator <= 0) {
            return 0;
        }

        double otherNorm = vectorNorm(otherPreferences);
        if (otherNorm <= 0) {
            return 0;
        }
        return numerator / (myNorm * otherNorm);
    }

    private double vectorNorm(Map<Long, Double> preferences) {
        double squareSum = 0;
        for (Double value : preferences.values()) {
            if (value != null) {
                squareSum += value * value;
            }
        }
        return Math.sqrt(squareSum);
    }

    private List<News> rankRecommendations(List<News> publishedNews,
                                           Set<Long> excludedIds,
                                           Set<Long> interestTagIds,
                                           Map<Long, Double> userSimilarityMap,
                                           Map<Long, Map<Long, Double>> userPreferenceMap,
                                           Map<Long, Set<Long>> newsTagMap,
                                           Map<Long, Long> commentCountMap,
                                           LocalDateTime now) {
        Map<Long, Double> scoreMap = new HashMap<>();
        Map<Long, News> newsMap = new HashMap<>();

        for (News news : publishedNews) {
            Long newsId = news.getId();
            if (newsId == null || excludedIds.contains(newsId)) {
                continue;
            }

            double cfScore = calculateCollaborativeScore(newsId, userSimilarityMap, userPreferenceMap);
            double tagScore = calculateTagScore(newsId, interestTagIds, newsTagMap);
            double hotScore = buildHotScore(news, commentCountMap);
            double freshScore = timeBonus(news.getPublishTime(), now);
            double finalScore = cfScore * CF_WEIGHT
                    + tagScore * TAG_WEIGHT
                    + hotScore * HOT_WEIGHT
                    + freshScore * FRESH_WEIGHT;

            scoreMap.put(newsId, finalScore);
            newsMap.put(newsId, news);
        }

        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .map(newsMap::get)
                .filter(news -> news != null)
                .toList();
    }

    private double calculateCollaborativeScore(Long newsId,
                                               Map<Long, Double> userSimilarityMap,
                                               Map<Long, Map<Long, Double>> userPreferenceMap) {
        double score = 0;
        for (Map.Entry<Long, Double> similarityEntry : userSimilarityMap.entrySet()) {
            Map<Long, Double> otherPreferences = userPreferenceMap.getOrDefault(similarityEntry.getKey(), Collections.emptyMap());
            Double preferenceWeight = otherPreferences.get(newsId);
            if (preferenceWeight != null) {
                score += similarityEntry.getValue() * preferenceWeight;
            }
        }
        return score;
    }

    private double calculateTagScore(Long newsId, Set<Long> interestTagIds, Map<Long, Set<Long>> newsTagMap) {
        if (interestTagIds.isEmpty()) {
            return 0;
        }
        Set<Long> newsTags = newsTagMap.getOrDefault(newsId, Collections.emptySet());
        long overlap = newsTags.stream().filter(interestTagIds::contains).count();
        return overlap * 0.25;
    }

    private double buildHotScore(News news, Map<Long, Long> commentCountMap) {
        long view = news.getViewCount() == null ? 0L : news.getViewCount();
        long like = news.getLikeCount() == null ? 0L : news.getLikeCount();
        long collect = news.getCollectCount() == null ? 0L : news.getCollectCount();
        long comment = commentCountMap.getOrDefault(news.getId(), 0L);
        return view * 0.02 + like * 0.35 + collect * 0.45 + comment * 0.30;
    }

    private double timeBonus(LocalDateTime publishTime, LocalDateTime now) {
        if (publishTime == null) {
            return 0;
        }
        long days = Math.max(0, Duration.between(publishTime, now).toDays());
        if (days <= 1) {
            return 1.0;
        }
        if (days <= 3) {
            return 0.6;
        }
        if (days <= 7) {
            return 0.3;
        }
        return 0;
    }

    @SafeVarargs
    private Set<Long> mergeSets(Set<Long>... sets) {
        Set<Long> merged = new HashSet<>();
        for (Set<Long> set : sets) {
            merged.addAll(set);
        }
        return merged;
    }
}
