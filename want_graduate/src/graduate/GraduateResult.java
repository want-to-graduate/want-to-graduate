package graduate;

import java.util.HashMap;
import java.util.Map;

public class GraduateResult {
	private boolean passed;

    // 어떤 항목이 몇 학점 부족한지
    private Map<String, Integer> missingCredits = new HashMap<>();
    // 어떤 과목구분이 몇 과목 부족한지
    private Map<String, Integer> missingCounts = new HashMap<>();

    public void addMissingCredit(String key, int lack) {
        if (lack > 0) missingCredits.put(key, lack);
    }

    public void addMissingCount(String key, int lack) {
        if (lack > 0) missingCounts.put(key, lack);
    }

    public void finalizePass() {
        this.passed = missingCredits.isEmpty() && missingCounts.isEmpty();
    }

    public boolean isPassed() { return passed; }
    public Map<String, Integer> getMissingCredits() { return missingCredits; }
    public Map<String, Integer> getMissingCounts() { return missingCounts; }
}
