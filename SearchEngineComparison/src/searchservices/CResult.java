package searchservices;
// Stores the result from querying the engines
public class CResult {
    private String engine;
    private String term;
    private long quantity;
    
    public CResult(String engine, String term, long quantity) {
        this.engine = engine;
        this.term = term;
        this.quantity = quantity;
    }
    
    public void additionate(long number) {
        this.quantity = this.quantity + number;
    }
    
    public String getEngine() {
        return this.engine;
    }

    public String getTerm() {
        return this.term;
    }

    public long getQuantity() {
        return this.quantity;
    }    
}