package org.jfree.data;

import static org.junit.Assert.*;
import org.junit.Test;
import java.security.InvalidParameterException;

public class DataUtilitiesTest {

    // ----------------------------
    // calculateColumnTotal(Values2D data, int column)
    // data: { EC1: Valid non-null Values2D, EC2: null Values2D,
	//         EC3: Values2D with some null cells }
	//       BVA: N/A
    // column: { EC4: column < 0, EC5: 0 <= column < columnCount,
	//           EC6: column >= columnCount }
	//         BVA: { BLB: -1, LB: 0, BUB: columnCount - 1, UB: columnCount }
    // ----------------------------

    // EC1 + EC5 (LB)
    @Test
    public void calculateColumnTotalForTwoValuesWithLBColumn() {
        // setup
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.addValue(7.5, 0, 0);
        values.addValue(-2.5, 1, 0);

        // exercise
        double result = DataUtilities.calculateColumnTotal(values, 0);

        // verify
        assertEquals("The column total for two values should be calculated correctly", 5.0, result, .000000001d);

        // tear-down: NONE in this test method
    }

    // EC2
    @Test(expected = InvalidParameterException.class)
    public void calculateColumnTotalForNull() {
        // exercise
        DataUtilities.calculateColumnTotal(null, 0);
    }

    // EC3 + EC5 (BUB)
    @Test
    public void calculateColumnTotalForValuesWithNullAndBUBColumn() {
        // setup
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.addValue(null, 0, 0);
        values.addValue(4.0, 0, 1);
        values.addValue(null, 1, 1);

        // exercise
        double result = DataUtilities.calculateColumnTotal(values, 1);

        // verify
        assertEquals("The column total should handle null values correctly", 4.0, result, .000000001d);

        // tear-down: NONE in this test method
    }

//    // EC4 (BLB) removed for assignment 4
//    @Test
//    public void calculateColumnTotalForBLBColumn() {
//        // setup
//        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
//        values.addValue(1.0, 0, 0);
//        values.addValue(2.0, 1, 0);
//
//        // exercise
//        double result = DataUtilities.calculateColumnTotal(values, -1);
//
//        // verify
//        assertEquals("The column total for a column index below the lower bound should be 0.0", 0.0, result,
//                .000000001d);
//
//        // tear-down: NONE in this test method
//    }

//    // EC6 (UB) removed for assignment 4
//    @Test
//    public void calculateColumnTotalForUBColumn() {
//        // setup
//        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
//        values.addValue(1.0, 0, 0);
//        values.addValue(2.0, 1, 0);
//
//        // exercise
//        double result = DataUtilities.calculateColumnTotal(values, 2);
//
//        // verify
//        assertEquals("The column total for a column index above the upper bound should be 0.0", 0.0, result,
//                .000000001d);
//
//        // tear-down: NONE in this test method
//    }
    
    

    // ----------------------------
    // calculateRowTotal(Values2D data, int row)
    // data: { EC1: Valid non-null Values2D, EC2: null Values2D,
	//         EC3: Values2D with some null cells }
	//	     BVA: N/A
    // row: { EC4: row < 0, EC5: 0 <= row < rowCount, EC6: row >= rowCount }
	//	    BVA: { BLB: -1, LB: 0, BUB: rowCount - 1, UB: rowCount }
    // ----------------------------

    // EC1 + EC5 (LB)
    @Test
    public void calculateRowTotalForTwoValuesWithLBRow() {
        // setup
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.addValue(-2.0, 0, 0);
        values.addValue(4.0, 0, 1);

        // exercise
        double result = DataUtilities.calculateRowTotal(values, 0);

        // verify
        assertEquals("The row total for two values should be calculated correctly", 2.0, result, .000000001d);

        // tear-down: NONE in this test method
    }

    // EC2
    @Test(expected = InvalidParameterException.class)
    public void calculateRowTotalForNull() {
        // exercise
        DataUtilities.calculateRowTotal(null, 0);
    }

    // EC3 + EC5 (BUB)
    @Test
    public void calculateRowTotalForValuesWithNullAndBUBRow() {
        // setup
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.addValue(null, 0, 0);
        values.addValue(null, 0, 1);
        values.addValue(6.0, 1, 0);
        values.addValue(null, 1, 1);

        // exercise
        double result = DataUtilities.calculateRowTotal(values, 1);

        // verify
        assertEquals("The row total should handle null values correctly", 6.0, result, .000000001d);

        // tear-down: NONE in this test method
    }

//    // EC4 (BLB) Removed for Assignment 4
//    @Test
//    public void calculateRowTotalForBLBRow() {
//        // setup
//        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
//        values.addValue(1.0, 0, 0);
//        values.addValue(2.0, 0, 1);
//
//        // exercise
//        double result = DataUtilities.calculateRowTotal(values, -1);
//
//        // verify
//        assertEquals("The row total for a row index below the lower bound should be 0.0", 0.0, result, .000000001d);
//
//        // tear-down: NONE in this test method
//    }

//    // EC6 (UB) Removed for assignment 4
//    @Test
//    public void calculateRowTotalForUBRow() {
//        // setup
//        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
//        values.addValue(1.0, 0, 0);
//        values.addValue(2.0, 0, 1);
//
//        // exercise
//        double result = DataUtilities.calculateRowTotal(values, 2);
//
//        // verify
//        assertEquals("The row total for a row index above the upper bound should be 0.0", 0.0, result, .000000001d);
//
//        // tear-down: NONE in this test method
//    }
    

