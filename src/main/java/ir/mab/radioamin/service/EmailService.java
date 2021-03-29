package ir.mab.radioamin.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService{
    @Override
    public void sendActivationCode(String activationCode) {
        System.out.println("Activation Code: " + activationCode);
    }
}
