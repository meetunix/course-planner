package de.uni.swt.spring.backend.bohnen;

import de.uni.swt.spring.app.mail.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class TestBohne {

    @Autowired
    EmailServiceImpl emailService;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    public String getMessage(String user, String passwort){
        //emailService.sendSimpleMessage("swt-operator@swt-planer.de","test@test.de","Passwort SWT Planer","Testmail");
        return "Button was clicked at "+ LocalTime.now().format(dtf)+"\n"+user+" "+passwort;
    }

    public void sendMail(){
        emailService.sendSimpleMessage("swt-operator@swt-planer.de","test@test.de","Passwort SWT Planer","Testmail");
    }

    public void sendMail(String email, String pass){
    	if (!email.matches(".*@uni-rostock.test")){
    		emailService.sendSimpleMessage("swt-operator@swt-planer.de",
    				email,
    				"Passwort SWT Planer",
    				"Ihr Passwort lautet: "+pass);
    	}
    }
}
