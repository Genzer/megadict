package com.megadict.format.dict.index.segment;

import java.text.Collator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class SegmentStore {
    
    private TreeMap<String, Segment> map;

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
        Collator collator = Collator.getInstance(new Locale("vi", "VN"));
        collator.setStrength(Collator.SECONDARY);
        map = new TreeMap<String, Segment>(collator);
    }

    private void putAllToMap(Collection<Segment> segments) {
        for (Segment segment : segments) {
            map.put(segment.upperbound(), segment);
        }
    }

    public Segment findSegmentPossiblyContains(String word) {
        return find(word);
    }

    private Segment find(String findingWord) {
        Segment result = null;

        SortedMap<String, Segment> greaterOrEqualKeys = map.tailMap(findingWord);

        if (!greaterOrEqualKeys.isEmpty()) {

            String firstMatched = greaterOrEqualKeys.firstKey();

            if (areTheSame(firstMatched, findingWord) && hasMoreElement(greaterOrEqualKeys)) {
                String keyOfNextSegment = nextKey(greaterOrEqualKeys, findingWord);
                firstMatched = keyOfNextSegment;
            }

            result = greaterOrEqualKeys.get(firstMatched);
        }

        return result;
    }

    private boolean areTheSame(String matchedKey, String word) {
        return matchedKey.equalsIgnoreCase(word);
    }

    private boolean hasMoreElement(SortedMap<String, Segment> map) {
        return map.size() != 1;
    }

    private String nextKey(SortedMap<String, Segment> map, String keyToFind) {
        Iterator<String> keys = map.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equalsIgnoreCase(keyToFind)) {
                return keys.next();
            }
        }

        return null;
    }
}
