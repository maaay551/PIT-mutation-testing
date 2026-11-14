package pitstudy;

/** A small, intentionally straightforward application for comparing testing tools.
 * All subtotals are whole dollars to keep the experiment easy to reproduce. */
public class OrderRules {

    /** $5 shipping below $50; free shipping at $50 or above. */
    public int shippingFee(int subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        if (subtotal >= 50) {
            return 0;
        }
        return 5;
    }

    /** 10% off starting at $100; integer-dollar discount rounds down. */
    public int discountedTotal(int subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        if (subtotal >= 100) {
            return subtotal - subtotal / 10;
        }
        return subtotal;
    }

    /** Coupons require membership AND an order of at least $75. */
    public boolean couponEligible(int subtotal, boolean member) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        return member && subtotal >= 75;
    }

    /** One loyalty point for every complete $10 spent. */
    public int loyaltyPoints(int subtotal) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        return subtotal / 10;
    }
}
