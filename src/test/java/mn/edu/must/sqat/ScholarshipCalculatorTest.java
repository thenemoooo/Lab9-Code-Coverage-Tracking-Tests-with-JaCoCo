package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Lab 9, Parts A + B + C: happy path, CFG-derived tests (see cfg.png) for 100% branch/line
 * coverage, and the loop-boundary tests (0 / 1 / 2+ iterations) for the loop in gpa().
 */
class ScholarshipCalculatorTest {

    private final ScholarshipCalculator calc = new ScholarshipCalculator();

    // ---------------- letterGrade ----------------

    @Test
    void letterGradeA() {
        assertEquals("A", calc.letterGrade(95));
    }

    @Test
    void letterGradeB() {
        assertEquals("B", calc.letterGrade(85));
    }

    @Test
    void letterGradeC() {
        assertEquals("C", calc.letterGrade(75));
    }

    @Test
    void letterGradeD() {
        assertEquals("D", calc.letterGrade(65));
    }

    @Test
    void letterGradeF() {
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGradeRejectsNegativeScore() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
    }

    @Test
    void letterGradeRejectsScoreAbove100() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---------------- gpa ----------------

    @Test
    void gpaTwoGrades() {
        // A (4) + B (3) -> 3.5
        assertEquals(3.5, calc.gpa(new double[] {95, 85}), 1e-9);
    }

    @Test
    void gpaCoversEveryGradeLetter() {
        // A, B, C, D, F -> (4 + 3 + 2 + 1 + 0) / 5 = 2.0; hits every switch case and default
        assertEquals(2.0, calc.gpa(new double[] {95, 85, 75, 65, 30}), 1e-9);
    }

    // ---- loop boundary in gpa(): 0 / 1 / 2+ iterations ----

    @Test
    void gpaLoopZeroIterationsNull() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
    }

    @Test
    void gpaLoopZeroIterationsEmptyArray() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[0]));
    }

    @Test
    void gpaLoopOneIteration() {
        assertEquals(4.0, calc.gpa(new double[] {95}), 1e-9);
    }

    @Test
    void gpaLoopManyIterations() {
        // A, B, C, D -> (4 + 3 + 2 + 1) / 4 = 2.5
        assertEquals(2.5, calc.gpa(new double[] {95, 85, 75, 65}), 1e-9);
    }

    // ---------------- scholarship (one test per CFG path, see cfg.png) ----------------

    @Test
    void scholarshipFullAward() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
    }

    @Test
    void scholarshipSeventyAward() {
        assertEquals(70, calc.scholarship(3.5, 13, false));
    }

    @Test
    void scholarshipFiftyAward() {
        assertEquals(50, calc.scholarship(3.2, 10, false));
    }

    @Test
    void scholarshipNoAward() {
        assertEquals(0, calc.scholarship(2.5, 20, false));
    }

    @Test
    void scholarshipViolationGivesZero() {
        assertEquals(0, calc.scholarship(3.9, 16, true));
    }

    @Test
    void scholarshipHighGpaLowCreditsFallsTo70() {
        // gpa >= 3.8 holds but credits < 15, so the 100% rule fails and the 70% rule applies
        assertEquals(70, calc.scholarship(3.9, 14, false));
    }

    @Test
    void scholarshipMidGpaLowCreditsFallsTo50() {
        // gpa >= 3.4 holds but credits < 12, so the 70% rule fails and the 50% rule applies
        assertEquals(50, calc.scholarship(3.5, 8, false));
    }

    @Test
    void scholarshipRejectsNegativeGpa() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.1, 10, false));
    }

    @Test
    void scholarshipRejectsGpaAboveFour() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
    }

    @Test
    void scholarshipRejectsNegativeCredits() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }
}
