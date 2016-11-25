package fsbench;

import com.google.common.base.Stopwatch;
import progressbar.ProgressBar;
import progressbar.ProgressBarStyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MigrationTask implements Callable<TaskResult> {

    private final static long KB = 1024;
    private final static long MB = KB * KB;
    private final static long GB = MB * KB;
    private final static long TB = GB * KB;

//    private final static long TOTAL_SPACE = 10 * MB;

    private final static long MIN_FILE_SIZE = 100;
    private final static long MAX_FILE_SIZE = 2 * KB;

    private final static int MAX_DIR_DEPTH = 5;

    private long totalSpace;

    public MigrationTask(long totalSpace) {
        this.totalSpace = totalSpace;
    }


    @Override
    public TaskResult call() throws Exception {
        long totalSize = 0;
        int totalFiles = 1;
        final long threadId = Thread.currentThread().getId();

        Stopwatch stopWatch = Stopwatch.createUnstarted();

//        ProgressBar pb = new ProgressBar("thread-" + threadId, 100, ProgressBarStyle.ASCII);
//        pb.start();

        // create thread directory
        Path dirPath = Paths.get(MigrationBenchmark.BASE_PATH, "thread-" + String.valueOf(threadId));
        Files.createDirectory(dirPath);

        while (totalSize < totalSpace) {

            byte[] bytes = randomFileBytes();
            InputStream is = new ByteArrayInputStream(bytes);

            Path filePath = Paths.get(dirPath.toString(), String.valueOf(totalFiles));

            stopWatch.start();
            Files.createFile(filePath);
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
            stopWatch.stop();

            totalSize += bytes.length;
            ++totalFiles;
//            pb.stepTo((int)(totalSize*100/totalSpace));
        }
//        pb.stop();

        TaskResult result = new TaskResult();
        result.threadId = threadId;
        result.seconds = stopWatch.elapsed(TimeUnit.SECONDS);
        result.milliseconds = stopWatch.elapsed(TimeUnit.MILLISECONDS);
        result.microseconds = stopWatch.elapsed(TimeUnit.MICROSECONDS);
        result.nanoseconds = stopWatch.elapsed(TimeUnit.NANOSECONDS);
        result.totalSize = totalSize;
        result.totalFiles = totalFiles;

        return result;
    }

    private static byte[] randomFileBytes() {
        long size = ThreadLocalRandom.current().nextLong(MIN_FILE_SIZE, MAX_FILE_SIZE + 1);
        assert size >= MIN_FILE_SIZE && size <= MAX_FILE_SIZE;
        byte[] bytes = new byte[(int)size];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static void main(String[] args) {
        long size = ThreadLocalRandom.current().nextLong(MIN_FILE_SIZE, MAX_FILE_SIZE + 1);
        System.out.println(size);
    }
}

class TaskResult {
    long threadId;
    long seconds;
    long milliseconds;
    long microseconds;
    long nanoseconds;
    long totalSize;
    long totalFiles;

    public long ops() {
        return totalFiles/seconds;
    }

    public long mbs() {
        return totalSize/seconds;
    }

    public long msop() {
        return milliseconds/totalFiles;
    }

    public String toString() {
        return  String.format("thread: %d %n", threadId) +
                String.format("total size: %d, total files: %d, time elapsed: %d s, %d ms, %d μs, %d ns %n", totalSize, totalFiles, seconds, milliseconds, microseconds, nanoseconds) +
                String.format("write/s: %d ops, throughout: %d mb/s, mean write time: %d ms/op %n", ops(), mbs(), msop());
    }
}
