package org.jfree.data;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

public class RangeTest {

	private Range range;
	private Range positiveRange; // [1.0, 5.0]
	private Range negativeRange; // [-5.0, -1.0]
	private Range mixedRange;    // [-3.0, 3.0]

	@Before
	public void setUp() {
		range         = new Range(1.0, 5.0);
		positiveRange = new Range(1.0, 5.0);
		negativeRange = new Range(-5.0, -1.0);
		mixedRange    = new Range(-3.0, 3.0);
	}

	// =========================================================
	// CONSTRUCTOR: Range(double lower, double upper)
	// =========================================================

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorInvalidRangeThrows() {
		new Range(5.0, 1.0);
	}

	@Test
	public void testConstructorEqualBoundsAllowed() {
		Range r = new Range(5.0, 5.0);
		assertEquals(5.0, r.getLowerBound(), 0.0);
		assertEquals(5.0, r.getUpperBound(), 0.0);
	}

	@Test
	public void testConstructorExtremeValues() {
		Range r = new Range(-Double.MAX_VALUE, Double.MAX_VALUE);
		assertEquals(-Double.MAX_VALUE, r.getLowerBound(), 0.0);
		assertEquals( Double.MAX_VALUE, r.getUpperBound(), 0.0);
	}

	@Test
	public void testConstructorBoundsStoredExactly() {
		// Kills UOI a--/a++/negation mutants on this.lower and this.upper.
		// Values chosen so that ±1 and sign-flip are all clearly distinguishable.
		assertEquals( 5.0, new Range( 5.0, 100.0).getLowerBound(), 0.0);
		assertEquals(-8.0, new Range(-8.0,   0.0).getLowerBound(), 0.0);
		assertEquals( 2.5, new Range( 2.5,   7.5).getLowerBound(), 0.0);
		assertEquals(10.0, new Range( 1.0,  10.0).getUpperBound(), 0.0);
		assertEquals(-3.0, new Range(-9.0,  -3.0).getUpperBound(), 0.0);
		assertEquals( 3.7, new Range( 0.0,   3.7).getUpperBound(), 0.0);
	}

	@Test
	public void testConstructorExceptionMessageComplete() {
		// Verifies every StringBuilder.append() call survives (kills VoidMethodCall mutants).
		try {
			new Range(8.0, 2.0);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String msg = e.getMessage();
			assertNotNull("Message must not be null", msg);
			assertTrue("Missing method signature",    msg.contains("Range(double, double)"));
			assertTrue("Missing lower value",         msg.contains("8.0"));
			assertTrue("Missing comparison operator", msg.contains(") <= upper ("));
			assertTrue("Missing upper value",         msg.contains("2.0"));
			assertTrue("Missing closing characters",  msg.contains(")."));
			assertTrue("lower must appear before upper",
					msg.indexOf("8.0") < msg.indexOf("2.0"));
			assertTrue("Message is too short — an append() was likely removed",
					msg.length() > 40);
		}
	}

	// =========================================================
	// FUNCTION 1: contains(double value)
	// =========================================================

	@Test
	public void testContainsBelowLowerBound() {
		assertFalse("Value just below lower bound (0.0) should be outside [1.0,5.0]",
				range.contains(0.0));
	}

	@Test
	public void testContainsAtLowerBoundary() {
		assertTrue("Value at lower boundary (1.0) should be inside [1.0,5.0]",
				range.contains(1.0));
	}

	@Test
	public void testContainsMidRange() {
		assertTrue("Value inside range (3.0) should be inside [1.0,5.0]",
				range.contains(3.0));
	}

	@Test
	public void testContainsAtUpperBoundary() {
		assertTrue("Value at upper boundary (5.0) should be inside [1.0,5.0]",
				range.contains(5.0));
	}

	@Test
	public void testContainsAboveUpperBound() {
		assertFalse("Value above upper bound (6.0) should be outside [1.0,5.0]",
				range.contains(6.0));
	}

	@Test
	public void testContainsJustAboveLowerBound() {
		assertTrue(range.contains(1.0001));
	}

	@Test
	public void testContainsJustBelowUpperBound() {
		assertTrue(range.contains(4.9999));
	}

	@Test
	public void testContainsNegativeValueInNegativeRange() {
		assertTrue(new Range(-10.0, -1.0).contains(-5.0));
	}

	@Test
	public void testContainsNaNValue() {
		assertFalse("NaN should not be inside any range", range.contains(Double.NaN));
	}

	@Test
	public void testContainsPositiveInfinity() {
		assertFalse(range.contains(Double.POSITIVE_INFINITY));
	}

	@Test
	public void testContainsNegativeInfinity() {
		assertFalse(range.contains(Double.NEGATIVE_INFINITY));
	}

	@Test
	public void testContainsNegativeValueInsideNegativeRange() {
		// ABSMutator: abs(-3)=3 is NOT in [-5,-1]; test ensures sign is preserved
		Range r = new Range(-5.0, -1.0);
		assertTrue(r.contains(-3.0));
		assertTrue(r.contains(-5.0));
		assertTrue(r.contains(-1.0));
		assertFalse(r.contains(-6.0));
		assertFalse(r.contains(0.0));
	}

	@Test
	public void testContainsJustOutsideBothBounds() {
		// Kills CBM < vs <= and > vs >= on both comparisons
		Range r = new Range(5.0, 10.0);
		assertFalse(r.contains(4.9999999));
		assertFalse(r.contains(10.0000001));
		assertTrue(r.contains(5.0));
		assertTrue(r.contains(10.0));
		assertTrue(r.contains(7.5));
	}

	// =========================================================
	// FUNCTION 2: getLength()
	// =========================================================

	@Test
	public void testGetLengthNormalRange() {
		assertEquals(4.0, range.getLength(), 0.0001);
	}

	@Test
	public void testGetLengthSmallRange() {
		assertEquals(0.1, new Range(1.0, 1.1).getLength(), 0.0001);
	}

	@Test
	public void testGetLengthLargeRange() {
		assertEquals(999999.0, new Range(1.0, 1000000.0).getLength(), 0.0001);
	}

	@Test
	public void testGetLengthZeroRange() {
		assertEquals(0.0, new Range(5.0, 5.0).getLength(), 0.0);
	}

