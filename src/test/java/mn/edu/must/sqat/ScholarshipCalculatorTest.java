package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Lab 9, Parts A - D: happy path, CFG-derived tests (see cfg.png) for 100% branch/line coverage,
 * loop-boundary tests (0 / 1 / 2+ iterations) and boundary-value tests from the specification.
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

    // ---------------- Part D: boundary values taken from the specification ----------------

    @ParameterizedTest(name = "letterGrade({0}) = {1}")
    @CsvSource({
        "0,F",
        "59.99,F",
        "60,D",
        "69.99,D",
        "70,C",
        "79.99,C",
        "80,B",
        "89.99,B",
        "90,A",
        "100,A"
    })
    void letterGradeBoundaries(double score, String expected) {
        assertEquals(expected, calc.letterGrade(score));
    }

    @Test
    void gpaScoreSixtyCountsAsD() {
        // spec: 60 is a "D" (1 point), so two scores of exactly 60 average 1.0
        assertEquals(1.0, calc.gpa(new double[] {60, 60}), 1e-9);
    }

    @ParameterizedTest(name = "scholarship({0}, {1}, {2}) = {3}")
    @CsvSource({
        "3.8,15,false,100",
        "3.79,15,false,70",
        "3.8,14,false,70",
        "3.4,12,false,70",
        "3.39,12,false,50",
        "3.4,11,false,50",
        "3.0,0,false,50",
        "2.99,20,false,0",
        "4.0,0,false,50",
        "0.0,0,false,0",
        "4.0,15,true,0"
    })
    void scholarshipThresholdBoundaries(double gpa, int credits, boolean violation, int expected) {
        assertEquals(expected, calc.scholarship(gpa, credits, violation));
    }
}
