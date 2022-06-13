package helpers;

public class Timer {
    private int secondsToCount;
    private int secondsCounter;
    private boolean isCounting;


    public Timer(int secondsToCount) {
        this.secondsToCount = secondsToCount;
        this.secondsCounter = 0;
    }


    public void start(){
        if(!isCounting)
            isCounting = true;
    }


    public void pause(){
        if(!isCounting)
            isCounting = false;
    }


    public int getTimePassed(){
        return this.secondsCounter;
    }

    public void increaseSecondsCounter(){
        secondsCounter++;
    }

    public boolean doesTimeHasElapsed(){
        return secondsCounter >= secondsToCount;
    }

    public void reset(){
        this.secondsCounter = 0;
    }



}
