package com.example.singhealthapp.auditor;

public class GetPhotoConcurrently implements Runnable {
    @Override
    public synchronized void run() {
        System.out.println("send intent");
        int result = getPhotoBitmap();
        System.out.println("result= "+result);
    }

    public synchronized int getPhotoBitmap() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
        return 1;
    }


}
