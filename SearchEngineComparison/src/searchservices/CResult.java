package searchservices;

public class CResult {
    private String enginename;
    private String term;
    private long quantity;
    
    public CResult(String enginename, String term, long quantity) {
        this.enginename = enginename;
        this.term = term;
        this.quantity = quantity;
    }
    
    public void additionate(long number) {
        this.quantity += number;
    }    

    public String getTerm() {
        return this.term;
    }
    
    public String getEngineName() {
        return this.enginename;
    }

    public long getQuantity() {
        return this.quantity;
    }    
}