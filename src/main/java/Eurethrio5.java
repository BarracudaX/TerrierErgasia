import org.terrier.indexing.Collection;
import org.terrier.indexing.tokenisation.EnglishTokeniser;
import org.terrier.indexing.tokenisation.Tokeniser;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public class Eurethrio5 {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Expected one argument:path to setup properties file,which contains these key/values:\n" +
                    "1)Key:save_indexer_here,Value:an absolute path to the folder where the inverted index should be saved.\n" +
                    "2)Key:collection_file,Value:an absolute path to a collection file which contains absolute paths to files that should be indexed.\n" +
                    "3)Key:terrier_properties_file,Value:an absolute path to terrier properties file.");
            System.exit(-1);
        }
        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader(args[0])));
        System.setProperty("terrier.setup", properties.getProperty("terrier_properties_file"));

        Tokeniser tokeniser = new FrequencyTokeniser(new EnglishTokeniser(), 8);

        Indexer indexer = new BasicIndexer(properties.getProperty("save_indexer_here"), "data");
        indexer.createDirectIndex(new Collection[]{
                new Eurethrio4.MyCollection(
                        Eurethrio4.getDocuments(properties.getProperty("collection_file"), tokeniser)
                )
        });
    }

}
