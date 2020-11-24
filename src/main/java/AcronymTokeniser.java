import org.terrier.indexing.tokenisation.TokenStream;
import org.terrier.indexing.tokenisation.Tokeniser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

public class AcronymTokeniser extends Tokeniser {

    private final Tokeniser delegatingTokeniser;
    private final Properties acronyms;

    public AcronymTokeniser(Tokeniser delegatingTokeniser, Properties acronyms) {
        this.delegatingTokeniser = delegatingTokeniser;
        this.acronyms = acronyms;
    }


    @Override
    public TokenStream tokenise(Reader reader) {
        return new AcronymTokenStream(delegatingTokeniser.tokenise(reader),acronyms,delegatingTokeniser);
    }


}
