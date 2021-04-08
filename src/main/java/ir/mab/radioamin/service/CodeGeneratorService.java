package ir.mab.radioamin.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CodeGeneratorService {
    public String generateActivationCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
