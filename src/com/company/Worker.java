package com.company;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Worker extends Thread {

    private static final Random RANDOM = new Random();

    private final CountDownLatch countDownLatch;

    public Worker(final CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        while (this.countDownLatch.getCount() != 0) {
            Path path = Paths.get(FileUtils.BASE_PATH + "file_" + UUID.randomUUID() + ".txt");
            try {
                Files.write(path, lines(), StandardCharsets.UTF_8);
                this.countDownLatch.countDown();
            } catch (IOException ex) {
                throw new IllegalStateException("Something went wrong during file creation.", ex);
            }
        }
    }

    private static Collection<String> lines() {
        return List.of(String.valueOf(RANDOM.nextInt(1000)), String.valueOf(RANDOM.nextInt(1000)));
    }
}
