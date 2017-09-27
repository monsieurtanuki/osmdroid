package org.osmdroid.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.osmdroid.util.constants.GeoConstants;
import org.osmdroid.views.util.constants.MathConstants;

import java.util.Random;

public class GeoPointTest {

	private static Random random = new Random();

	/**
	 * Testing that a distance from a point to the same point is 0
	 * @since 5.6.6
	 */
	@Test
	public void test_distanceTo_itself() {
		final double distancePrecisionDelta = 0;
		final int iterations = 100;
		for (int i = 0 ; i < iterations ; i ++) {
			final GeoPoint target = new GeoPoint(getRandomLatitude(), getRandomLongitude());
			final GeoPoint other = new GeoPoint(target);
			assertEquals("distance to self is zero for " + target, 0, target.distanceToAsDouble(other), distancePrecisionDelta);
			assertEquals("reverse distance to self is zero for " + other, 0, other.distanceToAsDouble(target), distancePrecisionDelta);
		}
	}

	/**
	 * Testing on Equator with an obvious check formula
	 * @since 5.6.6
	 */
	@Test
	public void test_distanceTo_Equator() {
		final double ratioDelta = 1E-10;
		final int iterations = 100;
		final double latitude = 0;
		for (int i = 0 ; i < iterations ; i ++) {
			final double longitude1 = getRandomLongitude();
			final double longitude2 = getRandomLongitude();
			final GeoPoint target = new GeoPoint(latitude, longitude1);
			final GeoPoint other = new GeoPoint(latitude, longitude2);

			final double diff = getCleanLongitudeDiff(longitude1, longitude2);
			final double expected = GeoConstants.RADIUS_EARTH_METERS * diff * MathConstants.DEG2RAD;
			final double delta = expected * ratioDelta;
			assertEquals("distance between " + target + " and " + other,
					expected, target.distanceToAsDouble(other), delta);
		}
	}

	/**
	 * Testing more specifically very close GeoPoints
	 * @since 5.6.6
	 */
	@Test
	public void test_distanceTo_Equator_Smaller() {
		final double ratioDelta = 1E-5;
		final int iterations = 10;
		final double latitude = 0;
		double longitudeIncrement = 1;
		for (int i = 0 ; i < iterations ; i ++) {
			final double longitude1 = getRandomLongitude();
			final double longitude2 = longitude1 + longitudeIncrement;
			longitudeIncrement /= 10.;
			final GeoPoint target = new GeoPoint(latitude, longitude1);
			final GeoPoint other = new GeoPoint(latitude, longitude2);
			final double diff = getCleanLongitudeDiff(longitude1, longitude2);
			final double expected = GeoConstants.RADIUS_EARTH_METERS * diff * MathConstants.DEG2RAD;
			final double delta = expected * ratioDelta;
			assertEquals("distance between " + target + " and " + other,
					expected, target.distanceToAsDouble(other), delta);
		}
	}

	/**
	 * Testing on all parallels with a not so obvious check formula
	 * @since 5.6.6
	 */
	@Test
	public void test_distanceTo_Parallels() {
		final double ratioDelta = 1E-5;
		final int iterations = 100;
		for (int i = 0 ; i < iterations ; i ++) {
			final double latitude = getRandomLatitude();
			final double longitude1 = getRandomLongitude();
			final double longitude2 = getRandomLongitude();
			final GeoPoint target = new GeoPoint(latitude, longitude1);
			final GeoPoint other = new GeoPoint(latitude, longitude2);

			final double diff = getCleanLongitudeDiff(longitude1, longitude2);
			final double expected = GeoConstants.RADIUS_EARTH_METERS * 2 * Math.asin(
					Math.cos(latitude * MathConstants.DEG2RAD)*Math.sin(diff * MathConstants.DEG2RAD/ 2));
			final double delta = expected * ratioDelta;
			assertEquals("distance between " + target + " and " + other,
					expected, target.distanceToAsDouble(other), delta);
		}
	}

	@Test
	public void test_bearingTo_north() {
		final GeoPoint target = new GeoPoint(0.0, 0.0);
		final GeoPoint other = new GeoPoint(10.0, 0.0);
		assertEquals("directly north", 0, Math.round(target.bearingTo(other)));
	}

