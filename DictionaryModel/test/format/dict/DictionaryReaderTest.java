package format.dict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class DictionaryReaderTest {

    @Test
    public void testGetDefinitionByIndex() {
        final String filePath = "C:\\test\\av-d.dict.dz";
        
        final String indexString = "perpetrate\tYzDy\tEo";
        
        IndexParser parser = new IndexTabDilimeterParser();
        
        Index perIndex = parser.parse(indexString);
        assertNotNull(perIndex);
        
        System.out.println(perIndex);
        
        DictionaryReader reader = new DictionaryReader(filePath);
        
        String result = reader.getDefinitionByIndex(perIndex);
        
        String text = "@perpetrate /'pə:pitreit/\n"+
        "*  ngoại động từ\n" +
        "- phạm, gây ra\n" +
        "=to perpetrate a blunder+ phạm một sai lầm\n" +
        "=to perpetrate hostility between two nations+ gây ra thù địch giữa hai nước\n" +
        "- (từ Mỹ,nghĩa Mỹ) trình bày không hay, biểu diễn tồi, thực hiện tồi.";
        
        assertEquals(text, result);
        
        System.out.println(result);        
    }

}
