package com.dmus.utils;

public enum InterviewLevel {
    Junior(1),
    Middle(2),
    Senior(3);

    final long levelNum;

    InterviewLevel(long lvl) {
        this.levelNum = lvl;
    }

    public long getLevelNum() { return levelNum; }

    public static InterviewLevel getLevelByLong(long level) {
        for (InterviewLevel lvl : InterviewLevel.values()) {
            if (lvl.levelNum == level) {
                return lvl;
            }
        }
        return null;
    }
}
