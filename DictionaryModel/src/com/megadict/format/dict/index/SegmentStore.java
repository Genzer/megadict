package com.megadict.format.dict.index;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class SegmentStore {

    public SegmentStore(SortedMap<String, Segment> sortedMapOfSegments) {
        if (sortedMapOfSegments.isEmpty()) {
            throw new IllegalArgumentException("The map must not be empty");
        }
        this.map = sortedMapOfSegments;
    }

    public SegmentStore(Collection<Segment> segments) {
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("The store doesn't allow empty collection of segments.");
        }
        putAllToMap(segments);
    }

    private void putAllToMap(Collection<Segment> segments) {
        map = new TreeMap<String, Segment>();
        for (Segment segment : segments) {
            map.put(segment.upperbound(), segment);
        }
    }
    

    public Segment getSegmentContains(String word) {
        return extract(word);
    }

    private Segment extract(String word) {
        SortedMap<String, Segment> greaterOrEqual = map.tailMap(word);
        String keyOfFirstMatch = greaterOrEqual.firstKey();
        return greaterOrEqual.get(keyOfFirstMatch);
    }

    private SortedMap<String, Segment> map;
}