    // ----------------------------
    // createNumberArray(double[] data)
    // data: { EC1: valid non-null array, EC2: null array, EC3: empty array,
	//	       EC4: mixed negative/positive values, EC5: contains NaN/Infinity }
	//	     BVA: N/A
    // ----------------------------

    // EC1: valid non-null array
    @Test
    public void createNumberArrayWithValidData() {
        // setup
        double[] data = { 1.5, 2.0, 3.5 };
        Number[] expected = { 1.5, 2.0, 3.5 };

        // exercise
        Number[] result = DataUtilities.createNumberArray(data);

        // verify
        assertArrayEquals("The created Number array should match the input double array", expected, result);
    }

    // EC2: null array
    @Test(expected = InvalidParameterException.class)
    public void createNumberArrayForNull() {
        // exercise
        DataUtilities.createNumberArray(null);
    }

    // EC3: empty array
    @Test
    public void createNumberArrayWithEmptyArray() {
        // setup
        double[] data = {};
        Number[] expected = {};

        // exercise
        Number[] result = DataUtilities.createNumberArray(data);

        // verify
        assertArrayEquals("The created Number array should be empty", expected, result);
    }

    // EC4: mixed negative/positive values
    @Test
    public void createNumberArrayWithMixedValues() {
        // setup
        double[] data = { -1.5, 0.0, 4.2 };
        Number[] expected = { -1.5, 0.0, 4.2 };

        // exercise
        Number[] result = DataUtilities.createNumberArray(data);

        // verify
		assertArrayEquals("The created Number array should handle mixed positive, negative, and zero values", expected, result);
    }

    // EC5: contains NaN/Infinity
    @Test
    public void createNumberArrayWithSpecialValues() {
        // setup
        double[] data = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
        Number[] expected = { Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };

        // exercise
        Number[] result = DataUtilities.createNumberArray(data);

        // verify
        assertArrayEquals("The created Number array should correctly store NaN and Infinity values", expected, result);
    }
    

    // ----------------------------
    // createNumberArray2D(double[][] data)
    // data: { EC1: valid non-null rectangular 2D array, EC2: null outer array,
	//	       EC3: contains null inner row, EC4: empty outer array,
	//	       EC5: some empty inner rows, EC6: contains NaN/Infinity }
	//	     BVA: N/A
    // ----------------------------

    // EC1: valid non-null rectangular 2D array
    @Test
    public void createNumberArray2DWithValidData() {
        // setup
        double[][] data = { { 1.5, 2.5 }, { 3.5, 4.5 } };
        Number[][] expected = { { 1.5, 2.5 }, { 3.5, 4.5 } };

        // exercise
        Number[][] result = DataUtilities.createNumberArray2D(data);

        // verify
        assertEquals("Outer array length should be 2", expected.length, result.length);
        assertArrayEquals("First inner array should match", expected[0], result[0]);
        assertArrayEquals("Second inner array should match", expected[1], result[1]);
    }

    // EC2: null outer array
    @Test(expected = InvalidParameterException.class)
    public void createNumberArray2DForNull() {
        // exercise
        DataUtilities.createNumberArray2D(null);
    }

    // EC3: contains null inner row
    @Test(expected = InvalidParameterException.class)
    public void createNumberArray2DWithNullInnerRow() {
        // setup
        double[][] data = { { 1.5, 2.5 }, null };

        // exercise
        DataUtilities.createNumberArray2D(data);
    }

    // EC4: empty outer array
    @Test
    public void createNumberArray2DWithEmptyOuterArray() {
        // setup
        double[][] data = {};

        // exercise
        Number[][] result = DataUtilities.createNumberArray2D(data);

        // verify
        assertEquals("Resulting outer array should be empty", 0, result.length);
    }

    // EC5: some empty inner rows
    @Test
    public void createNumberArray2DWithEmptyInnerRow() {
        // setup
        double[][] data = { { 1.5, 2.5 }, {} };
        Number[] expectedFirstRow = { 1.5, 2.5 };
        Number[] expectedSecondRow = {};

        // exercise
        Number[][] result = DataUtilities.createNumberArray2D(data);

        // verify
        assertEquals("Outer array length should be 2", 2, result.length);
        assertArrayEquals("First inner array should match", expectedFirstRow, result[0]);
        assertArrayEquals("Second inner array should be empty", expectedSecondRow, result[1]);
    }

