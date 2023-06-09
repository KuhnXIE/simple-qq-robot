package com.shr25.robot.utils;

import java.util.List;
import java.util.Random;

public class ListUtil {

    /**
     * 获取随机元素
     * @param list
     * @return
     * @param <T>
     */
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(list.size());

        return list.get(randomIndex);
    }

}
