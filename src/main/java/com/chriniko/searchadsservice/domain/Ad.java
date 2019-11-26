package com.chriniko.searchadsservice.domain;

import lombok.Getter;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Ad {

    protected String id;
    protected String text;
    protected int customerId;
    protected Date createdAt;

    public Ad(String adDiscriminator /* Note: used for Proof of Concept - testing */)
    {
        id = UUID.randomUUID().toString();
        text = "This is the ad text --- " + adDiscriminator;

        Random rnd = ThreadLocalRandom.current();

        customerId = Math.abs(rnd.nextInt(500));
        createdAt = Calendar.getInstance().getTime();
    }
}
