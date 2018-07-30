package searchservices;

public class CEngine {
    private String name;
    private String url;
    private String patternSearchIni;
    private String patternSearchFin;
        
    public CEngine() {
        name = "";
        url = "";
        patternSearchIni = "";
        patternSearchFin = "";
    }
    
    public CEngine(String name, String url, String patternSearchIni, String patternSearchFin) {
        this.name = name;
        this.url = url;
        this.patternSearchIni = patternSearchIni;
        this.patternSearchFin = patternSearchFin;
    }
    
    public String getName() {
        return this.name;
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