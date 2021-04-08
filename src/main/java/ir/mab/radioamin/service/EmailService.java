package ir.mab.radioamin.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendActivationCode(String activationCode) {
        System.out.println("Activation Code: " + activationCode);
    }
}
