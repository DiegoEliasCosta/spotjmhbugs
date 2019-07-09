package de.heidelberg.pvs.diego.detectors;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;

final class Util {
    static int countBugTypes(BugCollection bugCollection, String type) {
        int result = 0;
        for (BugInstance bug : bugCollection) {
            if (type.equals(bug.getType())) {
                result++;
            }
        }
        return result;
    }
}