    // EC6: contains NaN/Infinity
    @Test
    public void createNumberArray2DWithSpecialValues() {
        // setup
        double[][] data = { { Double.NaN, Double.POSITIVE_INFINITY }, { Double.NEGATIVE_INFINITY, 0.0 } };
        Number[][] expected = { { Double.NaN, Double.POSITIVE_INFINITY }, { Double.NEGATIVE_INFINITY, 0.0 } };

        // exercise
        Number[][] result = DataUtilities.createNumberArray2D(data);

        // verify
        assertEquals("Outer array length should be 2", expected.length, result.length);
        assertArrayEquals("First inner array should match special values", expected[0], result[0]);
        assertArrayEquals("Second inner array should match special values", expected[1], result[1]);
    }

    // ----------------------------
    // getCumulativePercentages(KeyedValues data)
    // data: { EC1: valid non-null with positive values,
	//	       EC2: null data,
	//	       EC3: contains zeros,
	//	       EC4: contains negative values,
	//	       EC5: contains null item values,
	//	       EC6: sum of all values = 0 }
	//	      BVA: N/A
    // ----------------------------

    // EC1
    @Test
    public void getCumulativePercentagesForPositiveValues() {
        // setup
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue((Comparable) 0, 5);
        values.addValue((Comparable) 1, 9);
        values.addValue((Comparable) 2, 2);

        // exercise
        KeyedValues result = DataUtilities.getCumulativePercentages(values);

        // verify
        assertEquals("The first cumulative percentage should be correct for positive values", 0.3125,
                result.getValue(0).doubleValue(), 0.000000001d);
        assertEquals("The second cumulative percentage should be correct for positive values", 0.875,
                result.getValue(1).doubleValue(), 0.000000001d);
        assertEquals("The third cumulative percentage should be correct for positive values", 1.0,
                result.getValue(2).doubleValue(), 0.000000001d);
    }

    // EC2
    @Test(expected = InvalidParameterException.class)
    public void getCumulativePercentagesForNull() {
        // exercise
        DataUtilities.getCumulativePercentages(null);
    }

    // EC3
    @Test
    public void getCumulativePercentagesForDataContainsZero() {
        // setup
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue((Comparable) 0, 0);
        values.addValue((Comparable) 1, 5);

        // exercise
        KeyedValues result = DataUtilities.getCumulativePercentages(values);

        // verify
        assertEquals("The cumulative percentage for a zero value should be correct", 0.0,
                result.getValue(0).doubleValue(), 0.000000001d);
        assertEquals("The cumulative percentage after a zero value should be correct", 1.0,
                result.getValue(1).doubleValue(), 0.000000001d);
    }

    // EC4
    @Test
    public void getCumulativePercentagesForDataContainsNegative() {
        // setup
        DefaultKeyedValues values = new DefaultKeyedValues();
        // values [-1, 2] -> total = 1, cumulative ratios: [-1/1, (-1+2)/1] = [-1.0,
        // 1.0]
        values.addValue((Comparable) 0, -1);
        values.addValue((Comparable) 1, 2);

        // exercise
        KeyedValues result = DataUtilities.getCumulativePercentages(values);

        // verify
        assertEquals("The cumulative percentage should handle negative values correctly", -1.0,
                result.getValue(0).doubleValue(), 0.000000001d);
        assertEquals("The cumulative percentage after a negative value should be correct", 1.0,
                result.getValue(1).doubleValue(), 0.000000001d);
    }

    // EC5
    @Test
    public void getCumulativePercentagesForDataContainsNull() {
        // setup
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue((Comparable) 0, 5);
        values.addValue((Comparable) 1, null);

        // exercise
        KeyedValues result = DataUtilities.getCumulativePercentages(values);

        // verify
        assertEquals("The cumulative percentage before a null value should be correct", 1.0,
                result.getValue(0).doubleValue(), 0.000000001d);
        assertEquals("The cumulative percentage for a null value should be correct", 1.0,
                result.getValue(1).doubleValue(), 0.000000001d);
    }

    // EC6
    @Test
    public void getCumulativePercentagesForDataSumIsZero() {
        // setup
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue((Comparable) 0, 0);
        values.addValue((Comparable) 1, 0);
        values.addValue((Comparable) 2, 0);

        // exercise
        KeyedValues result = DataUtilities.getCumulativePercentages(values);

        // verify
        assertTrue("Cumulative percentage should be NaN when the sum is zero (item 0)",
                Double.isNaN(result.getValue(0).doubleValue()));
        assertTrue("Cumulative percentage should be NaN when the sum is zero (item 1)",
                Double.isNaN(result.getValue(1).doubleValue()));
        assertTrue("Cumulative percentage should be NaN when the sum is zero (item 2)",
                Double.isNaN(result.getValue(2).doubleValue()));
    }

}
