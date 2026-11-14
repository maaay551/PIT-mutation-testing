package pitstudy;

import org.junit.Test;
import static org.junit.Assert.*;

/** Suite A: ordinary/typical inputs only; incomplete branch coverage. */
public class BasicTest {
    private final OrderRules rules = new OrderRules();

    @Test
    public void paidShipping() {
        assertEquals(5, rules.shippingFee(30));
    }

    @Test
    public void freeShipping() {
        assertEquals(0, rules.shippingFee(70));
    }

    @Test
    public void noDiscount() {
        assertEquals(60, rules.discountedTotal(60));
    }

    @Test
    public void discountApplied() {
        assertEquals(135, rules.discountedTotal(150));
    }

    @Test
    public void couponForMember() {
        assertTrue(rules.couponEligible(90, true));
    }

    @Test
    public void couponForNonmember() {
        assertFalse(rules.couponEligible(90, false));
    }

    @Test
    public void loyaltyForNormalOrder() {
        assertEquals(8, rules.loyaltyPoints(80));
    }

}