	@Test
	public void testGetLengthNegativeToPositive() {
		assertEquals(10.0, new Range(-5.0, 5.0).getLength(), 0.0);
	}

	@Test
	public void testGetLengthBothNegative() {
		assertEquals(8.0, new Range(-10.0, -2.0).getLength(), 0.0);
	}

	@Test
	public void testGetLengthExactZeroTolerance() {
		// Strict delta=0.0 kills UOI a++ on lower/upper (shifts result by 1)
		assertEquals(1.0,  new Range(7.0, 8.0).getLength(),   0.0);
		assertEquals(20.0, new Range(-10.0, 10.0).getLength(), 0.0);
		assertEquals(998.999, new Range(0.001, 999.0).getLength(), 0.0001);
	}

	@Test
	public void testGetLengthBothNegativeLarge() {
		// ABSMutator on lower: abs(-999)=999 changes result; expect 998, not 1000
		assertEquals(998.0, new Range(-999.0, -1.0).getLength(), 0.0);
	}

	// =========================================================
	// FUNCTION 3: getCentralValue()
	// =========================================================

	@Test
	public void testGetCentralValueNormalRange() {
		assertEquals(3.0, range.getCentralValue(), 0.0001);
	}

	@Test
	public void testGetCentralValueSmallRange() {
		assertEquals(1.05, new Range(1.0, 1.1).getCentralValue(), 0.0001);
	}

	@Test
	public void testGetCentralValueZeroRange() {
		assertEquals(5.0, new Range(5.0, 5.0).getCentralValue(), 0.0);
	}

	@Test
	public void testGetCentralValueSymmetricRange() {
		assertEquals(0.0, new Range(-5.0, 5.0).getCentralValue(), 0.0);
	}

	@Test
	public void testGetCentralValueLargeRange() {
		assertEquals(500000.5, new Range(1.0, 1000000.0).getCentralValue(), 0.0001);
	}

	@Test
	public void testGetCentralValueExactZeroTolerance() {
		// CRCR: divisor 2.0→1.0 gives lower+upper; divisor 3.0 gives another wrong value
		assertEquals( 0.0, new Range(-4.0, 4.0).getCentralValue(),  0.0);
		assertEquals( 1.0, new Range(0.0, 2.0).getCentralValue(),   0.0);
		assertEquals(-1.0, new Range(-3.0, 1.0).getCentralValue(),  0.0);
		assertEquals( 5.0, new Range(3.0, 7.0).getCentralValue(),   0.0);
		assertEquals(-5.0, new Range(-7.0, -3.0).getCentralValue(), 0.0);
	}

	@Test
	public void testGetCentralValueBothHalvesNeeded() {
		// AOD2: if lower/2 or upper/2 is dropped, these fail
		assertEquals( 6.0, new Range(2.0, 10.0).getCentralValue(),  0.0);
		assertEquals( 7.5, new Range(5.0, 10.0).getCentralValue(),  0.0);
		assertEquals(-2.0, new Range(-8.0, 4.0).getCentralValue(),  0.0);
		assertEquals( 3.5, new Range(-3.0, 10.0).getCentralValue(), 0.0);
	}

	@Test
	public void testGetCentralValueBothNegative() {
		// ABSMutator on lower: abs(-9)=9 => (9+-1)/2=4, not -5
		assertEquals(-5.0, new Range(-9.0, -1.0).getCentralValue(), 0.0);
		assertEquals(-4.0, new Range(-6.0, -2.0).getCentralValue(), 0.0);
	}

	// =========================================================
	// FUNCTION 4: getUpperBound()
	// =========================================================

	@Test
	public void testGetUpperBoundNormalRange() {
		assertEquals(5.0, range.getUpperBound(), 0.0);
	}

	@Test
	public void testGetUpperBoundZeroLengthRange() {
		assertEquals(5.0, new Range(5.0, 5.0).getUpperBound(), 0.0);
	}

	@Test
	public void testGetUpperBoundNegativeToPositive() {
		assertEquals(5.0, new Range(-5.0, 5.0).getUpperBound(), 0.0);
	}

	@Test
	public void testGetUpperBoundBothNegative() {
		assertEquals(-2.0, new Range(-10.0, -2.0).getUpperBound(), 0.0);
	}

	@Test
	public void testGetUpperBoundNegativeUpperBound() {
		assertEquals(-0.001, new Range(-100.0, -0.001).getUpperBound(), 0.0);
	}

	@Test
	public void testGetUpperBoundNonZeroExact() {
		// PrimitiveReturnsMutator: stubbing to 0.0 caught by non-zero assertions
		assertEquals(15.0,  new Range(7.0, 15.0).getUpperBound(),    0.0);
		assertEquals(-1.0,  new Range(-13.0, -1.0).getUpperBound(),  0.0);
		assertEquals(999.0, new Range(1.0, 999.0).getUpperBound(),   0.0);
	}

	@Test
	public void testGetUpperBoundNotNegated() {
		// ABSMutator: abs(-3)=3 != -3
		assertEquals(-3.0, new Range(-9.0, -3.0).getUpperBound(), 0.0);
	}

	// =========================================================
	// FUNCTION 5: getLowerBound()
	// =========================================================

	@Test
	public void testGetLowerBoundNormalRange() {
		assertEquals(1.0, range.getLowerBound(), 0.0);
	}

	@Test
	public void testGetLowerBoundZeroLengthRange() {
		assertEquals(5.0, new Range(5.0, 5.0).getLowerBound(), 0.0);
	}

	@Test
	public void testGetLowerBoundNegativeToPositive() {
		assertEquals(-5.0, new Range(-5.0, 5.0).getLowerBound(), 0.0);
	}

	@Test
	public void testGetLowerBoundBothNegative() {
		assertEquals(-10.0, new Range(-10.0, -2.0).getLowerBound(), 0.0);
	}

	@Test
	public void testGetLowerBoundNegativeLowerBound() {
		assertEquals(-0.001, new Range(-0.001, 5.0).getLowerBound(), 0.0);
	}

	@Test
	public void testGetLowerBoundNonZeroExact() {
		// PrimitiveReturnsMutator: stubbing to 0.0 caught by non-zero assertions
		assertEquals( 7.0,   new Range(7.0, 15.0).getLowerBound(),    0.0);
		assertEquals(-13.0,  new Range(-13.0, -1.0).getLowerBound(),  0.0);
		assertEquals( 0.001, new Range(0.001, 1.0).getLowerBound(),   0.0);
	}

