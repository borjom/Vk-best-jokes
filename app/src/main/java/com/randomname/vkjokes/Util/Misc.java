package com.randomname.vkjokes.Util;

import com.randomname.vkjokes.Models.WallPostModel;

import java.util.Comparator;

public class Misc {
    public static class WallPostModelComparator implements Comparator<WallPostModel> {
        public int compare(WallPostModel left, WallPostModel right) {
            Integer rightId = right.getId();

            return rightId.compareTo(left.getId());
        }
    }
}
