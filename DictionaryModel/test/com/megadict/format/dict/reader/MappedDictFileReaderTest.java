package com.megadict.format.dict.reader;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.junit.Ignore;
import org.junit.Test;

import com.megadict.format.dict.index.Index;
import com.megadict.format.dict.parser.IndexParser;
import com.megadict.format.dict.parser.IndexParsers;

public class MappedDictFileReaderTest {

    private static final File dictFile = new File("C:/test/av.dict");

    @Test
    public void testRead() {

        MappedDictFileReader reader = new MappedDictFileReader(dictFile);

        Index[] indexes = loadIndexes();

        for (Index index : indexes) {
            reader.open();
            byte[] readBytes = reader.read(index.getByteOffset(), index.getByteLength());
            String string = makeString(readBytes);
            System.out.println(string);
            reader.close();
        }

    }

    @Ignore @Test
    public void testWithUnwrappedBuffer() {
        MappedByteBuffer mappedFile = makeReadOnlyMappedFile(makeRandomAccessFile(dictFile));

        Index[] indexes = loadIndexes();

        for (Index index : indexes) {
            byte[] contentInByteArray = new byte[index.getByteLength()];

            mappedFile.position(index.getByteOffset());
            mappedFile.get(contentInByteArray, 0, contentInByteArray.length);
            String content = new String(contentInByteArray);

            assertNotNull(content);
            System.out.println(content);
        }
    }

    private Index[] loadIndexes() {

        String[] indexStrings = { "1-byte character\toN7/\tq", "reclassify\ty3Me\t4", "watch-dog\t14nk\tBx",
                "zymotic\toN2l\tBJ", "decorously\tuSf3\t4", "Forecast error\tqnR8\tDC", "procrastinating\tahbX\tBN",
                "two-master\tk+2K\tBU", "voidable\tm0Yz\tBa", "Z-score\tsmpF\tm", };

        IndexParser parser = IndexParsers.newInstance();

        Index[] result = new Index[indexStrings.length];

        for (int item = 0; item < indexStrings.length; item++) {
            result[item] = parser.parse(indexStrings[item]);
        }

        return result;

    }

    private String makeString(byte[] octets) {
        return new String(octets);
    }

    private RandomAccessFile makeRandomAccessFile(File file) {
        try {
            return new RandomAccessFile(file, "r");
        } catch (FileNotFoundException fnf) {
            throw new RuntimeException("File does not exists.", fnf);
        }
    }

    private MappedByteBuffer makeReadOnlyMappedFile(RandomAccessFile file) {
        FileChannel fileChannel = file.getChannel();
        try {
            return fileChannel.map(MapMode.READ_ONLY, 0, fileChannel.size());
        } catch (IOException e) {
            throw new RuntimeException("cannot create mapped buffer", e);
        }
    }

}
