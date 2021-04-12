package com.pycogroup.superblog.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AddressTest {
    @Test
    public void testAddress() {
        Address address = new Address("22 Pham Don","HCM", "vietnam");
        address.setStreetAddress("nguyen van cu");
        Assert.assertEquals("nguyen van cu", address.getStreetAddress());
        address.setCity("phu yen");
        Assert.assertEquals("phu yen", address.getCity());
        address.setCountry("Viet Nam");
        Assert.assertEquals("Viet Nam", address.getCountry());
        String expectedToString = "Address(" +
                "city=" + address.getCity() +
                ", country=" + address.getCountry() +
                ", streetAddress=" + address.getStreetAddress() +
                ')';
        Assert.assertEquals(expectedToString, address.toString());
    }
}
