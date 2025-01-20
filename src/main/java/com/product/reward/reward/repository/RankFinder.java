package com.product.reward.reward.repository;

import java.util.Map;

public interface RankFinder {

    Map<Long, Integer> dailyTopComics(int top);
    Map<Long, Integer> weeklyTopComics(int top);
    Map<Long, Integer> monthlyTopComics(int top);
    Map<Long, Integer> yearlyTopComics(int top);

}
