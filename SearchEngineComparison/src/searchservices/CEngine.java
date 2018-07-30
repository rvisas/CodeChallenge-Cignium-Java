package searchservices;

public class CEngine {
    private String enginename;
    private String url;
    private String patternSearchIni;
    private String patternSearchFin;
    
    public CEngine(String enginename, String url, String patternSearchIni, String patternSearchFin) {
        this.enginename = enginename;
        this.url = url;
        this.patternSearchIni = patternSearchIni;
        this.patternSearchFin = patternSearchFin;
    }
    
    public String getEngineName() {
        return this.enginename;
    }
    
    public String getURL() {
        return this.url;
    }
    
    public String getPatternSearchIni() {
        return this.patternSearchIni;
    }
    
    public String getPatternSearchFin() {
        return this.patternSearchFin;
    }
}