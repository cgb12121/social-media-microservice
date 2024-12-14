package com.mine.socialmedia.verificationservice.service;

import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class OtpService {

    //TODO: hashing before storing
    private final RedisTemplate<UUID, String> redisTemplate;

    private final String twilioPhoneNumber;

    @Autowired
    public OtpService(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.phone-number}") String twilioPhoneNumber,
            RedisTemplate<UUID, String> redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.twilioPhoneNumber = twilioPhoneNumber;
        Twilio.init(accountSid, authToken);
    }

    public String generateOtp(UUID identifier) {
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        redisTemplate.opsForValue().set(identifier, otp, 1, TimeUnit.MINUTES);
        return otp;
    }

    public boolean validateOtp(UUID identifier, String userInputOtp) {
        String storedOtp = redisTemplate.opsForValue().get(identifier);
        if (storedOtp != null && storedOtp.equals(userInputOtp)) {
            redisTemplate.delete(identifier);
            return true;
        }
        return false;
    }

    //TODO: consider async
    public void sendOtp(String toPhoneNumber, String otp) {
        StringBuilder body = new StringBuilder();
        body.append("Your OTP is: ").append(otp).append("\n").append("It will expire in 1 minute");
        try {
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    body.toString()
            ).create();
            log.info("OTP send to {} is: {}", toPhoneNumber, otp);
        } catch (TwilioException e) {
            log.error("Error sending OTP to {}. \n {}", toPhoneNumber, e.toString());
        }
    }
}
