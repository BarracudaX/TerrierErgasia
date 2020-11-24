import org.terrier.indexing.tokenisation.EnglishTokeniser;
import org.terrier.indexing.tokenisation.Tokeniser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class QuickTest {


    public static void main(String[] args) throws FileNotFoundException {
        Tokeniser tokeniser = new EnglishTokeniser();
        MyTaggedDocument myTaggedDocument = new MyTaggedDocument(
                new FileInputStream("/home/barracuda/Desktop/TerrierProject/MiniCollectionVirtual/my/File1.txt"),
                tokeniser,
                "DOC",
                "TEXT"
        );

        while (!myTaggedDocument.endOfDocument()) {
            System.out.println(myTaggedDocument.getNextTerm());
        }

    }

}
