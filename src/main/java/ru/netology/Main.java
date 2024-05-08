package ru.netology;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    static BlockingQueue<String> queueA = new LinkedBlockingQueue<>(100);
    static BlockingQueue<String> queueB = new LinkedBlockingQueue<>(100);
    static BlockingQueue<String> queueC = new LinkedBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread generatorThread = new Thread(() -> {
            Random random = new Random();
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                queueA.offer(text);
                queueB.offer(text);
                queueC.offer(text);
            }
        });
        generatorThread.start();

        Thread threadA = new Thread(() -> {
            processQueue(queueA, 'a');
        });

        Thread threadB = new Thread(() -> {
            processQueue(queueB, 'b');
        });

        Thread threadC = new Thread(() -> {
            processQueue(queueC, 'c');
        });

        threadA.start();
        threadB.start();
        threadC.start();

        generatorThread.join();
        Thread.sleep(100);
        threadA.interrupt();
        threadB.interrupt();
        threadC.interrupt();
    }

    public static void processQueue(BlockingQueue<String> queue, char ch) {
        int maxCount = 0;
        String maxString = "";
        try {
            while (true) {
                String text = queue.take();
                int count = countChar(text, ch);
                if (count > maxCount) {
                    maxCount = count;
                    maxString = text;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }
        System.out.println("Max '" + ch + "' count: " + maxCount);
        System.out.println("Max '" + ch + "' string: " + maxString.substring(0, 100));
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countChar(String text, char ch) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
}