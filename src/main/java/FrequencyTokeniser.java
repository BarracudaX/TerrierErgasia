import org.terrier.indexing.tokenisation.TokenStream;
import org.terrier.indexing.tokenisation.Tokeniser;

import java.io.Reader;

public class FrequencyTokeniser extends Tokeniser{

    private final Tokeniser delegatingTokeniser;
    private final int maximumAppearances;

    public FrequencyTokeniser(Tokeniser delegatingTokeniser, int maximumAppearances) {
        this.delegatingTokeniser = delegatingTokeniser;
        this.maximumAppearances = maximumAppearances;
    }

    @Override
    public TokenStream tokenise(Reader reader) {
        return new FrequencyTokenStream(delegatingTokeniser.tokenise(reader), maximumAppearances);
    }
}
