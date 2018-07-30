package searchenginecomparison;

import searchservices.CSearcher;

public class SearchEngineComparison {
    
    public static void main(String[] args) {        
        if (args.length == 0) {
            System.out.println("Kindly provide one or more terms to search.");
            System.out.println("The program is shutting down.");
        } else {
            CSearcher searcher = new CSearcher(args);
            searcher.SearchTermsAndStoreResults();
            searcher.PrintResultsPerTerm();
            searcher.PrintResultsPerEngine();
            searcher.CalculateSummary();
            searcher.PrintTotalWinner();  
        }
    }
}