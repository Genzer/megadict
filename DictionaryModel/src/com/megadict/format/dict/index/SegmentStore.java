package com.megadict.format.dict.index;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;

class SegmentStore {

    public SegmentStore(NavigableMap<String, Segment> sortedMapOfSegments) {
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
            map.put(segment.lowerbound(), segment);
        }
    }
    

    public Segment getSegmentContains(String word) {
        return extract(word);
    }

    private Segment extract(String word) {
        NavigableMap<String, Segment> lessOrEqual = map.headMap(word, INCLUSIVE);
        String keyOfFirstMatch = lessOrEqual.lastKey();
        return lessOrEqual.get(keyOfFirstMatch);
    }

    private static final boolean INCLUSIVE = true;
    private NavigableMap<String, Segment> map;
}
