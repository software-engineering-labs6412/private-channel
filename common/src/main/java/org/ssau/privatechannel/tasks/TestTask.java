package org.ssau.privatechannel.tasks;

import java.util.Date;
import java.util.TimerTask;

public class TestTask extends TimerTask {

    public void run(){
        System.out.println("Hello Mr Putin");
        System.out.println(new Date());
    }

}
