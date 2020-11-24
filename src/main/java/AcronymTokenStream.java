import org.terrier.indexing.tokenisation.TokenStream;
import org.terrier.indexing.tokenisation.Tokeniser;

import java.io.StringReader;
import java.util.Properties;

import static org.terrier.indexing.tokenisation.Tokeniser.EMPTY_STREAM;

public class AcronymTokenStream extends TokenStream {
    private final TokenStream delegatingTokenStream;
    private TokenStream acronymsStream = EMPTY_STREAM;
    private final Tokeniser tokeniser;
    private final Properties acronyms;

    public AcronymTokenStream(TokenStream delegatingTokenStream, Properties acronyms, Tokeniser tokeniser) {
        this.delegatingTokenStream = delegatingTokenStream;
        this.acronyms = acronyms;
        this.tokeniser = tokeniser;
    }

    @Override
    public boolean hasNext() {
        return acronymsStream.hasNext() || this.delegatingTokenStream.hasNext();
    }

    @Override
    public String next() {
        if (acronymsStream.hasNext()) {
            return acronymsStream.next();
        }
        String nextToken = this.delegatingTokenStream.next();

        if (nextToken == null) {
            return null;
        }

        String acronym = acronyms.getProperty(nextToken.toLowerCase());

        if (acronym != null) {
            acronymsStream = new AcronymTokeniser(tokeniser, acronyms)
                    .tokenise(new StringReader(acronym));
            return acronymsStream.next();
        }

        return nextToken;
    }
}