	@Test
	public void testGetLowerBoundNotNegated() {
		// ABSMutator: abs(-999)=999 != -999
		assertEquals(-999.0, new Range(-999.0, -1.0).getLowerBound(), 0.0);
	}

	// =========================================================
	// FUNCTION 6: intersects(double lower, double upper)
	// =========================================================

	@Test
	public void testIntersectsCompleteOverlap() {
		assertTrue(range.intersects(2.0, 4.0));
	}

	@Test
	public void testIntersectsPartialOverlapLeft() {
		assertTrue(range.intersects(0.0, 2.0));
	}

	@Test
	public void testIntersectsPartialOverlapRight() {
		assertTrue(range.intersects(4.0, 6.0));
	}

	@Test
	public void testIntersectsNoOverlapLeft() {
		assertFalse(range.intersects(-2.0, 0.0));
	}

	@Test
	public void testIntersectsNoOverlapRight() {
		assertFalse(range.intersects(6.0, 8.0));
	}

	@Test
	public void testIntersectsTouchingLowerBound() {
		// b1==lower: NOT an intersection per JFreeChart semantics
		assertFalse("Range ending exactly at lower bound should NOT intersect [1.0,5.0]",
				range.intersects(0.0, 1.0));
	}

	@Test
	public void testIntersectsTouchingUpperBound() {
		// b0==upper: NOT an intersection per JFreeChart semantics
		assertFalse("Range starting exactly at upper bound should NOT intersect [1.0,5.0]",
				range.intersects(5.0, 7.0));
	}

	@Test
	public void testIntersectsIdenticalRange() {
		assertTrue(range.intersects(1.0, 5.0));
	}

	@Test
	public void testIntersectsLargerRange() {
		assertTrue(range.intersects(-10.0, 10.0));
	}

	@Test
	public void testIntersectsSinglePointInside() {
		assertTrue(range.intersects(3.0, 3.0));
	}

	@Test
	public void testIntersectsSinglePointAtLowerBound() {
		assertFalse("Single-point at lower bound should NOT intersect",
				range.intersects(1.0, 1.0));
	}

	@Test
	public void testIntersectsSinglePointAtUpperBound() {
		assertFalse("Single-point at upper bound should NOT intersect",
				range.intersects(5.0, 5.0));
	}

	@Test
	public void testIntersectsInvalidRangeOrder() {
		assertFalse("Inverted argument range should NOT intersect",
				range.intersects(5.0, 1.0));
	}

	@Test
	public void testIntersectsPrecisionEdgeCases() {
		assertFalse(range.intersects(5.0000001, 6.0));
		assertFalse(range.intersects(0.0, 0.9999999));
		assertTrue(range.intersects(0.9999999, 1.0000001));
		assertTrue(range.intersects(4.9999999, 5.0000001));
	}

	@Test
	public void testIntersectsAllNegativeArgs() {
		// ABSMutator: abs() on negative args would push them positive, wrong result
		Range r = new Range(-10.0, -3.0);
		assertTrue(r.intersects(-7.0, -4.0));
		assertTrue(r.intersects(-12.0, -5.0));
		assertFalse(r.intersects(-2.0, -1.0));
		assertFalse(r.intersects(-15.0, -11.0));
	}

	@Test
	public void testIntersectsBoundaryB0EqualsUpper() {
		// ROR < vs <=: b0==upper is the exact discriminating value
		assertFalse(new Range(-5.0, -1.0).intersects(-1.0, 3.0));
		assertFalse(new Range(3.0, 8.0).intersects(8.0, 12.0));
	}

	@Test
	public void testIntersectsBoundaryB1EqualsLower() {
		// ROR <= vs <: b1==lower is the exact discriminating value
		assertFalse(new Range(-5.0, -1.0).intersects(-9.0, -5.0));
		assertFalse(new Range(3.0, 8.0).intersects(-1.0, 3.0));
	}

	@Test
	public void testIntersectsB0JustInsideAndAtUpperBoundary() {
		Range r = new Range(1.0, 5.0);
		assertTrue("b0=4.9 must intersect",           r.intersects(4.9, 6.0));
		assertFalse("b0=5.0 (==upper) must NOT intersect", r.intersects(5.0, 6.0));
	}

	@Test
	public void testIntersectsB1JustInsideAndAtLowerBoundary() {
		Range r = new Range(1.0, 5.0);
		assertTrue("b1=2 must intersect",              r.intersects(0.0, 2.0));
		assertFalse("b1=1 (==lower) must NOT intersect", r.intersects(0.0, 1.0));
	}

	// =========================================================
	// FUNCTION 7: constrain(double value)
	// =========================================================

	@Test
	public void testConstrainValueInsideRange() {
		assertEquals(3.0, range.constrain(3.0), 0.0);
	}

	@Test
	public void testConstrainValueBelowRange() {
		assertEquals(1.0, range.constrain(-1.0), 0.0);
	}

	@Test
	public void testConstrainValueAboveRange() {
		assertEquals(5.0, range.constrain(10.0), 0.0);
	}

	@Test
	public void testConstrainAtLowerBound() {
		assertEquals(1.0, range.constrain(1.0), 0.0);
	}

	@Test
	public void testConstrainAtUpperBound() {
		assertEquals(5.0, range.constrain(5.0), 0.0);
	}

	@Test
	public void testConstrainNaNReturnsNaN() {
		assertTrue("constrain(NaN) should return NaN",
				Double.isNaN(range.constrain(Double.NaN)));
	}

	@Test
	public void testConstrainAllThreeBranches() {
		Range r = new Range(2.0, 8.0);
		assertEquals("Above range: clamped to upper", 8.0, r.constrain(10.0), 0.0);
		assertEquals("Below range: clamped to lower", 2.0, r.constrain(-1.0), 0.0);
		assertEquals("Inside range: value returned",  5.0, r.constrain(5.0),  0.0);
	}

	@Test
	public void testConstrainNegativeRange() {
		Range r = new Range(-8.0, -1.0);
		// ABSMutator: abs(-4)=4, which would be clamped to -1 rather than -4
		assertEquals(-4.0, r.constrain(-4.0),  0.0);
		assertEquals(-8.0, r.constrain(-10.0), 0.0);
		assertEquals(-1.0, r.constrain(5.0),   0.0);
	}

