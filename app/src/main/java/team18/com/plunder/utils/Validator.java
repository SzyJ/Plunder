package team18.com.plunder.utils;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guillermochibas on 08/04/2017.
 */

public class Validator {

    private Matcher matcher;
    private Pattern regexPattern;
    public boolean isValid;

    private static final String EMAIL_REGEX =
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\" +
            "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])" +
            "?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]" +
            "?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x" +
            "0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String PASSWORD_REGEX =
            "(?=.*[0-9])(?=.*[A-Za-z])(.{6,})";

    public boolean validateRegister(EditText name, EditText email, EditText password, EditText password2, EditText birthDate){
        EditText[] fields = {name, email, password, password2, birthDate};
        for(EditText i:fields){
            i.setError(null);
        }
        setValid(true);

        if (checkEmpty(fields)) {
            checkEmpty(fields);
            setValid(false);
        } else if((ValidateEmail(email) || ValidatePassword(password))){
            ValidateEmail(email);
            ValidatePassword(password);
            setValid(false);
        } else if(ValidateSecondPassword(password, password2)){
            ValidateSecondPassword(password, password2);
            setValid(false);
        }
        return isValid();
    }

    public boolean validateLogin(EditText email, EditText password){
        EditText[] fields = {email, password};
        for(EditText i:fields){
            i.setError(null);
        }
        setValid(true);

        if (checkEmpty(fields)) {
            checkEmpty(fields);
            setValid(false);
        } else if((ValidateEmail(email) || ValidatePassword(password))){
            ValidateEmail(email);
            ValidatePassword(password);
            setValid(false);
        }
        return isValid();
    };

    public boolean regexValidator(final String pattern, String regex){
        regexPattern = Pattern.compile(regex);
        matcher = regexPattern.matcher(pattern);
        return matcher.matches();
    }

    public boolean checkEmpty(EditText[] fields){
        boolean isEmpty = false;

        for(EditText i : fields){
            String s = i.getText().toString();
            if(s.matches("")){
                i.setError("This field is required");
                isEmpty = true;
            }
        }
        return isEmpty;
    }

    public boolean ValidateEmail(EditText email) {
        Validator validator = new Validator();

        if (validator.regexValidator(email.getText().toString(), EMAIL_REGEX) == false) {
            email.setError("Invalid Email");
            return true;
        }
        return false;
    }

    public boolean ValidatePassword(EditText password) {
        Validator validator = new Validator();

        if (validator.regexValidator(password.getText().toString(), PASSWORD_REGEX) == false) {
            password.setError("Password must contain one alphabetical character (a-z), " +
                    "one numeric (0-9) and be at least 6 characters long");
            return true;
        }
        return false;
    }

    public boolean ValidateSecondPassword(EditText password, EditText password2) {
        String sPassword = password.getText().toString();
        String sPassword2 = password2.getText().toString();

        if (sPassword2.matches(sPassword) == false) {
            password2.setError("Password doesn't match");
            return true;
        }
        return false;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}
