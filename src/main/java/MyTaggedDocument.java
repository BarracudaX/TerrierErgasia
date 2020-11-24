import org.terrier.indexing.Document;
import org.terrier.indexing.tokenisation.TokenStream;
import org.terrier.indexing.tokenisation.Tokeniser;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MyTaggedDocument implements Document {

    private TokenStream tokenStream = Tokeniser.EMPTY_STREAM;
    private final BufferedReader bufferedInputStream;
    private final String docTag;
    private final String body;
    private boolean endOfDocument = false;
    private final Tokeniser tokeniser;
    private int lastCharacter = -1;

    public MyTaggedDocument(InputStream inputStream, Tokeniser tokeniser, String docTag, String body) {
        this.bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream));
        this.docTag = docTag;
        this.tokeniser = tokeniser;
        this.body = body;
    }

    @Override
    public String getNextTerm() {
        if (tokenStream.hasNext()) {
            return tokenStream.next();
        }

        try {
            if (processTillThisTag(docTag) && processTillThisTag(body)) {
                tokenStream = tokeniser
                        .tokenise(new StringReader(readBeforeThis("</" + body + ">")));
                return getNextTerm();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String readBeforeThis(String str) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean reachedTheGivenStr = false;
        int character = lastCharacter;

        do {
            if (str.charAt(0) == character) {
                StringBuilder reachedStr = new StringBuilder();
                reachedStr.append(str.charAt(0));
                character = bufferedInputStream.read();
                for (int i = 0; i < str.length() - 1 && character != -1; i++) {
                    reachedStr.append((char) character);
                    character = bufferedInputStream.read();
                }
                if (reachedStr.toString().equals(str)) {
                    reachedTheGivenStr = true;
                    break;
                }
            }
            stringBuilder.append((char) character);

        } while ((character = bufferedInputStream.read()) != -1);

        if (!reachedTheGivenStr) {
            throw new IllegalArgumentException("String : " + str + " never reached");
        }

        lastCharacter = bufferedInputStream.read();

        endOfDocument = lastCharacter == -1;

        String result = stringBuilder.toString();
        return result.length() > 0 ? result : null;
    }

    private boolean processTillThisTag(String tag) throws IOException {
        int character = lastCharacter;
        boolean result = false;
        do {
            //an opening tag
            if (character == '<' && (character = bufferedInputStream.read()) != '/') {
                StringBuilder tagBuilder = new StringBuilder();
                tagBuilder.append((char) character);
                character = bufferedInputStream.read();
                while ((character != -1) && character != '>') {
                    tagBuilder.append((char) character);
                    character = bufferedInputStream.read();
                }
                if (tagBuilder.toString().equals(tag)) {
                    result = true;
                    break;
                }
            }

        } while ((character = bufferedInputStream.read()) != -1);

        lastCharacter = bufferedInputStream.read();
        endOfDocument = lastCharacter == -1;

        return result;
    }

    @Override
    public Set<String> getFields() {
        return Collections.emptySet();
    }

    @Override
    public boolean endOfDocument() {
        return endOfDocument;
    }

    @Override
    public Reader getReader() {
        return bufferedInputStream;
    }

    @Override
    public String getProperty(String name) {
        return null;
    }

    @Override
    public Map<String, String> getAllProperties() {
        return Collections.emptyMap();
    }
}
