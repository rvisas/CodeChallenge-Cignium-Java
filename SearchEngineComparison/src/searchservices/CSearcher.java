package searchservices;

import java.io.BufferedReader;
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
    
    public CSearcher (String[] args) {            
        this.terms = new ArrayList<String>();
        this.engines = new ArrayList<CEngine>();
        this.results = new ArrayList<CResult>();
        this.summary = new ArrayList<CResult>();
        
        for (int indArgs=0; indArgs<args.length; indArgs++) {
            this.terms.add(args[indArgs]); // Create the Arraylist of Searching Terms
        }
        
        this.CreateEngines(); // Create the ArrayList of Search Engines
    }    
    
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
        
        // If you need an extra engine, just write down its parameters and add it to ArrayList
    }
    // Searches each term in each engine and stores the results
    public void SearchTermsAndStoreResults() {
        long quantityResult;
        String urlConcatenated="";
        for (int indTerm=0; indTerm<this.terms.size(); indTerm++) {
            for (int indEngine=0; indEngine<this.engines.size(); indEngine++) {
                urlConcatenated = this.engines.get(indEngine).getURL() + this.terms.get(indTerm).replace(" ","+");
                quantityResult = this.getQuantityfromHTML(this.getHTMLfromURL(urlConcatenated), this.engines.get(indEngine).getPatternSearchIni(), this.engines.get(indEngine).getPatternSearchFin());
                this.results.add(new CResult(this.engines.get(indEngine).getEngineName(), this.terms.get(indTerm), quantityResult));
            }
        }
    }

    private ArrayList<String> getHTMLfromURL(String urlEngine) {
        ArrayList<String> HTMLStringLines = new ArrayList<String>();
        try {
            URL url = new URL(urlEngine);        
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;            
            while (true) {
                inputLine = inputBuffer.readLine();
                if (inputLine == null)
                    break;
                HTMLStringLines.add(inputLine);
            }
            inputBuffer.close();            
        } catch (Exception e) {            
            e.printStackTrace();
        }
        return HTMLStringLines;
    }
    private long getQuantityfromHTML(ArrayList<String> HTMLStringLines, String patternSearchIni, String patternSearchFin) {
        long longAnswer=-1;
        String secondMiddleString = "", answerString="";
        for (int indHTML=0; indHTML<HTMLStringLines.size(); indHTML++) {
            int indexFound = HTMLStringLines.get(indHTML).indexOf(patternSearchIni);
            if (indexFound != -1) {
                secondMiddleString = HTMLStringLines.get(indHTML).substring(indexFound+patternSearchIni.length(),HTMLStringLines.get(indHTML).length()-1);
                answerString = secondMiddleString.substring(0,secondMiddleString.indexOf(patternSearchFin)-1);
                answerString = answerString.replaceAll("[^0-9]","");
                longAnswer = Long.valueOf(answerString);
                break;
            }            
        }
        return longAnswer;
    }
    
    public void PrintResultsPerTerm() {
        String term;
        CResult result;
        for (int indTerms=0; indTerms<this.terms.size(); indTerms++) {
            term = this.terms.get(indTerms);
            System.out.print(term + ": ");
            for (int indResults=0; indResults<this.results.size(); indResults++) {
                result = this.results.get(indResults);
                if (term.equals(result.getTerm())) {
                    System.out.print(result.getEngineName() + ": " + result.getQuantity() + " ");
                }
            }
            System.out.println();
        }
    }
    // Prints the term with more results per each engine
    public void PrintResultsPerEngine() {
        String engineName, maxTermPerEngine;
        long maxQuantityPerEngine=0;
        CResult result;
        for (int indEngines=0; indEngines<this.engines.size(); indEngines++) {
            engineName = this.engines.get(indEngines).getEngineName();
            System.out.print(engineName + " winner: ");
            maxQuantityPerEngine=-1; maxTermPerEngine="";
            for (int indResults=0; indResults<this.results.size(); indResults++) {
                result = this.results.get(indResults);
                if ((engineName.equals(result.getEngineName())) && (result.getQuantity() > maxQuantityPerEngine)) {
                    maxQuantityPerEngine = result.getQuantity();
                    maxTermPerEngine = result.getTerm();                    
                }
            }
            System.out.println(maxTermPerEngine);
        }
    }
    
    public void CalculateSummary() {
        CResult tempCResult;
        boolean found=false;
        for (int indResults=0; indResults<this.results.size(); indResults++) {
            tempCResult = this.results.get(indResults);
            if (this.summary.size() == 0) { // Empty list
                this.summary.add(new CResult("None", tempCResult.getTerm(), tempCResult.getQuantity()));
            } else { // term exists in summary list
                int indSummary=0; found=false;
                while ((!found) && (indSummary<this.summary.size())) {
                    if (tempCResult.getTerm().equals(this.summary.get(indSummary).getTerm())) {
                        found = true;
                        this.summary.get(indSummary).additionate(tempCResult.getQuantity());
                    }
                    indSummary++;                
                }                
                if (!found) { // term does not exist in summary list
                    this.summary.add(new CResult("None", tempCResult.getTerm(), tempCResult.getQuantity()));                                        
                }
            }
        }
    }
    
    public void PrintTotalWinner() {
        long max=-1;
        String term="";
        for (int indSummary=0; indSummary<this.summary.size(); indSummary++) {
            if (this.summary.get(indSummary).getQuantity() > max) {
                max = this.summary.get(indSummary).getQuantity();
                term = this.summary.get(indSummary).getTerm();
            }
        }               
        System.out.println("Total winner: " + term);
    }
}