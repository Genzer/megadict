package com.megadict.format.dict.index.segment;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class SegmentStore {

    public SegmentStore(Collection<Segment> segments) {
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("The store doesn't allow empty collection of segments.");
        }
        addData(segments);
    }
    
    private void addData(Collection<Segment> segments) {
        createKeyCaseInsensitiveMap();
        putAllToMap(segments);
    }
    
    private void createKeyCaseInsensitiveMap() {
        map = new TreeMap<String, Segment>(String.CASE_INSENSITIVE_ORDER);
    }

    private void putAllToMap(Collection<Segment> segments) {       
        for (Segment segment : segments) {
            map.put(segment.upperbound(), segment);
        }
    }

    public Segment findSegmentPossiblyContains(String word) {
        return find(word);
    }

    private Segment find(String word) {
        SortedMap<String, Segment> greaterOrEqual = map.tailMap(word);
        String keyOfFirstMatch = greaterOrEqual.firstKey();
        return greaterOrEqual.get(keyOfFirstMatch);
    }

    private SortedMap<String, Segment> map;
}