	@Test
	public void test_bearingTo_east() {
		final GeoPoint target = new GeoPoint(0.0, 0.0);
		final GeoPoint other = new GeoPoint(0.0, 10.0);
		assertEquals("directly east", 90, Math.round(target.bearingTo(other)));
	}

	@Test
	public void test_bearingTo_south() {
		final GeoPoint target = new GeoPoint(0.0, 0.0);
		final GeoPoint other = new GeoPoint(-10.0, 0.0);
		assertEquals("directly south", 180, Math.round(target.bearingTo(other)));
	}

	@Test
	public void test_bearingTo_west() {
		final GeoPoint target = new GeoPoint(0.0, 0.0);
		final GeoPoint other = new GeoPoint(0.0, -10.0);
		assertEquals("directly west", 270, Math.round(target.bearingTo(other)));
	}

	@Test
	public void test_bearingTo_north_west() {
		final GeoPoint target = new GeoPoint(0.0, 0.0);
		final GeoPoint other = new GeoPoint(-10.0, -10.0);
		assertEquals("north west", 180 + 45, Math.round(target.bearingTo(other)));
	}

	@Test
	public void test_destinationPoint_north_west_here() {
		// this test is based on the actual result, not calculated expectations,
		// but it is at least a basic sanity check for rounding errors and regression
		final GeoPoint start = new GeoPoint(52.387524, 4.891604);
		final GeoPoint end = new GeoPoint(52.3906999098817, 4.886399738626785);
		assertEquals("destinationPoint north west", end, start.destinationPoint(500, -45));
	}

	@Test
	public void test_toFromString_withoutAltitude() {
		final GeoPoint in = new GeoPoint(52387524, 4891604);
		final GeoPoint out = GeoPoint.fromIntString("52387524,4891604");
		assertEquals("toFromString without altitude", in, out);
	}

	@Test
	public void test_toFromString_withAltitude() {
		final GeoPoint in = new GeoPoint(52387524, 4891604, 12345);
          System.out.println("GeoPoint to intString " + in.toIntString());
          System.out.println("GeoPoint to doubleString " + in.toDoubleString());
          System.out.println("GeoPoint to toString " + in.toString());
		final GeoPoint out = GeoPoint.fromIntString(in.toIntString());
		assertEquals("toFromString with altitude", in, out);
	}

	@Test
	public void test_toFromDoubleString_withoutAltitude() {
		final GeoPoint in = new GeoPoint(-117.123, 33.123);
		final GeoPoint out = GeoPoint.fromDoubleString("-117.123,33.123", ',');
		assertEquals("toFromString without altitude", in, out);
	}

	@Test
	public void test_toFromDoubleString_withAltitude() {
		final GeoPoint in = new GeoPoint(-117.123, 33.123, 12345);
		final GeoPoint out = GeoPoint.fromDoubleString(in.toDoubleString(), ',');
		assertEquals("toFromString with altitude", in, out);
	}

	@Test
	public void test_toFromInvertedDoubleString_withoutAltitude() {
		final GeoPoint in = new GeoPoint(-117.123, 33.123);
		final GeoPoint out = GeoPoint.fromInvertedDoubleString("33.123,-117.123", ',');
		assertEquals("toFromString without altitude", in, out);
	}

	@Test
	public void test_toFromInvertedDoubleString_withAltitude() {
		final GeoPoint in = new GeoPoint(-117.123, 33.123, 12345);
		final GeoPoint out = GeoPoint.fromInvertedDoubleString(in.toInvertedDoubleString(), ',');
		assertEquals("toFromString with altitude", in, out);
	}


	/**
	 * @since 5.6.6
	 */
	private double getCleanLongitudeDiff(final double pLongitude1, final double pLongitude2) {
		double diff = Math.abs(pLongitude1 - pLongitude2);
		if (diff > TileSystem.MaxLongitude) {
			diff = TileSystem.MaxLongitude - TileSystem.MinLongitude - diff;
		}
		return diff;
	}

	/**
	 * @since 5.6.6
	 */
	private double getRandomLongitude() {
		return TileSystem.getRandomLongitude(random.nextDouble());
	}

	/**
	 * @since 5.6.6
	 */
	private double getRandomLatitude() {
		return TileSystem.getRandomLatitude(random.nextDouble(), TileSystem.MinLatitude);
	}
}
