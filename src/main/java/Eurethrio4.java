import org.terrier.indexing.*;
import org.terrier.indexing.Collection;
import org.terrier.indexing.tokenisation.EnglishTokeniser;
import org.terrier.indexing.tokenisation.Tokeniser;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class Eurethrio4 {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Expected one argument:path to setup properties file,which contains four key/values." +
                    "The setup properties file must contain these properties(key/value pairs):\n" +
                    "1)Key:path_to_acronyms,Value:an absolute path to properties file containing acronyms.\n" +
                    "2)Key:save_indexer_here,Value:an absolute path to the folder where the inverted index should be saved.\n" +
                    "3)Key:collection_file,Value:an absolute path to a collection file which contains absolute paths to files that should be indexed.\n" +
                    "4)Key:terrier_properties_file,Value:an absolute path to terrier properties file.");
            System.exit(-1);
        }
        Properties properties = new Properties();
        properties.load(new BufferedReader(new FileReader(args[0])));
        System.setProperty("terrier.setup", properties.getProperty("terrier_properties_file"));

        Properties acronyms = new Properties();
        acronyms.load(new BufferedReader(new FileReader(properties.getProperty("path_to_acronyms"))));

        Tokeniser tokeniser = new AcronymTokeniser(new EnglishTokeniser(), acronyms);

        Indexer indexer = new BasicIndexer(properties.getProperty("save_indexer_here"), "data");
        indexer.createDirectIndex(new Collection[]{
                new MyCollection(getDocuments(properties.getProperty("collection_file"), tokeniser))
        });
    }

    public static List<Document> getDocuments(String collectionPath,Tokeniser tokeniser) throws IOException {
        return Files
                .lines(Path.of(collectionPath))
                .map(Path::of)
                .filter(path -> path.isAbsolute() && path.toFile().exists() && path.toFile().isFile())
                .map(Path::toFile)
                .map(file -> {
                    MyTaggedDocument document = null;
                    try {
                        document = new MyTaggedDocument(
                                new FileInputStream(file),
                                tokeniser, "DOC", "TEXT"
                        );
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return document;
                }).filter(taggedDocument -> taggedDocument != null)
                .collect(toList());
    }

    public static class MyCollection implements Collection {

        private Iterator<Document> iterator;
        private final List<Document> collection;

        public MyCollection(List<Document> collection) {
            this.iterator = collection.iterator();
            this.collection = collection;
        }

        @Override
        public boolean nextDocument() {
            return iterator.hasNext();
        }

        @Override
        public Document getDocument() {
            return iterator.next();
        }

        @Override
        public boolean endOfCollection() {
            return iterator.hasNext();
        }

        @Override
        public void reset() {
            this.iterator = collection.iterator();
        }

        @Override
        public void close() throws IOException {
            this.iterator = null;
        }
    }

}
