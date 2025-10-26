package dk.lundogbendsen.springbootcourse.testing.validation;

// Code from https://www.computerworld.dk/eksperten/spm/584620
public class CprValidation {
    public boolean validateCpr(String cpr)
    {
        if(cpr==null || cpr.length()!=10) return false;

        char[] cprArray=cpr.toCharArray();

        for(int x=0;x<cprArray.length;x++)
        {
            if(!Character.isDigit(cprArray[x])) return false;
            cprArray[x]-='0';
        }

        char[] talArray={4,3,2,7,6,5,4,3,2,1};

        int sum=0;
        for(int x=0;x<10;x++)
        {
            sum+=cprArray[x]*talArray[x];
        }

        if((sum%11)!=0) return false;
        return true;
    }
}