	@Test
	public void testConstrainNonZeroInsideRange() {
		// PrimitiveReturnsMutator: stubbing return to 0.0 is caught by non-zero value
		assertEquals(7.0,  new Range(3.0, 9.0).constrain(7.0),  0.0);
		assertEquals(-5.0, new Range(-9.0, -3.0).constrain(-5.0), 0.0);
	}

	@Test
	public void testConstrainBoundaryValueAtUpper() {
		// CBM > vs >=: value==upper should return value, not be clamped
		assertEquals(5.0,       range.constrain(5.0),       0.0);
		assertEquals(5.0,       range.constrain(5.0000001),  0.0);
		assertEquals(4.9999999, range.constrain(4.9999999),  0.0);
	}

	@Test
	public void testConstrainBoundaryValueAtLower() {
		// CBM < vs <=: value==lower should return value, not be clamped
		assertEquals(1.0,       range.constrain(1.0),       0.0);
		assertEquals(1.0,       range.constrain(0.9999999),  0.0);
		assertEquals(1.0000001, range.constrain(1.0000001),  0.0);
	}

	// =========================================================
	// FUNCTION 8: combine(Range range1, Range range2)
	// =========================================================

	@Test
	public void testCombineTwoRanges() {
		Range combined = Range.combine(new Range(1.0, 5.0), new Range(3.0, 8.0));
		assertEquals(1.0, combined.getLowerBound(), 0.0001);
		assertEquals(8.0, combined.getUpperBound(), 0.0001);
	}

	@Test
	public void testCombineFirstRangeNull() {
		Range r2 = new Range(3.0, 8.0);
		assertEquals(r2, Range.combine(null, r2));
	}

	@Test
	public void testCombineSecondRangeNull() {
		Range r1 = new Range(1.0, 5.0);
		assertEquals(r1, Range.combine(r1, null));
	}

	@Test
	public void testCombineBothRangesNull() {
		assertNull(Range.combine(null, null));
	}

	@Test
	public void testCombineNonOverlappingRanges() {
		Range combined = Range.combine(new Range(1.0, 3.0), new Range(7.0, 10.0));
		assertEquals(1.0,  combined.getLowerBound(), 0.0001);
		assertEquals(10.0, combined.getUpperBound(), 0.0001);
	}

	@Test
	public void testCombineDistinctBoundsKillsArgumentPropagation() {
		// If min(r1.lower, r2.lower) => min(r1.lower, r1.lower), lower would be 1, not 7 anyway.
		// This test uses widely separated values to catch any propagation swap.
		Range combined = Range.combine(new Range(1.0, 5.0), new Range(7.0, 12.0));
		assertEquals("Must use r1.lower=1, not r2.lower=7",   1.0, combined.getLowerBound(), 0.0);
		assertEquals("Must use r2.upper=12, not r1.upper=5", 12.0, combined.getUpperBound(), 0.0);
	}

	@Test
	public void testCombineNegativeBothNegativeKillsNonVoidStub() {
		// NonVoidMethodCall: min/max stubbed to 0.0 is caught because neither bound is 0
		Range combined = Range.combine(new Range(-9.0, -3.0), new Range(-6.0, -1.0));
		assertEquals(-9.0, combined.getLowerBound(), 0.0);
		assertEquals(-1.0, combined.getUpperBound(), 0.0);
	}

	@Test
	public void testCombineNaNBoundsProduceNaNRange() {
		Range combined = Range.combine(
				new Range(Double.NaN, Double.NaN),
				new Range(Double.NaN, Double.NaN));
		assertTrue(combined.isNaNRange());
	}

	// =========================================================
	// FUNCTION 9: expandToInclude(Range range, double value)
	// =========================================================

