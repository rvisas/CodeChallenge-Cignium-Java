package searchservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CSearcher {
    private ArrayList<String> terms;
    private ArrayList<CEngine> engines;
    private ArrayList<CResult> results;
    private ArrayList<CResult> summary;
    private final String USER_AGENT = "Mozilla/5.0";
    // Constructor    
    public CSearcher (String[] args) {            
        this.terms = new ArrayList<String>();
        this.engines = new ArrayList<CEngine>();
        this.results = new ArrayList<CResult>();
        this.summary = new ArrayList<CResult>();
        // Iterate and create the arraylist of search terms
        int i=0;
        for (i=0; i<args.length; i++) {
            this.terms.add(args[i]);
        }
        // Populate the arraylist of Search Engines
        this.CreateEngines();     
    }    
    // Create an instance of CEngine for each search engine
    private void CreateEngines() {
        String url;
        String patternSearchIni;
        String patternSearchFin;
                
        url = "http://www.google.com/search?q=";
        patternSearchIni = "<div class=\"sd\" id=\"resultStats\">";
        patternSearchFin = "</div";
        CEngine google = new CEngine("Google", url, patternSearchIni, patternSearchFin);        
        this.engines.add(google);
        
        url = "https://www.bing.com/search?q=";
        patternSearchIni = "<span class=\"sb_count\">";
        patternSearchFin = "</span>";
        CEngine bing = new CEngine("Bing", url, patternSearchIni, patternSearchFin);        
        this.engines.add(bing);
        
        // If you need an extra engine, just write down its parameters
    }
    // Searches each term in each engine and stores the results
    public void SearchTermsAndStoreResults() {
        int indTerm=0, indEngine=0;
        long quantityResult;
        String urlConcatenated="";
        for (indTerm=0; indTerm<this.terms.size(); indTerm++) {
            for (indEngine=0; indEngine<this.engines.size(); indEngine++) {
                urlConcatenated = this.engines.get(indEngine).getURL() + this.terms.get(indTerm).replace(" ","+");
                quantityResult = this.getQuantityfromHTML(this.getHTMLfromURL(urlConcatenated), this.engines.get(indEngine).getPatternSearchIni(), this.engines.get(indEngine).getPatternSearchFin());
                this.results.add(new CResult(this.engines.get(indEngine).getName(), this.terms.get(indTerm), quantityResult));
            }
        }
    }

    // Query an HTTP URL direction
    private ArrayList<String> getHTMLfromURL(String urlEngine) {
        ArrayList<String> HTMLStringLines = new ArrayList<String>();
        try {
            URL url = new URL(urlEngine);        
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            
            while (true) {
                inputLine = in.readLine();
                if (inputLine == null)
                    break;
                HTMLStringLines.add(inputLine);
            }
            in.close();
        } catch (Exception e) {            
            e.printStackTrace();
        } 
        return HTMLStringLines;        
    }
    private long getQuantityfromHTML(ArrayList<String> HTMLStringLines, String patternSearchIni, String patternSearchFin) {
        int indHTML=0, indexFound=-1;
        long longAnswer = 0;
        String secondMiddleString = "", answerString="";
        for (indHTML=0; indHTML<HTMLStringLines.size(); indHTML++) {
            indexFound = HTMLStringLines.get(indHTML).indexOf(patternSearchIni);
            if (indexFound != -1) { // The pattern exists within the String
                secondMiddleString = HTMLStringLines.get(indHTML).substring(indexFound+patternSearchIni.length(),HTMLStringLines.get(indHTML).length()-1);
                answerString = secondMiddleString.substring(0,secondMiddleString.indexOf(patternSearchFin)-1);
                answerString = answerString.replaceAll("[^0-9]","");
                // Convert String to Long type
                longAnswer = Long.valueOf(answerString);
                break;
            }            
        }
        return longAnswer;
    }
    // Process the list of results and prints out a summary per term
    public void PrintResultsPerTerm() {
        String term;
        CResult result;
        int indTerms=0, indResults=0;
        for (indTerms=0; indTerms<this.terms.size(); indTerms++) {
            term = this.terms.get(indTerms);
            System.out.print(term + ": ");
            for (indResults=0; indResults<this.results.size(); indResults++) {
                result = this.results.get(indResults);
                if (term.equals(result.getTerm())) {
                    System.out.print(result.getEngine() + ": " + result.getQuantity() + " ");
                }
            }
            System.out.println();
        }
    }
    // Process the list of results and prints out a summary per engine
    public void PrintResultsPerEngine() {
        String engine, term;
        int indEngines=0, indResults=0;
        long max=0;
        CResult result;
        for (indEngines=0; indEngines<this.engines.size(); indEngines++) {
            engine = this.engines.get(indEngines).getName();
            System.out.print(engine + " winner: ");
            // Find the winner term per engine
            max=-1; term="";
            for (indResults=0; indResults<this.results.size(); indResults++) {
                result = this.results.get(indResults);
                if ((engine.equals(result.getEngine())) && (result.getQuantity() > max)) {
                    max = result.getQuantity();
                    term = result.getTerm();                    
                }
            }
            System.out.println(term);
        }
    }
    
    public void CalculateSummary() {
        CResult tempCResult;
        int indResults=0, indSummary=0;
        boolean found=false;
        for (indResults=0; indResults<this.results.size(); indResults++) {
            tempCResult = this.results.get(indResults);
            if (this.summary.size() == 0) { // Empty list
                this.summary.add(new CResult("None", tempCResult.getTerm(), tempCResult.getQuantity()));
            } else { // term exists in summary list
                indSummary=0; found=false;
                while ((!found) && (indSummary<this.summary.size())) {
                    if (tempCResult.getTerm().equals(this.summary.get(indSummary).getTerm())) {
                        found = true;
                        this.summary.get(indSummary).additionate(tempCResult.getQuantity());
                    }
                    indSummary++;                
                }
                // term does not exist in summary list
                if (!found) {
                    this.summary.add(new CResult("None", tempCResult.getTerm(), tempCResult.getQuantity()));                                        
                }
            }
        }
    }
    
    public void PrintTotalWinner() {
        int indSummary=0;
        long max=-1;
        String term="";
        for (indSummary=0; indSummary<this.summary.size(); indSummary++) {
            if (this.summary.get(indSummary).getQuantity() > max) {
                max = this.summary.get(indSummary).getQuantity();
                term = this.summary.get(indSummary).getTerm();
            }
        }                
        System.out.println("Total winner: " + term);
    }
}