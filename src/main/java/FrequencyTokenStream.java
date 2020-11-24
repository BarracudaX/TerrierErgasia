import org.terrier.indexing.tokenisation.TokenStream;

import java.util.HashMap;
import java.util.Map;

public class FrequencyTokenStream extends TokenStream {

    private static int MAXIMUM_APPEARANCES = 10;
    private final Map<String,Integer> tokenFrequency = new HashMap<>();
    private final TokenStream delegatingTokenStream;

    public FrequencyTokenStream(TokenStream delegatingTokenStream, int maximumAppearances) {
        this.delegatingTokenStream = delegatingTokenStream;
        MAXIMUM_APPEARANCES = maximumAppearances;
        prepareFrequencies();
    }

    private void prepareFrequencies() {
        while (delegatingTokenStream.hasNext()) {
            String nextToken = delegatingTokenStream.next();
            tokenFrequency.put(nextToken, tokenFrequency.computeIfAbsent(nextToken, s -> 0) + 1);
        }
    }

    @Override
    public boolean hasNext() {
        return tokenFrequency.values()
                .stream()
                .parallel()
                .anyMatch(frequency -> frequency <= MAXIMUM_APPEARANCES);
    }

    @Override
    public String next() {
        Map.Entry<String, Integer> nextToken = tokenFrequency
                .entrySet()
                .stream().parallel()
                .filter(entry -> entry.getValue() <= MAXIMUM_APPEARANCES)
                .findAny().orElse(null);

        if (nextToken != null) {
            tokenFrequency.remove(nextToken.getKey());
            return nextToken.getKey();
        }

        return null;
    }
}
