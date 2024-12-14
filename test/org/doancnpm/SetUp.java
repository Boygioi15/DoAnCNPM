package org.doancnpm;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;

public class SetUp {
    public static boolean isFXInitialized = false;

    public static void initFX() {
        if (!isFXInitialized) {
            // Khởi động JavaFX chỉ một lần
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isFXInitialized = true;
        }
    }


}
