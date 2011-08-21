package com.megadict.format.dict.index;

import java.util.Set;

interface IndexFileReader {
    Index getIndexOf(String headword);
    Set<Index> getIndexesSurrounding(String headWordInclusive);
}