	@Test
	public void testExpandToIncludeValueBelowRange() {
		Range expanded = Range.expandToInclude(range, -1.0);
		assertEquals(-1.0, expanded.getLowerBound(), 0.0001);
		assertEquals( 5.0, expanded.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandToIncludeValueAboveRange() {
		Range expanded = Range.expandToInclude(range, 10.0);
		assertEquals( 1.0, expanded.getLowerBound(), 0.0001);
		assertEquals(10.0, expanded.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandToIncludeValueInsideRange() {
		Range expanded = Range.expandToInclude(range, 3.0);
		assertEquals(1.0, expanded.getLowerBound(), 0.0001);
		assertEquals(5.0, expanded.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandToIncludeNullRange() {
		Range expanded = Range.expandToInclude(null, 3.0);
		assertNotNull(expanded);
		assertEquals(3.0, expanded.getLowerBound(), 0.0001);
		assertEquals(3.0, expanded.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandToIncludeMinMaxNotZeroKillsNonVoidStub() {
		// NonVoidMethodCall: min/max stubbed to 0.0 fails when correct result != 0
		Range r = new Range(3.0, 7.0);
		Range below = Range.expandToInclude(r, -5.0);
		assertEquals(-5.0, below.getLowerBound(), 0.0);
		assertEquals( 7.0, below.getUpperBound(), 0.0);
		Range above = Range.expandToInclude(r, 15.0);
		assertEquals(  3.0, above.getLowerBound(), 0.0);
		assertEquals(15.0, above.getUpperBound(), 0.0);
	}

	// =========================================================
	// FUNCTION 10: expand(Range range, double lowerMargin, double upperMargin)
	// =========================================================

	@Test
	public void testExpandWithPositiveMargins() {
		// length=4: lower=1-4*0.25=0, upper=5+4*0.5=7
		Range expanded = Range.expand(range, 0.25, 0.5);
		assertEquals(0.0, expanded.getLowerBound(), 0.0001);
		assertEquals(7.0, expanded.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandWithZeroMargins() {
		Range expanded = Range.expand(range, 0.0, 0.0);
		assertEquals(1.0, expanded.getLowerBound(), 0.0001);
		assertEquals(5.0, expanded.getUpperBound(), 0.0001);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExpandNullRangeThrows() {
		Range.expand(null, 0.1, 0.1);
	}

	@Test
	public void testExpandPreciseArithmetic() {
		// Range[2,6] length=4: lower=2-4*0.25=1, upper=6+4*0.5=8
		// Kills MathMutator (* vs +/- on margin multiply)
		Range expanded = Range.expand(new Range(2.0, 6.0), 0.25, 0.5);
		assertEquals(1.0, expanded.getLowerBound(), 0.0000001);
		assertEquals(8.0, expanded.getUpperBound(), 0.0000001);
	}

	@Test
	public void testExpandLargeMarginKillsMathMutants() {
		// Range[0,10] length=10: lower=0-10*3=-30, upper=10+10*2=30
		// If * replaced by +: lower=0-(10+3)=-13, upper=10+(10+2)=22
		Range r = Range.expand(new Range(0.0, 10.0), 3.0, 2.0);
		assertEquals(-30.0, r.getLowerBound(), 0.0);
		assertEquals( 30.0, r.getUpperBound(), 0.0);
	}

	@Test
	public void testExpandNegativeMarginsShrinkRange() {
		// length=4: lower=1-4*(-0.25)=2, upper=5+4*(-0.25)=4
		Range result = Range.expand(range, -0.25, -0.25);
		assertEquals(2.0, result.getLowerBound(), 0.0001);
		assertEquals(4.0, result.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandInvertedRangeCollapsesToMidpoint() {
		// Range[1,5] length=4, lm=1.0, um=-1.5:
		//   lower=1-4=-3, upper=5-6=-1 => -3 < -1 so not inverted...
		// Use lm=-1.0, um=-1.5: lower=1+4=5, upper=5+6=11 => not inverted
		// Use Range[2,4] length=2, lm=1.0, um=-3.0:
		//   lower=2-2=0, upper=4+2*(-3)=-2 => 0 > -2: inverted; mid=(0+-2)/2=-1
		Range result = Range.expand(new Range(2.0, 4.0), 1.0, -3.0);
		assertEquals(-1.0, result.getLowerBound(), 0.0001);
		assertEquals(-1.0, result.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandInvertedRangeMidpointDivisorCRCR() {
		// CRCR on the /2 in midpoint: if divisor->1, mid=lower+upper; if ->3, different
		// Range[0,10] length=10, lm=1.0, um=-2.0: lower=0-10=-10, upper=10+(-20)=-10
		// both sides equal, mid must be -10 exactly
		Range r = Range.expand(new Range(0.0, 10.0), 1.0, -2.0);
		assertEquals(-10.0, r.getLowerBound(), 0.0001);
		assertEquals(-10.0, r.getUpperBound(), 0.0001);
	}

	@Test
	public void testExpandNegativeRangeArithmetic() {
		// ABSMutator on lower/upper inside expand
		// Range[-6,-2] length=4, lm=0.5, um=0.5: lower=-6-2=-8, upper=-2+2=0
		Range r = Range.expand(new Range(-6.0, -2.0), 0.5, 0.5);
		assertEquals(-8.0, r.getLowerBound(), 0.0);
		assertEquals( 0.0, r.getUpperBound(), 0.0);
	}

	// =========================================================
	// FUNCTION 11: shift(Range base, double delta [, boolean allowZeroCrossing])
	// =========================================================

	@Test
	public void testShiftPositiveDelta() {
		Range shifted = Range.shift(range, 2.0);
		assertEquals(3.0, shifted.getLowerBound(), 0.0001);
		assertEquals(7.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftNegativeDelta() {
		Range shifted = Range.shift(range, -1.0);
		assertEquals(0.0, shifted.getLowerBound(), 0.0001);
		assertEquals(4.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftWithZeroCrossingAllowed() {
		Range shifted = Range.shift(new Range(-1.0, 1.0), 3.0, true);
		assertEquals(2.0, shifted.getLowerBound(), 0.0001);
		assertEquals(4.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftNoCrossingClampsPositiveLower() {
		// lower=1.0-3.0=-2.0 => clamped to 0.0
		Range shifted = Range.shift(new Range(1.0, 5.0), -3.0, false);
		assertEquals(0.0, shifted.getLowerBound(), 0.0001);
		assertEquals(2.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftNoCrossingPositiveBoundGoesNegativeClamped() {
		// Positive bounds shifted negative: clamped to 0
		Range shifted = Range.shift(new Range(2.0, 6.0), -5.0, false);
		assertEquals(0.0, shifted.getLowerBound(), 0.0001);
		assertEquals(1.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftNoCrossingNegativeRangeStaysClamped() {
		// Negative bounds shifted positive past zero: clamped to 0
		Range shifted = Range.shift(new Range(-3.0, -1.0), 5.0, false);
		assertEquals(0.0, shifted.getLowerBound(), 0.0001);
		assertEquals(0.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftAllowCrossingNegativeGoesPositive() {
		Range shifted = Range.shift(new Range(-2.0, 1.0), 4.0, true);
		assertEquals(2.0, shifted.getLowerBound(), 0.0001);
		assertEquals(5.0, shifted.getUpperBound(), 0.0001);
	}

	@Test
	public void testShiftNegativeRangeNegativeDeltaAllowCrossing() {
		// ABSMutator on delta: abs(-3)=3 would give wrong sign
		Range shifted = Range.shift(new Range(-5.0, -2.0), -3.0, true);
		assertEquals(-8.0, shifted.getLowerBound(), 0.0);
		assertEquals(-5.0, shifted.getUpperBound(), 0.0);
	}

	@Test
	public void testShiftZeroBoundaryNoZeroCrossing() {
		// Third branch of shiftWithNoZeroCrossing: value==0 => return 0+delta
		Range shifted = Range.shift(new Range(0.0, 4.0), 3.0, false);
		assertEquals(3.0, shifted.getLowerBound(), 0.0);
		assertEquals(7.0, shifted.getUpperBound(), 0.0);
	}

	@Test
	public void testShiftCrossPrecision() {
		Range shifted = Range.shift(new Range(-0.1, 0.1), -0.2, false);
		assertEquals(-0.3, shifted.getLowerBound(), 0.00001);
		assertEquals( 0.0, shifted.getUpperBound(), 0.00001);
	}

	@Test
	public void testShiftAdditionKillsAorMutants() {
		// bound=12, delta=3: 12+3=15; -: 9; *: 36; /: 4 — all distinct
		Range shifted = Range.shift(new Range(12.0, 20.0), 3.0);
		assertEquals(15.0, shifted.getLowerBound(), 0.0);
		assertEquals(23.0, shifted.getUpperBound(), 0.0);
	}

	@Test
	public void testShiftNoCrossingZeroConstantKillsCRCR() {
		// CRCR: 0.0 in Math.max replaced with 1.0
		// value=3, delta=-4: max(3-4,0)=0; max(-1,1)=1
		Range r = Range.shift(new Range(3.0, 8.0), -4.0, false);
		assertEquals("max(-1,0)=0, not max(-1,1)=1", 0.0, r.getLowerBound(), 0.0);

		// CRCR: 0.0 in Math.min replaced with 1.0
		// value=-3, delta=4: min(-3+4,0)=0; min(1,1)=1
		Range r2 = Range.shift(new Range(-8.0, -3.0), 4.0, false);
		assertEquals("min(1,0)=0, not min(1,1)=1", 0.0, r2.getUpperBound(), 0.0);
	}

	@Test
	public void testShiftNoCrossingConditionalBoundaryKillsCBM() {
		// CBM: value>0 mutated to value>1; value=0.5 is in (0,1] zone
		// correct: max(0.5-2,0)=0; mutant: 0.5-2=-1.5
		Range r = Range.shift(new Range(0.5, 3.0), -2.0, false);
		assertEquals(0.0, r.getLowerBound(), 0.0);

		// CBM: value>0 mutated to value==0; value=1 (positive, not zero) misrouted
		// correct: max(1-3,0)=0; mutant (1==0 false => else): 1-3=-2
		Range r2 = Range.shift(new Range(1.0, 4.0), -3.0, false);
		assertEquals(0.0, r2.getLowerBound(), 0.0);
	}

	// =========================================================
	// FUNCTION 12: equals(Object obj)
	// =========================================================

	@Test
	public void testEqualsSameRange() {
		assertTrue(range.equals(new Range(1.0, 5.0)));
	}

	@Test
	public void testEqualsSameReference() {
		assertTrue("A range must equal itself", range.equals(range));
	}

	@Test
	public void testEqualsDifferentLowerBound() {
		assertFalse(range.equals(new Range(2.0, 5.0)));
	}

	@Test
	public void testEqualsDifferentUpperBound() {
		assertFalse(range.equals(new Range(1.0, 6.0)));
	}

	@Test
	public void testEqualsNull() {
		assertFalse(range.equals(null));
	}

	@Test
	public void testEqualsDifferentType() {
		assertFalse(range.equals("1.0 to 5.0"));
	}

	@Test
	public void testEqualsFullContract() {
		Range r1 = new Range(1.0, 5.0);
		Range r2 = new Range(1.0, 5.0);
		Range r3 = new Range(1.0, 5.0);
		assertTrue(r1.equals(r1));           // reflexive
		assertTrue(r1.equals(r2));           // symmetric
		assertTrue(r2.equals(r1));
		assertTrue(r1.equals(r2) && r2.equals(r3) && r1.equals(r3)); // transitive
		assertFalse(r1.equals(new Range(2.0, 6.0))); // negative
	}

	// =========================================================
	// FUNCTION 13: toString()
	// =========================================================

	@Test
	public void testToStringExactFormat() {
		assertEquals("Range[1.0,5.0]", range.toString().replaceAll("\\s+", ""));
	}

	@Test
	public void testToStringOtherRange() {
		assertEquals("Range[1.5,3.5]", new Range(1.5, 3.5).toString().replaceAll("\\s+", ""));
	}

	// =========================================================
	// FUNCTION 14: hashCode()
	// =========================================================

	@Test
	public void testHashCodeConsistency() {
		assertEquals("Equal ranges must have equal hash codes",
				range.hashCode(), new Range(1.0, 5.0).hashCode());
	}

	@Test
	public void testHashCodeStability() {
		int h = range.hashCode();
		assertEquals(h, range.hashCode());
		assertEquals(h, new Range(1.0, 5.0).hashCode());
	}

	@Test
	public void testHashCodeSensitiveToLowerBound() {
		assertNotEquals(new Range(1.0, 10.0).hashCode(), new Range(5.0, 10.0).hashCode());
	}

	@Test
	public void testHashCodeSensitiveToUpperBound() {
		assertNotEquals(new Range(5.0, 10.0).hashCode(), new Range(5.0, 20.0).hashCode());
	}

	@Test
	public void testHashCodePositiveVsNegativeDiffers() {
		assertNotEquals(new Range(1.0, 2.0).hashCode(), new Range(-2.0, -1.0).hashCode());
	}

	@Test
	public void testHashCodeMultipleRangesDistinct() {
		// Covers InlineConstantMutator, CRCR on constants 31 and 32, and
		// MathMutator on XOR/addition in accumulation
		int[] hashes = {
			new Range(1.0,  3.0).hashCode(),
			new Range(2.0,  3.0).hashCode(),
			new Range(1.0,  4.0).hashCode(),
			new Range(-1.0, 3.0).hashCode()
		};
		for (int i = 0; i < hashes.length; i++) {
			for (int j = i + 1; j < hashes.length; j++) {
				assertNotEquals("Hash " + i + " vs " + j + " must differ",
						hashes[i], hashes[j]);
			}
		}
	}

	@Test
	public void testHashCodeEqualsContractHolds() {
		Range r1 = new Range(2.2, 5.5);
		Range r2 = new Range(2.2, 5.5);
		assertTrue(r1.equals(r2));
		assertEquals(r1.hashCode(), r2.hashCode());
	}

	// =========================================================
	// FUNCTION 15 & 16: max/min (private, tested via reflection)
	// =========================================================

	private double invokeMax(double d1, double d2) throws Exception {
		Method m = Range.class.getDeclaredMethod("max", double.class, double.class);
		m.setAccessible(true);
		return (double) m.invoke(null, d1, d2);
	}

	private double invokeMin(double d1, double d2) throws Exception {
		Method m = Range.class.getDeclaredMethod("min", double.class, double.class);
		m.setAccessible(true);
		return (double) m.invoke(null, d1, d2);
	}

	@Test
	public void testMaxD1NaN() throws Exception {
		assertEquals( 3.0, invokeMax(Double.NaN,  3.0), 0.0);
		assertEquals(-7.5, invokeMax(Double.NaN, -7.5), 0.0);
		assertEquals( 0.0, invokeMax(Double.NaN,  0.0), 0.0);
		assertEquals(Double.POSITIVE_INFINITY, invokeMax(Double.NaN, Double.POSITIVE_INFINITY), 0.0);
		assertTrue(Double.isNaN(invokeMax(Double.NaN, Double.NaN)));
	}

	@Test
	public void testMaxD2NaN() throws Exception {
		assertEquals( 3.0, invokeMax( 3.0, Double.NaN), 0.0);
		assertEquals(-7.5, invokeMax(-7.5, Double.NaN), 0.0);
		assertEquals( 0.0, invokeMax( 0.0, Double.NaN), 0.0);
		assertEquals(Double.NEGATIVE_INFINITY, invokeMax(Double.NEGATIVE_INFINITY, Double.NaN), 0.0);
	}

	@Test
	public void testMaxBothReal() throws Exception {
		assertEquals( 9.0, invokeMax(9.0,  4.0),  0.0);
		assertEquals( 9.0, invokeMax(4.0,  9.0),  0.0);
		assertEquals( 5.0, invokeMax(5.0,  5.0),  0.0);
		assertEquals(-2.0, invokeMax(-2.0, -8.0), 0.0);
		assertEquals( 3.0, invokeMax(-3.0,  3.0), 0.0);
		assertEquals(Double.POSITIVE_INFINITY, invokeMax(Double.POSITIVE_INFINITY, 1000.0), 0.0);
	}

	@Test
	public void testMinD1NaN() throws Exception {
		assertEquals( 3.0, invokeMin(Double.NaN,  3.0), 0.0);
		assertEquals(-7.5, invokeMin(Double.NaN, -7.5), 0.0);
		assertEquals( 0.0, invokeMin(Double.NaN,  0.0), 0.0);
		assertEquals(Double.NEGATIVE_INFINITY, invokeMin(Double.NaN, Double.NEGATIVE_INFINITY), 0.0);
		assertTrue(Double.isNaN(invokeMin(Double.NaN, Double.NaN)));
	}

	@Test
	public void testMinD2NaN() throws Exception {
		assertEquals( 3.0, invokeMin( 3.0, Double.NaN), 0.0);
		assertEquals(-7.5, invokeMin(-7.5, Double.NaN), 0.0);
		assertEquals( 0.0, invokeMin( 0.0, Double.NaN), 0.0);
		assertEquals(Double.POSITIVE_INFINITY, invokeMin(Double.POSITIVE_INFINITY, Double.NaN), 0.0);
	}

	@Test
	public void testMinBothReal() throws Exception {
		assertEquals( 4.0, invokeMin(4.0,  9.0),  0.0);
		assertEquals( 4.0, invokeMin(9.0,  4.0),  0.0);
		assertEquals( 5.0, invokeMin(5.0,  5.0),  0.0);
		assertEquals(-8.0, invokeMin(-2.0, -8.0), 0.0);
		assertEquals(-3.0, invokeMin(-3.0,  3.0), 0.0);
		assertEquals(Double.NEGATIVE_INFINITY, invokeMin(Double.NEGATIVE_INFINITY, -1000.0), 0.0);
	}

	// =========================================================
	// FUNCTION 17: isNaNRange()
	// =========================================================

	@Test
	public void testIsNaNRangeBothNaN() {
		assertTrue(new Range(Double.NaN, Double.NaN).isNaNRange());
	}

	@Test
	public void testIsNaNRangeOnlyLowerNaN() {
		assertFalse(new Range(Double.NaN, 5.0).isNaNRange());
	}

	@Test
	public void testIsNaNRangeOnlyUpperNaN() {
		assertFalse(new Range(1.0, Double.NaN).isNaNRange());
	}

	@Test
	public void testIsNaNRangeNeitherNaN() {
		assertFalse(new Range(1.0, 5.0).isNaNRange());
		assertFalse(new Range(3.0, 3.0).isNaNRange());
		assertFalse(new Range(-5.0, -1.0).isNaNRange());
		assertFalse(new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).isNaNRange());
	}

	// =========================================================
	// FUNCTION 18: intersects(Range range)
	// =========================================================

	@Test
	public void testIntersectsRangeCompleteOverlap() {
		assertTrue(positiveRange.intersects(new Range(2.0, 4.0)));
	}

	@Test
	public void testIntersectsRangePartialOverlapLeft() {
		assertTrue(positiveRange.intersects(new Range(0.0, 2.0)));
	}

	@Test
	public void testIntersectsRangePartialOverlapRight() {
		assertTrue(positiveRange.intersects(new Range(4.0, 6.0)));
	}

	@Test
	public void testIntersectsRangeEnclosing() {
		assertTrue(positiveRange.intersects(new Range(-10.0, 10.0)));
	}

	@Test
	public void testIntersectsRangeIdentical() {
		assertTrue(positiveRange.intersects(new Range(1.0, 5.0)));
	}

	@Test
	public void testIntersectsRangeTouchingLowerBound() {
		assertFalse("Range ending exactly at lower bound should NOT intersect",
				positiveRange.intersects(new Range(0.0, 1.0)));
	}

	@Test
	public void testIntersectsRangeTouchingUpperBound() {
		assertFalse("Range starting exactly at upper bound should NOT intersect",
				positiveRange.intersects(new Range(5.0, 7.0)));
	}

	@Test
	public void testIntersectsRangeZeroLengthInside() {
		assertTrue(positiveRange.intersects(new Range(3.0, 3.0)));
	}

	@Test
	public void testIntersectsRangeCompletelyLeft() {
		assertFalse(positiveRange.intersects(new Range(-2.0, 0.0)));
	}

	@Test
	public void testIntersectsRangeCompletelyRight() {
		assertFalse(positiveRange.intersects(new Range(6.0, 8.0)));
	}

	@Test
	public void testIntersectsRangeNegativeVsPositive() {
		assertFalse(positiveRange.intersects(negativeRange));
	}

	@Test
	public void testIntersectsRangeMixedAndPositive() {
		assertTrue(positiveRange.intersects(mixedRange));
	}

	@Test
	public void testIntersectsRangeSymmetry() {
		Range a = new Range(1.0, 4.0);
		Range b = new Range(3.0, 7.0);
		assertTrue(a.intersects(b));
		assertTrue(b.intersects(a));
	}

	@Test
	public void testIntersectsRangeAllNegative() {
		Range r = new Range(-10.0, -3.0);
		assertTrue(r.intersects(new Range(-7.0,  -4.0)));
		assertTrue(r.intersects(new Range(-12.0, -5.0)));
		assertFalse(r.intersects(new Range(-2.0,  -1.0)));
		assertFalse(r.intersects(new Range(-15.0, -11.0)));
	}

	// =========================================================
	// FUNCTION 19: scale(Range base, double factor)
	// =========================================================

	@Test
	public void testScaleZeroFactor() {
		Range result = Range.scale(range, 0.0);
		assertEquals(0.0, result.getLowerBound(), 0.0);
		assertEquals(0.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleFactorOne() {
		Range result = Range.scale(range, 1.0);
		assertEquals(1.0, result.getLowerBound(), 0.0);
		assertEquals(5.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleFactorGreaterThanOne() {
		Range result = Range.scale(range, 2.0);
		assertEquals( 2.0, result.getLowerBound(), 0.0);
		assertEquals(10.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleFractionalFactor() {
		Range result = Range.scale(range, 0.5);
		assertEquals(0.5, result.getLowerBound(), 0.0);
		assertEquals(2.5, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleNegativeRange() {
		Range result = Range.scale(new Range(-5.0, -1.0), 3.0);
		assertEquals(-15.0, result.getLowerBound(), 0.0);
		assertEquals( -3.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleMixedRange() {
		Range result = Range.scale(new Range(-3.0, 3.0), 4.0);
		assertEquals(-12.0, result.getLowerBound(), 0.0);
		assertEquals( 12.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleZeroLengthRange() {
		Range result = Range.scale(new Range(2.0, 2.0), 5.0);
		assertEquals(10.0, result.getLowerBound(), 0.0);
		assertEquals(10.0, result.getUpperBound(), 0.0);
		assertEquals( 0.0, result.getLength(),     0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScaleNegativeFactorThrows() {
		Range.scale(range, -1.0);
	}

	@Test
	public void testScaleFactorZeroNotNegativeKillsCBM() {
		// CBM: factor<0 mutated to factor<=0 would throw for factor=0
		Range result = Range.scale(new Range(5.0, 10.0), 0.0);
		assertEquals(0.0, result.getLowerBound(), 0.0);
		assertEquals(0.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testScaleAsymmetricRangeKillsArgumentPropagation() {
		// Kills argument-propagation mutant (lower/upper arg swapped in Range constructor)
		Range r = Range.scale(new Range(2.0, 6.0), 2.0);
		assertEquals("lower=2*2=4",  4.0, r.getLowerBound(), 0.0);
		assertEquals("upper=6*2=12", 12.0, r.getUpperBound(), 0.0);
	}

	// =========================================================
	// FUNCTION 20: combineIgnoringNaN(Range range1, Range range2)
	// =========================================================

	@Test
	public void testCombineIgnoringNaNBothNull() {
		assertNull(Range.combineIgnoringNaN(null, null));
	}

	@Test
	public void testCombineIgnoringNaNRange1NullRange2NaN() {
		assertNull(Range.combineIgnoringNaN(null, new Range(Double.NaN, Double.NaN)));
	}

	@Test
	public void testCombineIgnoringNaNRange1NullRange2Normal() {
		Range r2 = new Range(2.0, 6.0);
		assertEquals(r2, Range.combineIgnoringNaN(null, r2));
	}

	@Test
	public void testCombineIgnoringNaNRange1NaNRange2Null() {
		assertNull(Range.combineIgnoringNaN(new Range(Double.NaN, Double.NaN), null));
	}

	@Test
	public void testCombineIgnoringNaNRange1NormalRange2Null() {
		Range r1 = new Range(1.0, 5.0);
		assertEquals(r1, Range.combineIgnoringNaN(r1, null));
	}

	@Test
	public void testCombineIgnoringNaNBothNaN() {
		assertNull(Range.combineIgnoringNaN(
				new Range(Double.NaN, Double.NaN),
				new Range(Double.NaN, Double.NaN)));
	}

	@Test
	public void testCombineIgnoringNaNBothNormal() {
		Range result = Range.combineIgnoringNaN(new Range(1.0, 5.0), new Range(3.0, 8.0));
		assertNotNull(result);
		assertEquals(1.0, result.getLowerBound(), 0.0001);
		assertEquals(8.0, result.getUpperBound(), 0.0001);
	}

	@Test
	public void testCombineIgnoringNaNPrecision() {
		Range result = Range.combineIgnoringNaN(new Range(1.3, 4.7), new Range(2.2, 9.9));
		assertEquals(1.3, result.getLowerBound(), 0.0000001);
		assertEquals(9.9, result.getUpperBound(), 0.0000001);
	}

	@Test
	public void testCombineIgnoringNaNNormalRangesNotNullKillsNakedReceiver() {
		// NakedReceiver mutant: isNaNRange() replaced with receiver (always truthy)
		// would wrongly return null for normal ranges
		Range result = Range.combineIgnoringNaN(new Range(1.0, 5.0), new Range(3.0, 7.0));
		assertNotNull("Two normal ranges must not produce null", result);
		assertEquals(1.0, result.getLowerBound(), 0.0);
		assertEquals(7.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testCombineIgnoringNaNNullPlusNormalNotNullKillsNakedReceiver() {
		Range result = Range.combineIgnoringNaN(null, new Range(4.0, 9.0));
		assertNotNull("null + normal range should return the normal range", result);
		assertEquals(4.0, result.getLowerBound(), 0.0);
		assertEquals(9.0, result.getUpperBound(), 0.0);
	}

	@Test
	public void testCombineIgnoringNaNOnlyLowerNaNKillsRorMutant() {
		// ROR mutant: && mutated to || would wrongly return null when only one bound is NaN
		assertNotNull(Range.combineIgnoringNaN(new Range(Double.NaN, 5.0), new Range(2.0, 8.0)));
	}

	@Test
	public void testCombineIgnoringNaNOnlyUpperNaNKillsRorMutant() {
		assertNotNull(Range.combineIgnoringNaN(new Range(1.0, 5.0), new Range(2.0, Double.NaN)));
	}
}
