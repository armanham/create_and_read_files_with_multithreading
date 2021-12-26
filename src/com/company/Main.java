package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {

        createFiles();

        File[] files = readFiles();

        int length = files.length;

        File[] firstPart = new File[(length + 1) / 2];
        File[] secondPart = new File[length - firstPart.length];

        for (int i = 0; i < length; i++) {
            if (i < firstPart.length) {
                firstPart[i] = files[i];
            } else {
                secondPart[i - secondPart.length] = files[i];
            }
        }
        AtomicInteger atomicInteger = new AtomicInteger();

        List<Thread> threads = List.of(
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (File file : firstPart) {
                            atomicInteger.getAndAdd(calculateFileSum(file));
                        }
                    }
                }),
                new Thread(() -> {
                    for (File file : secondPart) {
                        atomicInteger.getAndAdd(calculateFileSum(file));
                    }
                })
        );
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(atomicInteger);
    }

    private static int calculateFileSum(File file) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String line;
            int sum = 0;
            while ((line = bufferedReader.readLine()) != null) {
                sum = sum + Integer.parseInt(line);
            }
            return sum;
        } catch (IOException ex) {
            throw new IllegalStateException("Calculating file sum was failed.", ex);
        } finally {

        }
    }

    private static File[] readFiles() {
        File folder = new File(FileUtils.BASE_PATH);
        if (!folder.exists()) {
            throw new IllegalStateException("Folder not found: " + FileUtils.BASE_PATH);
        }
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            throw new IllegalStateException("Empty folder: " + FileUtils.BASE_PATH);
        }
        return files;
    }

    private static void createFiles() {
        final CountDownLatch countDownLatch = new CountDownLatch(9);

        List<Worker> workers = List.of(new Worker(countDownLatch), new Worker(countDownLatch));

        workers.forEach(Thread::start);
        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Something went wrong during CDL await.", ex);
        }
    }
}
